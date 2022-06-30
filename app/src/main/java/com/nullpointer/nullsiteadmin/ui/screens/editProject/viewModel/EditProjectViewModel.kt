package com.nullpointer.nullsiteadmin.ui.screens.editProject.viewModel


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.core.delagetes.SavableProperty
import com.nullpointer.nullsiteadmin.models.Project
import com.nullpointer.nullsiteadmin.models.PropertySavableString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
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
    }

    private var initProject by SavableProperty<Project?>(state, KEY_PROJECT_SAVED, null)

    val nameProject = PropertySavableString(
        state = state,
        label = R.string.label_name_project,
        hint = R.string.hint_name_project,
        emptyError = R.string.error_empty_name_project,
        lengthError = R.string.error_length_name_project,
        maxLength = MAX_LENGTH_NAME
    )

    val descriptionProject = PropertySavableString(
        state = state,
        label = R.string.label_description_project,
        hint = R.string.hint_description_project,
        emptyError = R.string.error_description_project,
        lengthError = R.string.error_length_description_project,
        maxLength = MAX_LENGTH_DESCRIPTION
    )

    val urlRepositoryProject = PropertySavableString(
        state = state,
        label = R.string.label_repo_project,
        hint = R.string.hint_repo_project,
        emptyError = R.string.error_empty_link_project,
        lengthError = R.string.error_length_link_project,
        maxLength = MAX_LENGTH_URL
    )

    val urlImgProject = PropertySavableString(
        state = state,
        label = R.string.label_img_project,
        hint = R.string.hint_img_project,
        emptyError = R.string.error_empty_link_project,
        lengthError = R.string.error_length_link_project,
        maxLength = MAX_LENGTH_URL
    )


    private val _messageError = Channel<String>()
    val messageError = _messageError.consumeAsFlow()

    val isDataValid: Boolean
        get() = !nameProject.hasError &&
                !descriptionProject.hasError &&
                !urlRepositoryProject.hasError &&
                !urlImgProject.hasError

    private val hasAnyChange
        get() = initProject?.name != nameProject.value ||
                initProject?.description != descriptionProject.value ||
                initProject?.urlImg != urlImgProject.value ||
                initProject?.urlRepo != urlRepositoryProject.value

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
                name = nameProject.value,
                description = descriptionProject.value,
                urlRepo = urlRepositoryProject.value,
                urlImg = urlImgProject.value
            )
        } else {
            _messageError.trySend("No hay cambios que guardar")
            null
        }

    }
}