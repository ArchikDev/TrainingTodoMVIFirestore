package com.dolgozitbudet.trainingtodomvifirestore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dolgozitbudet.trainingtodomvifirestore.common.SIDE_EFFECTS_KEY
import com.dolgozitbudet.trainingtodomvifirestore.ui.components.AddTaskDialogComponent
import com.dolgozitbudet.trainingtodomvifirestore.ui.components.EmptyComponent
import com.dolgozitbudet.trainingtodomvifirestore.ui.components.LoadingComponent
import com.dolgozitbudet.trainingtodomvifirestore.ui.components.TaskCardComponent
import com.dolgozitbudet.trainingtodomvifirestore.ui.components.UpdateTaskDialogComponent
import com.dolgozitbudet.trainingtodomvifirestore.ui.components.WelcomeMessageComponent
import com.dolgozitbudet.trainingtodomvifirestore.ui.events.TaskScreenUIEvent
import com.dolgozitbudet.trainingtodomvifirestore.ui.sideEffects.TaskScreenSideEffects
import com.dolgozitbudet.trainingtodomvifirestore.ui.theme.TrainingTodoMVIFirestoreTheme
import com.dolgozitbudet.trainingtodomvifirestore.ui.viewModel.TaskViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val tasksViewModel: TaskViewModel = viewModel()

            val uiState = tasksViewModel.state.collectAsState().value

            val effectFlow = tasksViewModel.effect

            val snackBarHostState = remember { SnackbarHostState() }

            LaunchedEffect(key1 = SIDE_EFFECTS_KEY) {
                effectFlow.onEach { effect ->
                    when (effect) {
                        is TaskScreenSideEffects.ShowSnackBarMessage -> {
                            snackBarHostState.showSnackbar(
                                message = effect.message,
                                duration = SnackbarDuration.Short,
                                actionLabel = "DISMISS",
                            )
                        }
                    }
                }.collect {}
            }

            TrainingTodoMVIFirestoreTheme {
                if (uiState.isShowAddTaskDialog) {
                    AddTaskDialogComponent(
                        uiState = uiState,
                        setTaskTitle = { title ->
                            tasksViewModel.sendEvent(
                                event = TaskScreenUIEvent.OnChangeTaskTitle(title = title)
                            )
                        },
                        setTaskBody = { body ->
                            tasksViewModel.sendEvent(
                                event = TaskScreenUIEvent.OnChangeTaskBody(body = body)
                            )
                        },
                        saveTask = {
                            tasksViewModel.sendEvent(
                                event = TaskScreenUIEvent.AddTask(
                                    title = uiState.currentTextFieldTitle,
                                    body = uiState.currentTextFieldBody,
                                )
                            )
                        },
                        closeDialog = {
                            tasksViewModel.sendEvent(
                                event = TaskScreenUIEvent.OnChangeAddTaskDialogState(show = false)
                            )
                        }
                    )
                }

                if (uiState.isShowUpdateTaskDialog) {
                    UpdateTaskDialogComponent(
                        uiState = uiState,
                        setTaskTitle = { title ->
                            tasksViewModel.sendEvent(
                                event = TaskScreenUIEvent.OnChangeTaskTitle(title = title)
                            )
                        },
                        setTaskBody = { body ->
                            tasksViewModel.sendEvent(
                                event = TaskScreenUIEvent.OnChangeTaskBody(body = body)
                            )
                        },
                        saveTask = {
                            tasksViewModel.sendEvent(
                                event = TaskScreenUIEvent.UpdateTask
                            )
                        },
                        closeDialog = {
                            tasksViewModel.sendEvent(
                                event = TaskScreenUIEvent.OnChangeUpdateTaskDialogState(show = false)
                            )
                        },
                        task = uiState.taskToBeUpdated
                    )
                }

                Scaffold(
                    snackbarHost = {
                        SnackbarHost(snackBarHostState)
                    },
                    floatingActionButton = {
                        ExtendedFloatingActionButton(
                            icon = {
                                Icon(
                                    imageVector = Icons.Rounded.AddCircle,
                                    contentDescription = "Add Task",
                                    tint = Color.White
                                )
                            },
                            text = {
                                Text(
                                    text = "Add Task",
                                    color = Color.White
                                )
                            },
                            onClick = {
                                tasksViewModel.sendEvent(
                                    event = TaskScreenUIEvent.OnChangeAddTaskDialogState(show = true)
                                )
                            },
                            modifier = Modifier.padding(horizontal = 12.dp),
                            containerColor = Color.Black,
                            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp)
                        )
                    },
                    containerColor = Color(0XFFFAFAFA)
                ) { paddingValues ->
                    Box(modifier = Modifier.padding(paddingValues = paddingValues)) {
                        when {
                            uiState.isLoading -> {
                                LoadingComponent()
                            }

                            !uiState.isLoading && uiState.tasks.isNotEmpty() -> {
                                LazyColumn(contentPadding = PaddingValues(14.dp)) {
                                    item {
                                        WelcomeMessageComponent()

                                        Spacer(modifier = Modifier.height(30.dp))
                                    }

                                    items(uiState.tasks) { task ->
                                        TaskCardComponent(
                                            deleteTask = { taskId ->
                                                tasksViewModel.sendEvent(
                                                    event = TaskScreenUIEvent.DeleteTask(taskId = taskId)
                                                )
                                            },
                                            updateTask = { taskToBeUpdated ->
                                                tasksViewModel.sendEvent(
                                                    event = TaskScreenUIEvent.OnChangeUpdateTaskDialogState(show = true)
                                                )

                                                tasksViewModel.sendEvent(
                                                    event = TaskScreenUIEvent.SetTaskToBeUpdated(
                                                        taskToBeUpdated = taskToBeUpdated
                                                    )
                                                )
                                            },
                                            task = task
                                        )
                                    }
                                }
                            }

                            !uiState.isLoading && uiState.tasks.isEmpty() -> {
                                EmptyComponent()
                            }
                        }
                    }
                }
            }
        }
    }
}