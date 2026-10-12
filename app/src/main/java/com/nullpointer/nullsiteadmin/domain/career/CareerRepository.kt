package com.nullpointer.nullsiteadmin.domain.career

import android.net.Uri
import com.nullpointer.nullsiteadmin.models.career.*

interface CareerRepository {
    suspend fun load(): CareerSnapshot
    fun newSkillId(): String
    suspend fun importSkills()
    suspend fun saveSkill(skill: SkillContent)
    suspend fun deleteSkill(skill: SkillContent)
    suspend fun uploadResume(locale: String, uri: Uri, progress: (Float) -> Unit): ResumeContent
    suspend fun publishResume(locale: String, uploaded: ResumeContent, baseline: ResumeContent)
    suspend fun withdrawResume(locale: String, baseline: ResumeContent)
}
