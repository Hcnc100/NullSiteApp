package com.nullpointer.nullsiteadmin.ui.screens.editProject.viewModel


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.nullpointer.nullsiteadmin.core.delagetes.SavableComposeState
import com.nullpointer.nullsiteadmin.core.delagetes.SavableProperty
import com.nullpointer.nullsiteadmin.core.utils.validateError
import com.nullpointer.nullsiteadmin.models.Project
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
        private const val KEY_DESCRIPTION_PROJECT = "KEY_DESCRIPTION_PROJECT"
        private const val KEY_NAME_PROJECT = "KEY_NAME_PROJECT"
        private const val KEY_URL_REPO = "KEY_URL_REPO"
        private const val KEY_URL_IMG = "KEY_URL_IMG"

        private const val MAX_LENGTH_NAME = 50
        private const val MAX_LENGTH_URL = 100
        private const val MAX_LENGTH_DESCRIPTION = 200
    }

    private var initProject by SavableProperty<Project?>(state, KEY_PROJECT_SAVED, null)

    var name by SavableComposeState(state, KEY_NAME_PROJECT, "")
        private set
    var errorName by SavableComposeState(state, "", "")
        private set
    val nameLength get() = "${name.length}/$MAX_LENGTH_NAME"

    var description by SavableComposeState(state, KEY_DESCRIPTION_PROJECT, "")
        private set
    var errorDescription by SavableComposeState(state, "", "")
        private set
    val descriptionLength get() = "${description.length}/$MAX_LENGTH_DESCRIPTION"

    var urlRepository by SavableComposeState(state, KEY_URL_REPO, "")
        private set
    var errorUrlRepo by SavableComposeState(state, "", "")
        private set
    val urlRepoLength get() = "${urlRepository.length}/$MAX_LENGTH_URL"

    var urlImage by SavableComposeState(state, KEY_URL_IMG, "")
        private set
    var errorUrlImage by SavableComposeState(state, "", "")
        private set
    val urlImgLength get() = "${urlImage.length}/$MAX_LENGTH_URL"

    private val _messageError = Channel<String>()
    val messageError = _messageError.consumeAsFlow()

    val isSaveEnable: Boolean
        get() = errorName.isEmpty() &&
                errorDescription.isEmpty() &&
                errorUrlImage.isEmpty() &&
                errorUrlRepo.isEmpty()
    private val hasAnyChange
        get() = initProject?.description != description ||
                initProject?.name != name ||
                initProject?.urlImg != urlImage ||
                initProject?.urlRepo != urlRepository

    fun initVM(project: Project) {
        initProject = project
        project.let {
            name = it.name
            description = it.description
            urlRepository = it.urlRepo
            urlImage = it.urlImg
        }
    }

    fun updateName(newName: String) {
        name = newName
        validateError(
            name,
            MAX_LENGTH_NAME,
            "El nombre es requerido",
            "El nombre no puede ser tan largo"
        )
    }

    fun updateDescription(newDescription: String) {
        description = newDescription
        validateError(
            description,
            MAX_LENGTH_DESCRIPTION,
            "La description es requerido",
            "La description no puede ser tan larga"
        )
    }

    fun updateUrlRepository(newUrlRepository: String) {
        urlRepository = newUrlRepository
        validateError(
            KEY_URL_REPO,
            MAX_LENGTH_URL,
            "La url es requerido",
            "La url no puede ser tan largo"
        )
    }

    fun updateUrlImage(newUrlImage: String) {
        urlImage = newUrlImage
        validateError(
            KEY_URL_IMG,
            MAX_LENGTH_URL,
            "La url de la imagen es requerido",
            "La url no puede ser tan larga"
        )
    }



    fun getUpdatedProject(): Project? {
        return if (hasAnyChange) {
            initProject?.copy(
                name = this.name,
                description = this.description,
                urlRepo = this.urlRepository,
                urlImg = this.urlImage
            )
        } else {
            _messageError.trySend("No hay cambios que guardar")
            null
        }

    }
}