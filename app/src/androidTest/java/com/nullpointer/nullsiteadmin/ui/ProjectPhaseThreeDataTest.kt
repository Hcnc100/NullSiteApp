package com.nullpointer.nullsiteadmin.ui

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.nullpointer.nullsiteadmin.database.*
import com.nullpointer.nullsiteadmin.domain.image.ProjectImageUploader
import com.nullpointer.nullsiteadmin.domain.project.ProjectRepository
import com.nullpointer.nullsiteadmin.models.project.data.*
import com.nullpointer.nullsiteadmin.models.project.entity.ProjectEntity
import com.nullpointer.nullsiteadmin.models.project.wrapper.*
import com.nullpointer.nullsiteadmin.ui.screens.project.editProjectScreen.viewModel.EditProjectViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.*
import org.junit.Test
import java.util.UUID
import java.util.Date

internal class ProjectQaRepository : ProjectRepository {
    var saved: UpdateProjectWrapper? = null
    override val listProjects = flowOf(emptyList<ProjectData>())
    override suspend fun editProject(updateProjectWrapper: UpdateProjectWrapper) { saved = updateProjectWrapper }
    override suspend fun insertProject(createProjectWrapper: CreateProjectWrapper) = error("No remote writes")
    override suspend fun deleterListProjectById(listIdProject: List<String>) = error("No deletes")
    override suspend fun deleterProjectById(idProject: String) = error("No deletes")
    override suspend fun requestLastProject() = 0
    override suspend fun concatenateProjects() = 0
}

class ProjectPhaseThreeDataTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val project = ProjectData("qa", "Project", "Description", "https://example.com/cover.png")
    private fun main(block: () -> Unit) = InstrumentationRegistry.getInstrumentation().runOnMainSync(block)

    @Test fun migrationFiveToSixPreservesProjectsEmailsAndEnrichedRoundTrip() = runBlocking {
        val name = "migration-${UUID.randomUUID()}.db"
        val helper = object : SQLiteOpenHelper(context, name, null, 5) {
            override fun onCreate(db: SQLiteDatabase) {
                db.execSQL("CREATE TABLE projects (idProject TEXT NOT NULL PRIMARY KEY, name TEXT NOT NULL, description TEXT NOT NULL, urlImg TEXT NOT NULL, urlRepo TEXT NOT NULL, createdAt INTEGER NOT NULL, updatedAt INTEGER NOT NULL, isVisible INTEGER NOT NULL)")
                db.execSQL("CREATE TABLE emails (id TEXT NOT NULL PRIMARY KEY, name TEXT NOT NULL, message TEXT NOT NULL, email TEXT NOT NULL, subject TEXT NOT NULL, createAt INTEGER NOT NULL, updateAt INTEGER NOT NULL, isOpen INTEGER NOT NULL)")
                db.execSQL("INSERT INTO projects VALUES ('old','Old project','Description','https://example.com/img','',1,2,1)")
                db.execSQL("INSERT INTO emails VALUES ('mail','Sender','Message','qa@example.com','Subject',3,4,0)")
            }
            override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = error("Room must migrate")
        }
        helper.writableDatabase; helper.close()
        val db = Room.databaseBuilder(context, NullSiteDatabase::class.java, name).addMigrations(PROJECT_CONTENT_MIGRATION).build()
        try {
            val old = db.getProjectDao().getLastProject()!!
            assertEquals("Old project", old.name); assertTrue(old.isVisible); assertEquals(Date(1), old.createdAt)
            assertEquals(ProjectContent(), ProjectData.fromProjectEntity(old).content)
            db.openHelper.readableDatabase.query("SELECT message FROM emails WHERE id='mail'").use { assertTrue(it.moveToFirst()); assertEquals("Message", it.getString(0)) }
            val enriched = ProjectData.fromProjectEntity(old).copy(content = ProjectContent(urlPlay = "https://example.com/demo",
                galleryItems = listOf(ProjectImage("https://example.com/a", alt = "Captura", translations = mapOf("en" to mapOf("alt" to "Screenshot"))))))
            db.getProjectDao().updateProject(ProjectEntity.fromProjectData(enriched))
            assertEquals(enriched, ProjectData.fromProjectEntity(db.getProjectDao().getLastProject()!!))
        } finally { db.close(); context.deleteDatabase(name) }
    }

    @Test fun partialUploadFailureRetriesOnlyPendingAndNeverSavesDocument() = runBlocking {
        val calls = mutableListOf<String>(); var fail = true
        val uploader = object : ProjectImageUploader {
            override suspend fun upload(projectId: String, uri: Uri, progress: (Float) -> Unit): ProjectImage {
                calls.add(uri.toString()); progress(50f)
                if (uri.toString().endsWith("b") && fail) throw IllegalStateException("offline")
                return ProjectImage("https://example.com/${uri.lastPathSegment}.png")
            }
        }
        val repo = ProjectQaRepository(); val saved = SavedStateHandle(); lateinit var vm: EditProjectViewModel
        main { vm = EditProjectViewModel(saved, repo, uploader); vm.initVM(project); vm.enqueueImages(listOf(Uri.parse("content://qa/a"), Uri.parse("content://qa/b"))) }
        withTimeout(5000) { while (vm.isUploading) delay(20) }
        main {
            assertEquals(listOf("https://example.com/a.png"), vm.contentEditor.content.gallery)
            assertEquals(listOf("content://qa/b"), vm.pendingUploads); assertFalse(vm.isDataValid); assertNull(repo.saved)
            fail = false; vm.retryUploads()
        }
        withTimeout(5000) { while (vm.isUploading) delay(20) }
        main {
            assertEquals(listOf("content://qa/a", "content://qa/b", "content://qa/b"), calls)
            assertEquals(2, vm.contentEditor.images.size); assertTrue(vm.pendingUploads.isEmpty()); assertTrue(vm.isDataValid)
            val restored = EditProjectViewModel(SavedStateHandle(saved.keys().associateWith { saved.get<Any?>(it) }), repo, uploader)
            restored.initVM(project.copy(name = "Remote")); assertEquals(vm.contentEditor.content, restored.contentEditor.content)
            assertEquals("Project", restored.nameProject.currentValue); assertTrue(restored.hasUnsavedChanges)
        }
    }

    @Test fun cancelledUploadKeepsPendingAndDoesNotPublish() = runBlocking {
        val started = CompletableDeferred<Unit>()
        val uploader = object : ProjectImageUploader {
            override suspend fun upload(projectId: String, uri: Uri, progress: (Float) -> Unit): ProjectImage {
                started.complete(Unit); awaitCancellation()
            }
        }
        lateinit var vm: EditProjectViewModel; val repo = ProjectQaRepository()
        main { vm = EditProjectViewModel(SavedStateHandle(), repo, uploader); vm.initVM(project); vm.enqueueImages(listOf(Uri.parse("content://qa/a"))) }
        withTimeout(5000) { started.await() }; main { vm.cancelUploads() }
        withTimeout(5000) { while (vm.isUploading) delay(20) }
        main { assertEquals(1, vm.pendingUploads.size); assertTrue(vm.contentEditor.images.isEmpty()); assertNull(repo.saved); vm.discardPendingUploads(); assertTrue(vm.pendingUploads.isEmpty()) }
    }
}
