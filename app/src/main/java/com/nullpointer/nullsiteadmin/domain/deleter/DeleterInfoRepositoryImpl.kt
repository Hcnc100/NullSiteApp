package com.nullpointer.nullsiteadmin.domain.deleter

import com.nullpointer.nullsiteadmin.data.local.email.EmailLocalDataSource
import com.nullpointer.nullsiteadmin.data.local.infoUser.InfoUserLocalDataSource
import com.nullpointer.nullsiteadmin.data.local.project.ProjectLocalDataSource
import com.nullpointer.nullsiteadmin.data.local.settings.SettingsDataSource

class DeleterInfoRepositoryImpl(
    private val settingsDataSource: SettingsDataSource,
    private val infoUserLocalDataSource: InfoUserLocalDataSource,
    private val emailLocalDataSource: EmailLocalDataSource,
    private val projectLocalDataSource: ProjectLocalDataSource
) : DeleterInfoRepository {
    override suspend fun deleterAllInformation() {
        settingsDataSource.clearData()
        emailLocalDataSource.deleterAllEmails()
        projectLocalDataSource.deleterAllProjects()
        infoUserLocalDataSource.deleterPersonalInfo()
    }

}