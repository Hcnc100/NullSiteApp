package com.nullpointer.nullsiteadmin.ui.preview.provider

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.nullpointer.nullsiteadmin.core.states.Resource
import com.nullpointer.nullsiteadmin.models.project.data.ProjectData

class ListProjectProvider : PreviewParameterProvider<Resource<List<ProjectData>>> {
    override val values = listOf(
        Resource.Loading,
        Resource.Failure,
        Resource.Success(emptyList()),
        Resource.Success(ProjectData.exampleList)
    ).asSequence()
}