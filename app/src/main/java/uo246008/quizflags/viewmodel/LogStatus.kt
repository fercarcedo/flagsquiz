package uo246008.quizflags.viewmodel

sealed class LogStatus {
    object Loading : LogStatus()
    object Finished : LogStatus()
}