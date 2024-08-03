package com.dolgozitbudet.trainingtodomvifirestore.ui.events

import com.dolgozitbudet.trainingtodomvifirestore.data.model.Task

sealed class TaskScreenUIEvent {
    data object GetTasks: TaskScreenUIEvent()

    data class AddTask(val title: String, val body: String): TaskScreenUIEvent()

    data object UpdateTask : TaskScreenUIEvent()

    data class DeleteTask(val taskId: String): TaskScreenUIEvent()

    data class OnChangeTaskTitle(val title: String): TaskScreenUIEvent()

    data class OnChangeTaskBody(val body: String): TaskScreenUIEvent()

    data class OnChangeAddTaskDialogState(val show: Boolean): TaskScreenUIEvent()

    data class OnChangeUpdateTaskDialogState(val show: Boolean): TaskScreenUIEvent()

    data class SetTaskToBeUpdated(val taskToBeUpdated: Task): TaskScreenUIEvent()


}