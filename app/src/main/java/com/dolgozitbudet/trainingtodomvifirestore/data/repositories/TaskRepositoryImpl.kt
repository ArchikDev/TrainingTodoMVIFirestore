package com.dolgozitbudet.trainingtodomvifirestore.data.repositories

import com.dolgozitbudet.trainingtodomvifirestore.common.COLLECTION_PATH_NAME
import com.dolgozitbudet.trainingtodomvifirestore.common.PLEASE_CHECK_INTERNET_CONNECTION
import com.dolgozitbudet.trainingtodomvifirestore.common.ResultFetch
import com.dolgozitbudet.trainingtodomvifirestore.common.convertDateFormat
import com.dolgozitbudet.trainingtodomvifirestore.common.getCurrentTimeAsString
import com.dolgozitbudet.trainingtodomvifirestore.data.model.Task
import com.dolgozitbudet.trainingtodomvifirestore.di.IoDispatcher
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val todoAppDb: FirebaseFirestore,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
): TaskRepository {

    override suspend fun addTask(title: String, body: String): ResultFetch<Unit> {
        return try {
            withContext(ioDispatcher) {
                val task = hashMapOf(
                    "title" to title,
                    "body" to body,
                    "createdAt" to getCurrentTimeAsString(),
                )

                val addTaskTimeout = withTimeoutOrNull(10000L) {
                    todoAppDb.collection(COLLECTION_PATH_NAME)
                        .add(task)
                }

                if (addTaskTimeout == null) {
                    ResultFetch.Failure(
                        IllegalStateException(
                        PLEASE_CHECK_INTERNET_CONNECTION)
                    )
                }

                ResultFetch.Success(Unit)
            }
        } catch (exception: Exception) {
            ResultFetch.Failure(exception = exception)
        }
    }

    override suspend fun getAllTasks(): ResultFetch<List<Task>> {
        return try {
            withContext(ioDispatcher) {
                val fetchingTaskTimeout = withTimeoutOrNull(10000L) {
                    todoAppDb.collection(COLLECTION_PATH_NAME)
                        .get()
                        .await()
                        .documents.map { document ->
                            Task(
                                taskId = document.id,
                                title = document.getString("title") ?: "",
                                body = document.getString("body") ?: "",
                                createdAt = convertDateFormat(
                                    document.getString("createdAt") ?: ""
                                ),
                            )
                        }
                }

                if (fetchingTaskTimeout == null) {
                    ResultFetch.Failure(
                        IllegalStateException(
                            PLEASE_CHECK_INTERNET_CONNECTION)
                    )
                }

                ResultFetch.Success(fetchingTaskTimeout?.toList() ?: emptyList())
            }
        } catch (exception: Exception) {
            ResultFetch.Failure(exception = exception)
        }
    }

    override suspend fun deleteTask(taskId: String): ResultFetch<Unit> {
        return try {
            withContext(ioDispatcher) {
                val deleteTaskTimeout = withTimeoutOrNull(10000L) {
                    todoAppDb.collection(COLLECTION_PATH_NAME)
                        .document(taskId)
                        .delete()
                }

                if (deleteTaskTimeout == null) {
                    ResultFetch.Failure(
                        IllegalStateException(
                            PLEASE_CHECK_INTERNET_CONNECTION)
                    )
                }

                ResultFetch.Success(Unit)
            }
        } catch (exception: Exception) {
            ResultFetch.Failure(exception = exception)
        }
    }

    override suspend fun updateTask(taskId: String, title: String, body: String): ResultFetch<Unit> {
        return try {
            withContext(ioDispatcher) {
                val taskUpdate: Map<String, String> = hashMapOf(
                    "title" to title,
                    "body" to body,
                )



                val updateTaskTimeout = withTimeoutOrNull(10000L) {
                    todoAppDb.collection(COLLECTION_PATH_NAME)
                        .document(taskId)
                        .update(taskUpdate)
                }

                if (updateTaskTimeout == null) {
                    ResultFetch.Failure(
                        IllegalStateException(
                            PLEASE_CHECK_INTERNET_CONNECTION)
                    )
                }

                ResultFetch.Success(Unit)
            }
        } catch (exception: Exception) {
            ResultFetch.Failure(exception = exception)
        }
    }
}