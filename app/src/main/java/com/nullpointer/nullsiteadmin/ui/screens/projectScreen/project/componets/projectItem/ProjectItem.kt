package com.nullpointer.nullsiteadmin.ui.screens.projectScreen.project.componets.projectItem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nullpointer.nullsiteadmin.models.project.data.ProjectData
import com.nullpointer.nullsiteadmin.ui.preview.config.SimplePreview

@Composable
fun ProjectItem(
    projectData: ProjectData,
    modifier: Modifier = Modifier,
    actionEditProject: (ProjectData) -> Unit,
) {
    Card(
        elevation = 10.dp,
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Text(
                text = projectData.name,
                style = MaterialTheme.typography.h6,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            ImageProject(urlImg = projectData.urlImg)
            Text(
                text = projectData.description,
                style = MaterialTheme.typography.body1,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.height(80.dp)
            )
            ButtonEditProject(
                actionClick = { actionEditProject(projectData) }
            )
        }
    }
}


@SimplePreview
@Composable
private fun ProjectItemPreview() {
    ProjectItem(
        projectData = ProjectData.example,
        actionEditProject = { /*TODO*/ },
    )
}