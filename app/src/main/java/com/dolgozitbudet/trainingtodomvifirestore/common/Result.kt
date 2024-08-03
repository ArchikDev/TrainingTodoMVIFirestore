package com.dolgozitbudet.trainingtodomvifirestore.common

sealed class ResultFetch<out T: Any> {
    data class Success<out T: Any>(val data: T): ResultFetch<T>()
    data class Failure(val exception: Exception): ResultFetch<Nothing>()
}