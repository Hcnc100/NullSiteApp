package com.nullpointer.nullsiteadmin.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nullpointer.nullsiteadmin.R
import com.nullpointer.nullsiteadmin.core.delagetes.SavableComposeState
import com.nullpointer.nullsiteadmin.core.states.Resource
import com.nullpointer.nullsiteadmin.core.utils.launchSafeIO
import com.nullpointer.nullsiteadmin.domain.project.ProjectRepository
import com.nullpointer.nullsiteadmin.models.Project
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
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
        private const val KEY_CONCATENATE_ENABLE = "KEY_CONCATENATE_ENABLE"
    }

    private val _messageErrorProject = Channel<Int>()
    val messageErrorProject = _messageErrorProject.receiveAsFlow()

    var isConcatenateProjects by SavableComposeState(
        defaultValue = false,
        key = KEY_IS_CONCATENATE,
        savedStateHandle = savedStateHandle
    )
        private set

    var isEnabledConcatenateProjects by SavableComposeState(
        defaultValue = true,
        key = KEY_CONCATENATE_ENABLE,
        savedStateHandle = savedStateHandle
    )
        private set

    init {
        requestNewProjects(false)
    }

    val listProject = flow<Resource<List<Project>>> {
        projectRepository.listProjects.collect {
            emit(Resource.Success(it))
        }
    }.flowOn(Dispatchers.IO).catch {
        Timber.e("Failed to list projects $it")
        _messageErrorProject.trySend(R.string.error_load_project)
        emit(Resource.Failure)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        Resource.Loading
    )

    fun concatenateProject() = launchSafeIO(
        isEnabled = isEnabledConcatenateProjects,
        blockBefore = { isConcatenateProjects = true },
        blockAfter = { isConcatenateProjects = false },
        blockIO = {
            val sizeConcatProject = projectRepository.concatenateProjects()
            Timber.d("Size of concatenate project: $sizeConcatProject")
            withContext(Dispatchers.Main) {
                isEnabledConcatenateProjects = sizeConcatProject != 0
            }
        },
        blockException = {
            Timber.d("Error concatenating projects $it")
        }
    )

    fun editProject(
        project: Project
    ) = launchSafeIO(
        blockIO = {
            projectRepository.editProject(project)
            delay(300)
            _messageErrorProject.trySend(R.string.message_upload_project)
        },
        blockException = {
            _messageErrorProject.trySend(R.string.message_error_upload_project)
            Timber.e("Failed upload project $it")
        }
    )

    fun requestNewProjects(forceRefresh: Boolean = true) = launchSafeIO(
        blockIO = {
            val size = projectRepository.requestLastProject(forceRefresh)
            Timber.d("new projects size: $size")
        },
        blockException = {
            Timber.e("Error request last projects $it")
        }
    )
}