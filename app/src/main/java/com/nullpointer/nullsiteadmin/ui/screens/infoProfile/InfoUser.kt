package com.nullpointer.nullsiteadmin.ui.screens.infoProfile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.models.PersonalInfo

@Composable
fun InfoUser(
    modifier: Modifier = Modifier,
    personalInfo: PersonalInfo,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PhotoProfile(urlImgProfile = personalInfo.urlImg)
        ListInfoPersonal(
            personalInfo = personalInfo,
            modifier = Modifier.padding(10.dp)
        )
    }
}

@Composable
private fun ListInfoPersonal(
    personalInfo: PersonalInfo,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        FieldInfoPersonal(
            nameFiled = stringResource(id = R.string.label_name_admin),
            valueFiled = personalInfo.name
        )
        Spacer(modifier = Modifier.height(10.dp))
        FieldInfoPersonal(
            nameFiled = stringResource(id = R.string.label_profession_admin),
            valueFiled = personalInfo.profession
        )
        Spacer(modifier = Modifier.height(10.dp))
        FieldInfoPersonal(
            nameFiled = stringResource(id = R.string.label_description_admin),
            valueFiled = personalInfo.description
        )
    }
}

@Composable
private fun FieldInfoPersonal(
    nameFiled: String,
    valueFiled: String,
) {
    Column {
        Text(text = nameFiled, style = MaterialTheme.typography.h6, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(5.dp))
        Text(text = valueFiled, style = MaterialTheme.typography.body1)
    }
}

@Composable
private fun PhotoProfile(
    modifier: Modifier = Modifier,
    urlImgProfile: String,
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Box {
            AsyncImage(
                model = urlImgProfile,
                contentDescription = stringResource(id = R.string.description_current_img_profile),
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
            )
        }
    }
}
