package uo246008.quizflags.model

data class Score(
    val correct: Int,
    val incorrect: Int,
    val time: Long,
    val score: Int
)