package com.nullpointer.nullsiteadmin.ui.screens.profile.infoProfile.componets.composables.infoUser

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import com.nullpointer.nullsiteadmin.ui.screens.profile.editInfoProfile.components.ProfileLocaleSelector
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.models.personalInfo.data.PersonalInfoData
import com.nullpointer.nullsiteadmin.ui.preview.config.SimplePreview


@Composable
fun ContainerInfoUser(
    modifier: Modifier = Modifier,
    personalInfoData: PersonalInfoData
) {
    var locale by rememberSaveable { mutableStateOf("es") }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        ProfileLocaleSelector(locale = locale, onSelect = { locale = it })
        ProfileContentPreview(profile = personalInfoData, locale = locale)
    }
}

@SimplePreview
@Composable
fun PreviewContainerInfoUser() {
    ContainerInfoUser(
        personalInfoData = PersonalInfoData.example
    )
}


@Composable
private fun FieldInfoPersonal(
    nameFiled: String,
    valueFiled: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = nameFiled,
            style = MaterialTheme.typography.titleLarge,
            fontSize = 12.sp,
        )
        Text(
            text = valueFiled,
            style = MaterialTheme.typography.bodyLarge,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@SimplePreview
@Composable
private fun FieldInfoPersonalPreview() {
    FieldInfoPersonal(
        nameFiled = "Nombre",
        valueFiled = "Null Pointer"
    )
}
