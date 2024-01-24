package com.nullpointer.nullsiteadmin.ui.screens.project.projectScreen.componets.lists

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nullpointer.nullsiteadmin.models.project.data.ProjectData
import com.nullpointer.nullsiteadmin.ui.screens.project.projectScreen.componets.projectItem.ProjectItem
import com.nullpointer.nullsiteadmin.ui.share.CircularProgressAnimation
import com.nullpointer.runningcompose.ui.preview.config.OrientationPreviews

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListProjectSuccess(
    isConcatenate: Boolean,
    lazyGridState: LazyGridState,
    modifier: Modifier = Modifier,
    listProjectData: List<ProjectData>,
    actionEditProject: (ProjectData) -> Unit,
) {

    Box {
        LazyVerticalGrid(
            modifier = modifier.fillMaxSize(),
            state = lazyGridState,
            contentPadding = PaddingValues(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            columns = GridCells.Adaptive(minSize = 280.dp),
            content = {
                items(
                    count = listProjectData.size,
                    key = { listProjectData[it].idProject }
                ) {
                    ProjectItem(
                        projectData = listProjectData[it],
                        actionEditProject = actionEditProject,
                        modifier = Modifier.animateItemPlacement()
                    )
                }
            })
        CircularProgressAnimation(
            isVisible = isConcatenate,
            modifier = Modifier
                .padding(15.dp)
                .align(Alignment.BottomCenter)
        )
    }
}

@OrientationPreviews
@Composable
private fun ListProjectSuccessPreview() {
    ListProjectSuccess(
        isConcatenate = false,
        lazyGridState = rememberLazyGridState(),
        listProjectData = ProjectData.exampleList,
        actionEditProject = {}
    )
}

