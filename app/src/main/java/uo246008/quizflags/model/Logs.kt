package uo246008.quizflags.model

import com.squareup.moshi.Json

data class Logs(
    @field:Json(name = "user_id") val userId: String,
    val correct: Int,
    val incorrect: Int,
    val time: Long,
    val score: Int,
    @field:Json(name = "time_until_first_correct") val timeUntilFirstCorrect: Long,
    @field:Json(name = "time_until_first_incorrect") val timeUntilFirstIncorrect: Long,
    @field:Json(name = "max_consecutive_correct") val maxConsecutiveCorrect: Int,
    @field:Json(name = "max_consecutive_incorrect") val maxConsecutiveIncorrect: Int,
    @field:Json(name = "questions_data") val questionsData: String
)