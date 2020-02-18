package uo246008.quizflags.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.navGraphViewModels
import uo246008.quizflags.Quizflags
import uo246008.quizflags.R
import uo246008.quizflags.viewmodel.LogStatus
import uo246008.quizflags.viewmodel.QuizViewModel

class ScoreFragment : Fragment() {

    private val viewModel by navGraphViewModels<QuizViewModel>(R.id.quiz_nav_graph)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_score, container, false)
        val correctAnswersTv = view.findViewById<TextView>(R.id.correct_answers_tv)
        val incorrectAnswersTv = view.findViewById<TextView>(R.id.incorrect_answers_tv)
        val timeTv = view.findViewById<TextView>(R.id.time_tv)
        val progressBar = view.findViewById<ProgressBar>(R.id.progress_bar)
        val finishButton = view.findViewById<Button>(R.id.finish_btn)
        val score = viewModel.score
        correctAnswersTv.text = Quizflags.app.getString(R.string.correct_answers, score.correct)
        incorrectAnswersTv.text = Quizflags.app.getString(R.string.incorrect_answers, score.incorrect)
        timeTv.text = Quizflags.app.getString(R.string.time, "${(score.time / 60).toString().padStart(2, '0')}:${(score.time % 60).toString().padStart(2, '0')}")
        finishButton.setOnClickListener {
            requireActivity().finish()
        }
        viewModel.logStatus.observe(viewLifecycleOwner) {
            when (it) {
                is LogStatus.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    finishButton.isVisible = false
                }
                is LogStatus.Finished -> {
                    progressBar.visibility = View.GONE
                    finishButton.isVisible = true
                }
            }
        }
        return view
    }
}