package uo246008.quizflags.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import uo246008.quizflags.Quizflags
import uo246008.quizflags.model.Flag
import uo246008.quizflags.model.Logs
import uo246008.quizflags.model.QuestionData
import uo246008.quizflags.model.Score
import uo246008.quizflags.service.LogService
import java.nio.charset.Charset

private const val QUESTIONS_KEY = "questions"
private const val NAME_KEY = "name"
private const val FLAG_KEY = "flag"
private const val OPTIONS_KEY = "options"

class QuizViewModel : ViewModel() {
    private val _flags = MutableLiveData<Flag>()
    val flags: LiveData<Flag> = _flags
    private var currentQuestion = 0
    private val flagsList: List<Flag>
    var userId: String = "1"
    private var startTime = 0L
    private var time = 0L
    private var lastCardTime = 0L
    private var correctAnswers = 0
    private var incorrectAnswers = 0
    private var timeUntilFirstCorrect = 0L
    private var timeUntilFirstIncorrect = 0L
    private var currentConsecutiveCorrect = 0
    private var currentConsecutiveIncorrect = 0
    private var maxConsecutiveCorrect = 0
    private var maxConsecutiveIncorrect = 0
    private val _logStatus = MutableLiveData<LogStatus>()
    val logStatus: LiveData<LogStatus> = _logStatus
    private val questionsData = mutableListOf<QuestionData>()

    val score: Score
        get() {
            return Score(
                correctAnswers,
                incorrectAnswers,
                time / 1000,
                calculateScore()
            )
        }

    init {
        flagsList = loadFlags()
        _flags.value = flagsList[currentQuestion]
    }

    private fun loadFlags(): List<Flag> {
        val json = loadJSONFromAsset("questions.json")
        val flagsResult = loadFlagsFromJSON(json).toMutableList()
        flagsResult.shuffle()
        return flagsResult
    }

    private fun loadJSONFromAsset(name: String): String {
        val inputStream = Quizflags.app.assets.open(name)
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        return String(buffer, Charset.forName("UTF-8"))
    }

    private fun loadFlagsFromJSON(json: String): List<Flag> {
        val flags = mutableListOf<Flag>()
        val jsonObject = JSONObject(json)
        val questionsJson = jsonObject.getJSONArray(QUESTIONS_KEY)
        for (i in 0 until questionsJson.length()) {
            val questionObject = questionsJson.getJSONObject(i)
            val name = questionObject.getString(NAME_KEY)
            val flag = "flags/${questionObject.getString(FLAG_KEY)}"
            val optionsJson = questionObject.getJSONArray(OPTIONS_KEY)
            val options = mutableListOf<String>()
            for (optionIndex in 0 until optionsJson.length()) {
                options.add(optionsJson.getString(optionIndex))
            }
            options.shuffle()
            flags.add(Flag(name, flag, options))
        }
        return flags
    }

    fun checkAnswer(answer: String): Boolean {
        val correct = if (flagsList[currentQuestion].name == answer) {
            if (timeUntilFirstCorrect == 0L) {
                timeUntilFirstCorrect = System.currentTimeMillis() - startTime
            }
            currentConsecutiveIncorrect = 0
            correctAnswers++
            currentConsecutiveCorrect++
            if (currentConsecutiveCorrect > maxConsecutiveCorrect) {
                maxConsecutiveCorrect = currentConsecutiveCorrect
            }
            true
        } else {
            if (timeUntilFirstIncorrect == 0L) {
                timeUntilFirstIncorrect = System.currentTimeMillis() - startTime
            }
            currentConsecutiveCorrect = 0
            incorrectAnswers++
            currentConsecutiveIncorrect++
            if (currentConsecutiveIncorrect > maxConsecutiveIncorrect) {
                maxConsecutiveIncorrect = currentConsecutiveIncorrect
            }
            false
        }
        questionsData.add(QuestionData(flagsList[currentQuestion], System.currentTimeMillis() - lastCardTime, correct))
        return correct
    }

    fun nextQuestion(): Boolean {
        return if (currentQuestion < flagsList.size - 1) {
            currentQuestion++
            _flags.value = flagsList[currentQuestion]
            lastCardTime = System.currentTimeMillis()
            true
        } else {
            time = System.currentTimeMillis() - startTime
            sendLogs()
            false
        }
    }

    fun startIfNeeded() {
        if (startTime == 0L) {
            startTime = System.currentTimeMillis()
            lastCardTime = System.currentTimeMillis()
        }
    }

    private fun calculateScore(): Int = (correctAnswers - (incorrectAnswers * (1f / (flagsList.size - 1f))) - ((time / 1000) * 0.05f)).toInt()

    fun sendLogs() {
        val logs = Logs(
            userId,
            correctAnswers,
            incorrectAnswers,
            time / 1000,
            calculateScore(),
            timeUntilFirstCorrect,
            timeUntilFirstIncorrect,
            maxConsecutiveCorrect,
            maxConsecutiveIncorrect,
            questionsData.toString()
        )
        viewModelScope.launch {
            _logStatus.value = LogStatus.Loading
            withContext(Dispatchers.IO) {
                LogService.instance.sendLogs(logs)
            }
            _logStatus.value = LogStatus.Finished
        }
    }
}