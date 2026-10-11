package com.nullpointer.nullsiteadmin.ui.screens.project.projectScreen.componets.projectItem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Text(
                text = projectData.name,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            ImageProject(urlImg = projectData.urlImg)
            Text(
                text = projectData.description,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.heightIn(min = 80.dp)
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
