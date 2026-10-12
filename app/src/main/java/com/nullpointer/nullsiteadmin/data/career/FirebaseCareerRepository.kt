package com.nullpointer.nullsiteadmin.data.career

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.storage
import com.nullpointer.nullsiteadmin.domain.career.CareerRepository
import com.nullpointer.nullsiteadmin.exception.NullAppException
import com.nullpointer.nullsiteadmin.exception.InvalidResumePdf
import com.nullpointer.nullsiteadmin.exception.SkillsImportRequired
import com.nullpointer.nullsiteadmin.models.career.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID
import javax.inject.Inject

class FirebaseCareerRepository @Inject constructor(@ApplicationContext private val context: Context) : CareerRepository {
    private val db get() = Firebase.firestore
    private val settings get() = db.collection("siteSettings").document("public")
    private val skills get() = db.collection("skills")
    private suspend fun requireAdmin() {
        val user = Firebase.auth.currentUser ?: throw NullAppException.AdminRequired
        if (user.getIdToken(false).await().claims["admin"] != true) throw NullAppException.AdminRequired
    }
    override fun newSkillId(): String = skills.document().id
    override suspend fun load(): CareerSnapshot {
        requireAdmin()
        val config = settings.get(Source.SERVER).await()
        val docs = skills.get(Source.SERVER).await()
        requireAdmin()
        val raw = config.get("resume") as? Map<*, *>
        val resumes = CareerContract.locales.associateWith { locale ->
            @Suppress("UNCHECKED_CAST") val entry = raw?.get(locale) as? Map<String, Any?>
            ResumeContent(entry?.get("storagePath") as? String ?: "", entry?.get("fileName") as? String ?: "",
                (entry?.get("sizeBytes") as? Number)?.toLong() ?: 0, entry?.get("enabled") == true,
                CareerContract.canonical(entry))
        }
        return CareerSnapshot(docs.documents.map { CareerContract.readSkill(it.id, it.data.orEmpty()) }
            .sortedWith(compareBy({ it.sortOrder.toLongOrNull() ?: Long.MAX_VALUE }, { it.name }, { it.id })),
            config.getBoolean("skillsManaged") == true, resumes)
    }
    override suspend fun importSkills() {
        requireAdmin()
        db.runTransaction { tx ->
            if (tx.get(settings).getBoolean("skillsManaged") == true) return@runTransaction
            val targets = CareerContract.defaultSkills.map { skills.document(it.id) }
            val existing = targets.map { tx.get(it) }
            CareerContract.defaultSkills.forEachIndexed { index, skill -> if (!existing[index].exists()) {
                tx.set(targets[index], CareerContract.skillCreate(skill) + mapOf("createdAt" to FieldValue.serverTimestamp(), "updatedAt" to FieldValue.serverTimestamp()))
            } }
            tx.set(settings, mapOf("skillsManaged" to true), com.google.firebase.firestore.SetOptions.merge())
        }.await()
    }
    override suspend fun saveSkill(skill: SkillContent) {
        requireAdmin(); require(CareerContract.valid(skill)); require(skill.id.isNotBlank() && '/' !in skill.id)
        val target = skills.document(skill.id)
        db.runTransaction { tx ->
            val current = tx.get(target)
            if (tx.get(settings).getBoolean("skillsManaged") != true) throw SkillsImportRequired()
            CareerContract.assertSkillBaseline(current.data, skill.baseline)
            if (skill.baseline == null) tx.set(target, CareerContract.skillCreate(skill) + mapOf("createdAt" to FieldValue.serverTimestamp(), "updatedAt" to FieldValue.serverTimestamp()))
            else tx.update(target, CareerContract.skillPatch(skill) + mapOf("updatedAt" to FieldValue.serverTimestamp()))
        }.await()
    }
    override suspend fun deleteSkill(skill: SkillContent) {
        requireAdmin(); require(skill.baseline != null)
        val target = skills.document(skill.id)
        db.runTransaction { tx ->
            CareerContract.assertSkillBaseline(tx.get(target).data, skill.baseline)
            tx.delete(target)
        }.await()
    }
    override suspend fun uploadResume(locale: String, uri: Uri, progress: (Float) -> Unit): ResumeContent = withContext(Dispatchers.IO) {
        require(locale in CareerContract.locales); requireAdmin()
        val resolver = context.contentResolver
        val name = resolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use {
            if (it.moveToFirst()) it.getString(0) else null
        }.orEmpty()
        val mime = resolver.getType(uri)
        if (mime != "application/pdf" || !name.endsWith(".pdf", true)) throw InvalidResumePdf()
        val bytes = resolver.openInputStream(uri)?.use { input -> ByteArrayOutputStream().use { out ->
            val buffer = ByteArray(8192)
            while (true) {
                val count = input.read(buffer); if (count < 0) break
                if (out.size() + count > CareerContract.MAX_PDF_BYTES) throw InvalidResumePdf()
                out.write(buffer, 0, count)
            }
            out.toByteArray()
        } } ?: throw InvalidResumePdf()
        if (!CareerContract.validPdf(name, mime, bytes)) throw InvalidResumePdf()
        requireAdmin()
        val path = "resumes/$locale/${UUID.randomUUID()}.pdf"
        val fileName = "CV-$locale.pdf"
        val target = Firebase.storage.getReference(path)
        val task = target.putBytes(bytes, StorageMetadata.Builder().setContentType("application/pdf")
            .setContentDisposition("attachment; filename=\"$fileName\"").build())
        task.addOnProgressListener { progress(100f * it.bytesTransferred / it.totalByteCount.coerceAtLeast(1)) }
        try { task.await() } catch (e: Exception) {
            task.cancel()
            // Cleanup is deferred; cancellation must not wait on another network operation.
            throw e
        }
        ResumeContent(path, fileName, bytes.size.toLong(), enabled = true)
    }
    override suspend fun publishResume(locale: String, uploaded: ResumeContent, baseline: ResumeContent) {
        requireAdmin(); require(CareerContract.activeResume(locale, uploaded))
        db.runTransaction { tx ->
            val current = tx.get(settings)
            @Suppress("UNCHECKED_CAST") val entry = (current.get("resume") as? Map<*, *>)?.get(locale) as? Map<String, Any?>
            CareerContract.assertResumeBaseline(entry, baseline.baseline)
            val patch = mapOf("storagePath" to uploaded.storagePath, "fileName" to "CV-$locale.pdf", "sizeBytes" to uploaded.sizeBytes,
                "enabled" to true, "updatedAt" to FieldValue.serverTimestamp())
            if (current.exists()) tx.update(settings, mapOf("resume.$locale" to patch))
            else tx.set(settings, mapOf("resume" to mapOf(locale to patch)))
        }.await()
        // A failed acknowledgement can hide a successful commit. Never delete the new PDF on that path.
        if (CareerContract.validResumePath(locale, baseline.storagePath) && baseline.storagePath != uploaded.storagePath) {
            runCatching { Firebase.storage.getReference(baseline.storagePath).delete().await() }
        }
    }
    override suspend fun withdrawResume(locale: String, baseline: ResumeContent) {
        requireAdmin(); require(locale in CareerContract.locales)
        db.runTransaction { tx ->
            val current = tx.get(settings)
            @Suppress("UNCHECKED_CAST") val entry = (current.get("resume") as? Map<*, *>)?.get(locale) as? Map<String, Any?>
            CareerContract.assertResumeBaseline(entry, baseline.baseline)
            if (current.exists() && entry != null) tx.update(settings, mapOf("resume.$locale.enabled" to false))
        }.await()
    }
}
