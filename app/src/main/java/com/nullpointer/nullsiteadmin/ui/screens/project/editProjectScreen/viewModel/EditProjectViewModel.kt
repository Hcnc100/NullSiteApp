package com.nullpointer.nullsiteadmin.ui.screens.project.editProjectScreen.viewModel


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.core.delagetes.PropertySavableString
import com.nullpointer.nullsiteadmin.core.utils.launchSafeIO
import com.nullpointer.nullsiteadmin.domain.project.ProjectRepository
import com.nullpointer.nullsiteadmin.models.project.data.ProjectData
import com.nullpointer.nullsiteadmin.models.project.wrapper.UpdateProjectWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class EditProjectViewModel @Inject constructor(
    state: SavedStateHandle,
    private val projectRepository: ProjectRepository
) : ViewModel() {
    companion object {
        private const val MAX_LENGTH_NAME = 50
        private const val MAX_LENGTH_URL = 100
        private const val MAX_LENGTH_DESCRIPTION = 200

        private const val TAG_NAME_PROJECT = "TAG_NAME_PROJECT"
        private const val TAG_URL_IMG_PROJECT = "TAG_URL_IMG_PROJECT"
        private const val TAG_URL_REPO_PROJECT = "TAG_URL_REPO_PROJECT"
        private const val TAG_DESCRIPTION_PROJECT = "TAG_DESCRIPTION_PROJECT"
    }

    val nameProject = PropertySavableString(
        savedState = state,
        label = R.string.label_name_project,
        hint = R.string.hint_name_project,
        emptyError = R.string.error_empty_name_project,
        lengthError = R.string.error_length_name_project,
        maxLength = MAX_LENGTH_NAME,
        tagSavable = TAG_NAME_PROJECT
    )

    val descriptionProject = PropertySavableString(
        savedState = state,
        label = R.string.label_description_project,
        hint = R.string.hint_description_project,
        emptyError = R.string.error_description_project,
        lengthError = R.string.error_length_description_project,
        maxLength = MAX_LENGTH_DESCRIPTION,
        tagSavable = TAG_DESCRIPTION_PROJECT
    )

    val urlRepositoryProject = PropertySavableString(
        savedState = state,
        label = R.string.label_repo_project,
        hint = R.string.hint_repo_project,
        emptyError = R.string.error_empty_link_project,
        lengthError = R.string.error_length_link_project,
        maxLength = MAX_LENGTH_URL,
        tagSavable = TAG_URL_REPO_PROJECT
    )

    val urlImgProject = PropertySavableString(
        savedState = state,
        label = R.string.label_img_project,
        hint = R.string.hint_img_project,
        emptyError = R.string.error_empty_link_project,
        lengthError = R.string.error_length_link_project,
        maxLength = MAX_LENGTH_URL,
        tagSavable = TAG_URL_IMG_PROJECT
    )


    private val _messageError = Channel<Int>()
    val messageError = _messageError.receiveAsFlow()


    var isUpdatedProject by mutableStateOf(false)
        private set


    val isDataValid: Boolean
        get() = !nameProject.hasError &&
                !descriptionProject.hasError &&
                !urlRepositoryProject.hasError &&
                !urlImgProject.hasError

    private val hasAnyChange
        get() = nameProject.hasChanged ||
                descriptionProject.hasChanged ||
                urlImgProject.hasChanged ||
                urlRepositoryProject.hasChanged

    fun initVM(projectData: ProjectData) {
        projectData.let {
            nameProject.setDefaultValue(it.name)
            urlImgProject.setDefaultValue(it.urlImg)
            urlRepositoryProject.setDefaultValue(it.urlRepo)
            descriptionProject.setDefaultValue(it.description)
        }
    }


    fun updatedProject(
        currentProjectData: ProjectData,
        actionSuccess: () -> Unit
    ) = launchSafeIO(
        blockBefore = { isUpdatedProject = true },
        blockAfter = { isUpdatedProject = false }
    ) {
        when {
            !isDataValid ->
                _messageError.trySend(R.string.error_invalid_data)

            !hasAnyChange ->
                _messageError.trySend(R.string.error_no_data_change)

            else -> {
                val updateProjectWrapper = UpdateProjectWrapper(
                    name = nameProject.currentValue,
                    urlImg = urlImgProject.currentValue,
                    urlRepo = urlRepositoryProject.currentValue,
                    description = descriptionProject.currentValue,
                    idProject = currentProjectData.idProject,
                    isVisible = currentProjectData.isVisible
                )
                projectRepository.editProject(updateProjectWrapper)
                _messageError.trySend(R.string.message_data_upload)
                delay(2000)
                withContext(Dispatchers.Main) {
                    actionSuccess()
                }
            }
        }

    }
}