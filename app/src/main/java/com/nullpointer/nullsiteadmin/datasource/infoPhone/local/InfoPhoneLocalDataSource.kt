package com.nullpointer.nullsiteadmin.datasource.infoPhone.local

import com.nullpointer.nullsiteadmin.models.phoneInfo.data.InfoPhoneData

interface InfoPhoneLocalDataSource {
    suspend fun getSavedInfoPhone(): InfoPhoneData?
    suspend fun getCurrentInfoPhone(): InfoPhoneData
    suspend fun updateSavedData(updateCurrentData: InfoPhoneData)
    suspend fun deleterSavedInfoPhone()
}