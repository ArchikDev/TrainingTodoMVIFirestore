package com.dolgozitbudet.trainingtodomvifirestore.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dolgozitbudet.trainingtodomvifirestore.common.ResultFetch
import com.dolgozitbudet.trainingtodomvifirestore.data.model.Task
import com.dolgozitbudet.trainingtodomvifirestore.data.repositories.TaskRepository
import com.dolgozitbudet.trainingtodomvifirestore.ui.events.TaskScreenUIEvent
import com.dolgozitbudet.trainingtodomvifirestore.ui.sideEffects.TaskScreenSideEffects
import com.dolgozitbudet.trainingtodomvifirestore.ui.state.TasksScreenUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repository: TaskRepository
): ViewModel() {
    private val _state: MutableStateFlow<TasksScreenUIState> = MutableStateFlow(TasksScreenUIState())
    val state: StateFlow<TasksScreenUIState> = _state.asStateFlow()

    private val _effect: Channel<TaskScreenSideEffects> = Channel()
    val effect = _effect.receiveAsFlow()

    init {
        sendEvent(TaskScreenUIEvent.GetTasks)
    }

    fun sendEvent(event: TaskScreenUIEvent) {
        reduce(event = event, oldState = state.value)
    }

    private fun setState(newState: TasksScreenUIState) {
        _state.value = newState
    }

    private fun setEffect(builder: () -> TaskScreenSideEffects) {
        val effectValue = builder()
        viewModelScope.launch {
            _effect.send(effectValue)
        }
    }

    private fun reduce(event: TaskScreenUIEvent, oldState: TasksScreenUIState) {
        when(event) {
            is TaskScreenUIEvent.AddTask -> {
                addTask(
                    oldState = oldState,
                    title = event.title,
                    body = event.body
                )
            }
            is TaskScreenUIEvent.DeleteTask -> {
                deleteTask(
                    oldState = oldState,
                    taskId = event.taskId
                )
            }
            TaskScreenUIEvent.GetTasks -> {
                getAllTasks(oldState = oldState)
            }
            is TaskScreenUIEvent.OnChangeAddTaskDialogState -> {
                onChangeAddTaskDialogState(
                    oldState = oldState,
                    isShown = event.show
                )
            }
            is TaskScreenUIEvent.OnChangeTaskBody -> {
                onChangeTaskBody(
                    oldState = oldState,
                    body = event.body
                )
            }
            is TaskScreenUIEvent.OnChangeTaskTitle -> {
                onChangeTaskTitle(
                    oldState = oldState,
                    title = event.title
                )
            }
            is TaskScreenUIEvent.OnChangeUpdateTaskDialogState -> {
                onChangeUpdateTaskDialogState(
                    oldState = oldState,
                    isShown = event.show
                )
            }
            is TaskScreenUIEvent.SetTaskToBeUpdated -> {
                setTaskToBeUpdated(
                    oldState = oldState,
                    task = event.taskToBeUpdated
                )
            }
            TaskScreenUIEvent.UpdateTask -> {
                updateTask(
                    oldState = oldState
                )
            }
        }
    }

    private fun updateTask(oldState: TasksScreenUIState) {
        viewModelScope.launch {
            setState(
                oldState.copy(
                    isLoading = true
                )
            )

            val title = oldState.currentTextFieldTitle
            val body = oldState.currentTextFieldBody
            val taskToBeUpdated = oldState.taskToBeUpdated

            when(val result = repository.updateTask(
                title = title,
                body = body,
                taskId = taskToBeUpdated?.taskId ?: "",
            )) {
                is ResultFetch.Failure -> {
                    setState(
                        oldState.copy(
                            isLoading = false
                        )
                    )

                    val errorMessage = result.exception.message ?: "An Error fetching tasks"

                    setEffect { TaskScreenSideEffects.ShowSnackBarMessage(message = errorMessage) }
                }
                is ResultFetch.Success -> {
                    setState(
                        oldState.copy(
                            isLoading = false,
                            currentTextFieldTitle = "",
                            currentTextFieldBody = "",
                        )
                    )

                    sendEvent(event = TaskScreenUIEvent.OnChangeAddTaskDialogState(show = true))

                    setEffect { TaskScreenSideEffects.ShowSnackBarMessage(message = "Task updated successfully") }

                    sendEvent(TaskScreenUIEvent.GetTasks)
                }
            }
        }
    }

    private fun setTaskToBeUpdated(oldState: TasksScreenUIState, task: Task) {
        setState(
            oldState.copy(
                taskToBeUpdated = task
            )
        )
    }

    private fun onChangeUpdateTaskDialogState(oldState: TasksScreenUIState, isShown: Boolean) {
        setState(
            oldState.copy(
                isShowUpdateTaskDialog = isShown
            )
        )
    }

    private fun onChangeTaskBody(oldState: TasksScreenUIState, body: String) {
        setState(
            oldState.copy(
                currentTextFieldBody = body
            )
        )
    }

    private fun onChangeTaskTitle(oldState: TasksScreenUIState, title: String) {
        setState(
            oldState.copy(
                currentTextFieldTitle = title
            )
        )
    }

    private fun onChangeAddTaskDialogState(
        oldState: TasksScreenUIState,
        isShown: Boolean
    ) {
        setState(
            oldState.copy(
                isShowAddTaskDialog = isShown
            )
        )
    }

    private fun getAllTasks(oldState: TasksScreenUIState) {
        viewModelScope.launch {
            setState(
                oldState.copy(
                    isLoading = true
                )
            )

            when(val result = repository.getAllTasks()) {
                is ResultFetch.Failure -> {
                    setState(
                        oldState.copy(
                            isLoading = false
                        )
                    )

                    val errorMessage = result.exception.message ?: "An Error fetching tasks"

                    setEffect { TaskScreenSideEffects.ShowSnackBarMessage(message = errorMessage) }
                }
                is ResultFetch.Success -> {
                    setState(
                        oldState.copy(
                            isLoading = false,
                            tasks = result.data
                        )
                    )
                }
            }
        }
    }

    private fun deleteTask(oldState: TasksScreenUIState, taskId: String) {
        viewModelScope.launch {
            setState(
                oldState.copy(
                    isLoading = true
                )
            )

            when(val result = repository.deleteTask(taskId = taskId)) {
                is ResultFetch.Failure -> {
                    setState(
                        oldState.copy(
                            isLoading = false
                        )
                    )

                    val errorMessage = result.exception.message ?: "An Error delete task"

                    setEffect { TaskScreenSideEffects.ShowSnackBarMessage(message = errorMessage) }
                }
                is ResultFetch.Success -> {
                    setState(
                        oldState.copy(isLoading = false)
                    )

                    setEffect { TaskScreenSideEffects.ShowSnackBarMessage(message = "Task deleted success") }

                    sendEvent(TaskScreenUIEvent.GetTasks)
                }
            }
        }
    }

    private fun addTask(oldState: TasksScreenUIState, title: String, body: String) {
        viewModelScope.launch {
            setState(
                oldState.copy(
                    isLoading = true
                )
            )

            when(val result = repository.addTask(title = title, body = body)) {
                is ResultFetch.Failure -> {
                    setState(
                        oldState.copy(
                            isLoading = false
                        )
                    )

                    val errorMessage = result.exception.message ?: "An Error adding task"

                    setEffect { TaskScreenSideEffects.ShowSnackBarMessage(message = errorMessage) }
                }
                is ResultFetch.Success -> {
                    setState(
                        oldState.copy(
                            isLoading = false,
                            currentTextFieldTitle = "",
                            currentTextFieldBody = "",
                        )
                    )

                    sendEvent(event = TaskScreenUIEvent.OnChangeAddTaskDialogState(show = false))

                    sendEvent(TaskScreenUIEvent.GetTasks)

                    setEffect { TaskScreenSideEffects.ShowSnackBarMessage(message = "Task added success") }
                }
            }
        }
    }
}