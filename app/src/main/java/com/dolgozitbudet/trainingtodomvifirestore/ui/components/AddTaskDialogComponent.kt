@file:OptIn(ExperimentalMaterial3Api::class)

package com.dolgozitbudet.trainingtodomvifirestore.ui.components


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults.outlinedTextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.dolgozitbudet.trainingtodomvifirestore.ui.state.TasksScreenUIState

@Composable
fun AddTaskDialogComponent(
    setTaskTitle: (String) -> Unit,
    setTaskBody: (String) -> Unit,
    saveTask: () -> Unit,
    closeDialog: () -> Unit,
    uiState: TasksScreenUIState
) {
    Dialog(onDismissRequest = { closeDialog() }) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            LazyColumn(contentPadding = PaddingValues(12.dp)) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "New task",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = uiState.currentTextFieldTitle,
                        onValueChange = { title ->
                            setTaskTitle(title)
                        },
                        label = { Text("Task Title") },
                        colors = outlinedTextFieldColors(
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = Color.Black,
                            focusedLabelColor = Color.Black,
                            unfocusedLabelColor = Color.Black,
                            cursorColor = Color.Black
                        )
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = uiState.currentTextFieldBody,
                        onValueChange = { body ->
                            setTaskBody(body)
                        },
                        label = { Text("Task Body") },
                        colors = outlinedTextFieldColors(
                            focusedBorderColor = Color.Black,
                            unfocusedBorderColor = Color.Black,
                            focusedLabelColor = Color.Black,
                            unfocusedLabelColor = Color.Black,
                            cursorColor = Color.Black
                        )
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            saveTask()
                            setTaskTitle("")
                            setTaskBody("")
                            closeDialog()
                        },
                        modifier = Modifier.padding(horizontal = 12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Save Task"
                        )
                    }
                }
            }
        }
    }
}