package com.dolgozitbudet.trainingtodomvifirestore.ui.sideEffects

sealed class TaskScreenSideEffects {
    data class ShowSnackBarMessage(val message: String): TaskScreenSideEffects()

}