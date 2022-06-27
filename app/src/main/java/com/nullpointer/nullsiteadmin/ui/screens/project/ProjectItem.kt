package com.nullpointer.nullsiteadmin.ui.screens.project

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.models.Project

@Composable
fun ProjectItem(
    project: Project,
    actionEditProject: (Project) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.padding(8.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = project.name, style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.height(10.dp))
            AsyncImage(
                model = project.urlImg,
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(250.dp)
                    .aspectRatio(1f)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = project.description, style = MaterialTheme.typography.body1)
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = { actionEditProject(project) }) {
                Row {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_edit),
                        contentDescription = ""
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("Editar")
                }
            }
        }
    }
}