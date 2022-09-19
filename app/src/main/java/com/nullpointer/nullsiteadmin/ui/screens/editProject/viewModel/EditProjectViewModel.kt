package com.nullpointer.nullsiteadmin.ui.screens.editProject.viewModel


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.core.delagetes.PropertySavableString
import com.nullpointer.nullsiteadmin.core.delagetes.SavableProperty
import com.nullpointer.nullsiteadmin.models.Project
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class EditProjectViewModel @Inject constructor(
    state: SavedStateHandle
) : ViewModel() {
    companion object {
        private const val KEY_PROJECT_SAVED = "KEY_PROJECT_SAVED"
        private const val MAX_LENGTH_NAME = 50
        private const val MAX_LENGTH_URL = 100
        private const val MAX_LENGTH_DESCRIPTION = 200

        private const val TAG_NAME_PROJECT = "TAG_NAME_PROJECT"
        private const val TAG_URL_IMG_PROJECT = "TAG_URL_IMG_PROJECT"
        private const val TAG_URL_REPO_PROJECT = "TAG_URL_REPO_PROJECT"
        private const val TAG_DESCRIPTION_PROJECT = "TAG_DESCRIPTION_PROJECT"
    }

    private var initProject by SavableProperty<Project?>(state, KEY_PROJECT_SAVED, null)

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

    val isDataValid: Boolean
        get() = !nameProject.hasError &&
                !descriptionProject.hasError &&
                !urlRepositoryProject.hasError &&
                !urlImgProject.hasError

    private val hasAnyChange
        get() = initProject?.name != nameProject.currentValue ||
                initProject?.description != descriptionProject.currentValue ||
                initProject?.urlImg != urlImgProject.currentValue ||
                initProject?.urlRepo != urlRepositoryProject.currentValue

    fun initVM(project: Project) {
        initProject = project
        project.let {
            nameProject.changeValue(it.name)
            descriptionProject.changeValue(it.description)
            urlRepositoryProject.changeValue(it.urlRepo)
            urlImgProject.changeValue(it.urlImg)
        }
    }


    fun getUpdatedProject(): Project? {
        return if (hasAnyChange) {
            initProject?.copy(
                name = nameProject.currentValue,
                urlImg = urlImgProject.currentValue,
                urlRepo = urlRepositoryProject.currentValue,
                description = descriptionProject.currentValue
            )
        } else {
            _messageError.trySend(R.string.error_no_data_change)
            null
        }

    }
}