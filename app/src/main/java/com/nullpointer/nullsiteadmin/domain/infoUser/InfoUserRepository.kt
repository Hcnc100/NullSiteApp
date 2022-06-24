package com.nullpointer.nullsiteadmin.domain.infoUser

import com.nullpointer.nullsiteadmin.models.PersonalInfo
import kotlinx.coroutines.flow.Flow

interface InfoUserRepository {

    val myPersonalInfo:Flow<PersonalInfo>

    suspend fun updateAnyFieldUser(nameAdmin:String?,profession:String?,description:String?)
}