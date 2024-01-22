package com.nullpointer.nullsiteadmin.datasource.infoPhone.local

import com.nullpointer.nullsiteadmin.data.infoPhone.local.CurrentInfoPhone
import com.nullpointer.nullsiteadmin.data.infoPhone.local.InfoPhoneDataStore
import com.nullpointer.nullsiteadmin.models.phoneInfo.data.InfoPhoneData
import kotlinx.coroutines.flow.first

class InfoPhoneLocalDataSourceImpl(
    private val currentInfoPhone: CurrentInfoPhone,
    private val infoPhoneDataStore: InfoPhoneDataStore,
) : InfoPhoneLocalDataSource {
    override suspend fun getSavedInfoPhone(): InfoPhoneData? =
        infoPhoneDataStore.getCurrentInfoPhoneSaved().first()

    override suspend fun getCurrentInfoPhone(): InfoPhoneData =
        currentInfoPhone.getCurrentInfoPhone()

    override suspend fun updateSavedData(updateCurrentData: InfoPhoneData) =
        infoPhoneDataStore.updateInfoPhone(updateCurrentData)

    override suspend fun deleterSavedInfoPhone() =
        infoPhoneDataStore.deleterData()


}