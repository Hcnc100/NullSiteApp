package com.nullpointer.nullsiteadmin.ui.screens.project.projectScreen.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.core.delagetes.SavableComposeState
import com.nullpointer.nullsiteadmin.core.states.Resource
import com.nullpointer.nullsiteadmin.core.utils.ExceptionManager
import com.nullpointer.nullsiteadmin.core.utils.launchSafeIO
import com.nullpointer.nullsiteadmin.domain.project.ProjectRepository
import com.nullpointer.nullsiteadmin.models.project.data.ProjectData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProjectViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val projectRepository: ProjectRepository
) : ViewModel() {

    companion object {
        private const val KEY_IS_CONCATENATE = "KEY_IS_CONCATENATE"
        private const val KEY_IS_REQUEST_PROJECT = "KEY_IS_REQUEST_PROJECT"
        private const val KEY_CONCATENATE_ENABLE = "KEY_CONCATENATE_ENABLE"
    }

    private val _messageErrorProject = Channel<Int>()
    val messageErrorProject = _messageErrorProject.receiveAsFlow()

    var isConcatenateProjects by SavableComposeState(
        defaultValue = false,
        key = KEY_IS_CONCATENATE, savedStateHandle = savedStateHandle
    )
        private set

    private var isEnabledConcatenateProjects by SavableComposeState(
        defaultValue = true, key = KEY_CONCATENATE_ENABLE, savedStateHandle = savedStateHandle
    )

    var isRequestProject by SavableComposeState(
        defaultValue = false,
        key = KEY_IS_REQUEST_PROJECT,
        savedStateHandle = savedStateHandle
    )
        private set


    init {
        requestNewProjects()
    }

    val listProjectData =
        projectRepository.listProjects.transform<List<ProjectData>, Resource<List<ProjectData>>> {
            emit(Resource.Success(it))
        }.flowOn(Dispatchers.IO).catch {
            Timber.e("Failed to list projects $it")
            _messageErrorProject.trySend(R.string.error_load_project)
            emit(Resource.Failure)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            Resource.Loading
        )

    fun concatenateProject(
        actionSuccessConcatenate: () -> Unit = {}
    ) = launchSafeIO(
        isEnabled = isEnabledConcatenateProjects,
        blockBefore = { isConcatenateProjects = true },
        blockAfter = { isConcatenateProjects = false },
        blockIO = {
            val sizeConcatProject = projectRepository.concatenateProjects()
            Timber.d("Size of concatenate project: $sizeConcatProject")
            withContext(Dispatchers.Main) {
                isEnabledConcatenateProjects = sizeConcatProject != 0
                actionSuccessConcatenate()
            }
        },
        blockException = {
            ExceptionManager.sendMessageErrorToException(
                exception = it,
                channel = _messageErrorProject,
                message = "Failed to concatenate projects"
            )
        }
    )

    fun requestNewProjects() = launchSafeIO(
        isEnabled = !isRequestProject,
        blockBefore = { isRequestProject = true },
        blockAfter = { isRequestProject = false },
        blockIO = {
            val size = projectRepository.requestLastProject()
            Timber.d("new projects size: $size")
            withContext(Dispatchers.Main) {
                isEnabledConcatenateProjects = true
            }
        },
        blockException = {
            ExceptionManager.sendMessageErrorToException(
                exception = it,
                channel = _messageErrorProject,
                message = "Error request last projects"
            )
        })
}