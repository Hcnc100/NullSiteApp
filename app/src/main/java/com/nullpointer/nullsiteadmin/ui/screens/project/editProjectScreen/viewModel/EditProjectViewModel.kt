package com.nullpointer.nullsiteadmin.ui.screens.project.editProjectScreen.viewModel


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.core.delagetes.PropertySavableString
import com.nullpointer.nullsiteadmin.core.utils.ExceptionManager
import com.nullpointer.nullsiteadmin.core.utils.FormLengthLimits
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
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import android.net.Uri
import com.nullpointer.nullsiteadmin.core.delagetes.SavableComposeState
import com.nullpointer.nullsiteadmin.domain.image.ProjectImageUploader
import com.nullpointer.nullsiteadmin.domain.image.UnavailableProjectImageUploader
import com.nullpointer.nullsiteadmin.models.project.data.ProjectContentContract
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException

@HiltViewModel
class EditProjectViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val projectRepository: ProjectRepository,
    private val imageUploader: ProjectImageUploader = UnavailableProjectImageUploader,
) : ViewModel() {
    companion object {
        private const val MAX_LENGTH_NAME = FormLengthLimits.PROJECT_NAME
        private const val MAX_LENGTH_URL = FormLengthLimits.URL
        private const val MAX_LENGTH_DESCRIPTION = FormLengthLimits.PROJECT_DESCRIPTION
        private const val TAG_BASELINE = "PROJECT_EDIT_BASELINE"

        private const val TAG_NAME_PROJECT = "TAG_NAME_PROJECT"
        private const val TAG_URL_IMG_PROJECT = "TAG_URL_IMG_PROJECT"
        private const val TAG_URL_REPO_PROJECT = "TAG_URL_REPO_PROJECT"
        private const val TAG_DESCRIPTION_PROJECT = "TAG_DESCRIPTION_PROJECT"
    }

    private var initialized = false
    private var baseline: ProjectData? = null
    val contentEditor = ProjectContentEditorState(state)
    var published by SavableComposeState(state, "PROJECT_PUBLISHED", false)
    private var pendingJson by SavableComposeState(state, "PROJECT_PENDING_UPLOADS", "[]")
    val pendingUploads: List<String> get() = Json.decodeFromString(pendingJson)
    var isUploading by mutableStateOf(false)
        private set
    var uploadProgress by mutableStateOf(0f)
        private set
    private var uploadJob: Job? = null
    val hasUnsavedChanges get() = initialized && (hasAnyChange || pendingUploads.isNotEmpty())

    fun enqueueImages(uris: List<Uri>) {
        if (isUploading || isUpdatedProject) return
        val pending = (pendingUploads + uris.map(Uri::toString)).distinct()
        if (uris.size > 20 || contentEditor.images.size + pending.size > 60) {
            _messageError.trySend(R.string.project_upload_error); return
        }
        pendingJson = Json.encodeToString(pending)
        retryUploads()
    }
    fun discardPendingUploads() { if (!isUploading) pendingJson = "[]" }
    fun cancelUploads() { uploadJob?.cancel() }
    fun retryUploads() {
        if (isUploading || isUpdatedProject || pendingUploads.isEmpty()) return
        if (contentEditor.images.size + pendingUploads.size > 60) {
            _messageError.trySend(R.string.project_upload_error); return
        }
        val projectId = baseline?.idProject ?: return
        isUploading = true
        uploadJob = viewModelScope.launch {
            try {
                for (uri in pendingUploads) {
                    val image = imageUploader.upload(projectId, Uri.parse(uri)) { uploadProgress = it }
                    contentEditor.addImage(image)
                    pendingJson = Json.encodeToString(pendingUploads - uri)
                }
            } catch (e: CancellationException) { throw e }
            catch (_: Exception) { _messageError.trySend(R.string.project_upload_error) }
            finally { isUploading = false; uploadProgress = 0f }
        }
    }

    val nameProject = PropertySavableString(
        savedState = state,
        label = R.string.label_name_project,
        hint = R.string.hint_name_project,
        emptyError = R.string.error_empty_name_project,
        lengthError = R.string.error_length_name_project,
        maxLength = MAX_LENGTH_NAME,
        tagSavable = TAG_NAME_PROJECT,
        rejectBlank = true,
    )

    val descriptionProject = PropertySavableString(
        savedState = state,
        label = R.string.label_description_project,
        hint = R.string.hint_description_project,
        emptyError = R.string.error_description_project,
        lengthError = R.string.error_length_description_project,
        maxLength = MAX_LENGTH_DESCRIPTION,
        tagSavable = TAG_DESCRIPTION_PROJECT,
        rejectBlank = true,
    )

    val urlRepositoryProject = PropertySavableString(
        savedState = state,
        label = R.string.label_repo_project,
        hint = R.string.hint_repo_project,
        lengthError = R.string.error_length_link_project,
        maxLength = MAX_LENGTH_URL,
        tagSavable = TAG_URL_REPO_PROJECT,
    )

    val urlImgProject = PropertySavableString(
        savedState = state,
        label = R.string.label_img_project,
        hint = R.string.hint_img_project,
        emptyError = R.string.error_empty_link_project,
        lengthError = R.string.error_length_link_project,
        maxLength = MAX_LENGTH_URL,
        tagSavable = TAG_URL_IMG_PROJECT,
        rejectBlank = true,
    )


    private val _messageError = Channel<Int>()
    val messageError = _messageError.receiveAsFlow()


    var isUpdatedProject by mutableStateOf(false)
        private set


    val isDataValid: Boolean
        get() = !nameProject.hasError &&
                !descriptionProject.hasError &&
                !urlRepositoryProject.hasError &&
                !urlImgProject.hasError && contentEditor.isValid && pendingUploads.isEmpty() && !isUploading &&
                ProjectContentContract.https(urlImgProject.currentValue, false) &&
                ProjectContentContract.https(urlRepositoryProject.currentValue) &&
                1 + contentEditor.images.size + contentEditor.content.links.orEmpty().size +
                    (if (urlRepositoryProject.currentValue.isBlank()) 0 else 1) +
                    (if (contentEditor.content.urlPlay.isNullOrBlank()) 0 else 1) <= 60

    private val hasAnyChange
        get() = nameProject.hasChanged ||
                descriptionProject.hasChanged ||
                urlImgProject.hasChanged ||
                urlRepositoryProject.hasChanged || contentEditor.content != baseline?.content || published != baseline?.isVisible

    fun initVM(projectData: ProjectData) {
        if (initialized) return
        val saved: String? = state[TAG_BASELINE]
        val original = saved?.let { Json.decodeFromString<ProjectData>(it) } ?: projectData
        baseline = original
        state[TAG_BASELINE] = Json.encodeToString(original)
        original.let {
            contentEditor.initialize(it.content)
            if (saved == null) published = it.isVisible
            nameProject.setDefaultValue(it.name, preserveCurrentValue = saved != null)
            urlImgProject.setDefaultValue(it.urlImg, preserveCurrentValue = saved != null)
            urlRepositoryProject.setDefaultValue(it.urlRepo, preserveCurrentValue = saved != null)
            descriptionProject.setDefaultValue(it.description, preserveCurrentValue = saved != null)
        }
        initialized = true
    }




    fun updatedProject(
        currentProjectData: ProjectData,
        actionSuccess: () -> Unit
    ) = launchSafeIO(
        isEnabled = initialized && !isUpdatedProject && !isUploading,
        blockBefore = {
            nameProject.reValueField()
            descriptionProject.reValueField()
            urlRepositoryProject.reValueField()
            urlImgProject.reValueField()
            isUpdatedProject = true
        },
        blockAfter = { isUpdatedProject = false },
        blockException = {
            ExceptionManager.sendMessageErrorToException(
                exception = it,
                debugMessage = "Error to update project",
                channel = _messageError,
                messageResource = R.string.error_update_project
            )
        },
        blockIO = {
            when {
                !isDataValid ->
                    _messageError.trySend(R.string.error_invalid_data)

                !hasAnyChange ->
                    _messageError.trySend(R.string.error_no_data_change)

                else -> {
                    val original = checkNotNull(baseline)
                    require(original.idProject == currentProjectData.idProject)
                    val updateProjectWrapper = UpdateProjectWrapper(
                        name = nameProject.getValueOnlyHasChanged(),
                        urlImg = urlImgProject.getValueOnlyHasChanged(),
                        urlRepo = urlRepositoryProject.getValueOnlyHasChanged(),
                        description = descriptionProject.getValueOnlyHasChanged(),
                        idProject = original.idProject,
                        isVisible = published.takeIf { it != original.isVisible },
                        baseline = original,
                        content = contentEditor.content.takeIf { it != original.content },
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
    )
}
