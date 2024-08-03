package com.dolgozitbudet.trainingtodomvifirestore.data.repositories

import com.dolgozitbudet.trainingtodomvifirestore.common.ResultFetch
import com.dolgozitbudet.trainingtodomvifirestore.data.model.Task

interface TaskRepository {
    suspend fun addTask(title: String, body: String): ResultFetch<Unit>

    suspend fun getAllTasks(): ResultFetch<List<Task>>

    suspend fun deleteTask(taskId: String): ResultFetch<Unit>

    suspend fun updateTask(taskId: String, title: String, body: String): ResultFetch<Unit>
}