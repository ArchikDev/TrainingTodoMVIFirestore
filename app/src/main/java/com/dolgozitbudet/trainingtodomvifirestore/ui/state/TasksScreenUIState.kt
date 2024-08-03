package com.dolgozitbudet.trainingtodomvifirestore.ui.state

import com.dolgozitbudet.trainingtodomvifirestore.data.model.Task

data class TasksScreenUIState(
    val isLoading: Boolean = false,
    val tasks: List<Task> = emptyList(),
    val errorMessage: String? = null,
    val taskToBeUpdated: Task? = null,
    val isShowAddTaskDialog: Boolean = false,
    val isShowUpdateTaskDialog: Boolean = false,
    val currentTextFieldTitle: String = "",
    val currentTextFieldBody: String = "",
)
