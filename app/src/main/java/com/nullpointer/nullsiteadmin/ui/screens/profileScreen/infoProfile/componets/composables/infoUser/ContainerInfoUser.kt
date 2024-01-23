package com.nullpointer.nullsiteadmin.ui.screens.profileScreen.infoProfile.componets.composables.infoUser

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        FieldInfoPersonal(
            nameFiled = stringResource(id = R.string.label_name_admin),
            valueFiled = personalInfoData.name
        )
        FieldInfoPersonal(
            nameFiled = stringResource(id = R.string.label_profession_admin),
            valueFiled = personalInfoData.profession
        )
        FieldInfoPersonal(
            nameFiled = stringResource(id = R.string.label_description_admin),
            valueFiled = personalInfoData.description
        )
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
            style = MaterialTheme.typography.h6,
            fontSize = 12.sp,
        )
        Text(
            text = valueFiled,
            style = MaterialTheme.typography.body1,
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