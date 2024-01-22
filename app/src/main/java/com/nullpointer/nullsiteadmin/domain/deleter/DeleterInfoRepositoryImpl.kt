package com.nullpointer.nullsiteadmin.domain.deleter

import com.nullpointer.nullsiteadmin.datasource.email.local.EmailLocalDataSource
import com.nullpointer.nullsiteadmin.datasource.user.local.InfoUserLocalDataSource
import com.nullpointer.nullsiteadmin.datasource.project.local.ProjectLocalDataSource
import com.nullpointer.nullsiteadmin.datasource.settings.local.SettingsLocalDataSource

class DeleterInfoRepositoryImpl(
    private val settingsLocalDataSource: SettingsLocalDataSource,
    private val infoUserLocalDataSource: InfoUserLocalDataSource,
    private val emailLocalDataSource: EmailLocalDataSource,
    private val projectLocalDataSource: ProjectLocalDataSource
) : DeleterInfoRepository {
    override suspend fun deleterAllInformation() {
        settingsLocalDataSource.clearData()
        emailLocalDataSource.deleterAllEmails()
        projectLocalDataSource.deleterAllProjects()
        infoUserLocalDataSource.deleterPersonalInfo()
    }

}