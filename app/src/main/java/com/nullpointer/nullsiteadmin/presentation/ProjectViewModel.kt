package com.nullpointer.nullsiteadmin.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nullpointer.nullsiteadmin.core.states.Resource
import com.nullpointer.nullsiteadmin.domain.project.ProjectRepository
import com.nullpointer.nullsiteadmin.models.Project
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProjectViewModel @Inject constructor(
    private val projectRepository: ProjectRepository
) : ViewModel() {

    private val _messageErrorProject = Channel<String>()
    val messageErrorProject = _messageErrorProject.receiveAsFlow()

    val listProject = flow<Resource<List<Project>>> {
        projectRepository.listProjects.collect {
            emit(Resource.Success(it))
        }
    }.flowOn(Dispatchers.IO).catch {
        Timber.e("Failed to list projects $it")
        _messageErrorProject.trySend("Error while loading projects")
        emit(Resource.Failure)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        Resource.Loading
    )

    fun editProject(
        project: Project
    ) = viewModelScope.launch(Dispatchers.IO) {
        try {
            projectRepository.editProject(project)
            delay(300)
            _messageErrorProject.trySend("Projecto actualizado")
        } catch (e: Exception) {
            Timber.e("Failed to edit project $e")
            when (e) {
                is CancellationException -> throw e
                else -> _messageErrorProject.trySend("Error while edit project")
            }
        }
    }
}