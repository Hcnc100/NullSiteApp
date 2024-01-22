package com.nullpointer.nullsiteadmin.ui.screens.project.componets.lists

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.models.project.data.ProjectData
import com.nullpointer.nullsiteadmin.ui.screens.animation.AnimationScreen
import com.nullpointer.nullsiteadmin.ui.screens.project.componets.items.ProjectItem
import com.nullpointer.nullsiteadmin.ui.screens.project.componets.items.ProjectItemLoading
import com.nullpointer.nullsiteadmin.ui.share.LazyListConcatenate
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer

@Composable
fun ListLoadProject(modifier: Modifier = Modifier) {
    val shimmer = rememberShimmer(shimmerBounds = ShimmerBounds.View)
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(
            count = 5,
            key = { it }
        ) {
            ProjectItemLoading(shimmer = shimmer)
        }
    }
}

@Composable
fun ListErrorProject(
    modifier: Modifier = Modifier
) {
    AnimationScreen(
        modifier = modifier,
        animation = R.raw.error,
        textEmpty = stringResource(R.string.message_error_project)
    )
}

@Composable
fun ListEmptyProject(
    modifier: Modifier = Modifier
) {
    AnimationScreen(
        modifier = modifier,
        animation = R.raw.empty1,
        textEmpty = stringResource(R.string.message_empty_project)
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListProjectSuccess(
    isConcatenate: Boolean,
    listProjectData: List<ProjectData>,
    modifier: Modifier = Modifier,
    concatenateProject: () -> Unit,
    actionEditProject: (ProjectData) -> Unit,
    lazyListState: LazyListState = rememberLazyListState()
) {
    LazyListConcatenate(
        modifier = modifier,
        isConcatenate = isConcatenate,
        lazyListState = lazyListState,
        actionConcatenate = concatenateProject,
        paddingValues = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(
            items = listProjectData,
            key = { it.idProject }
        ) {
            ProjectItem(
                projectData = it,
                actionEditProject = actionEditProject,
                modifier = Modifier.animateItemPlacement()
            )
        }
    }
}