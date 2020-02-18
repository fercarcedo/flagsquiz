package uo246008.quizflags.fragment

import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.google.android.material.button.MaterialButton
import uo246008.quizflags.Quizflags
import uo246008.quizflags.R
import uo246008.quizflags.model.Flag
import uo246008.quizflags.viewmodel.QuizViewModel

class QuizFragment : Fragment() {

    private val viewModel by navGraphViewModels<QuizViewModel>(R.id.quiz_nav_graph)

    private lateinit var firstAnswerBtn: MaterialButton
    private lateinit var secondAnswerBtn: MaterialButton
    private lateinit var thirdAnswerBtn: MaterialButton
    private lateinit var fourthAnswerBtn: MaterialButton
    private lateinit var flag: Flag

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.startIfNeeded()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_quiz, container, false)
        val flagIv = view.findViewById<ImageView>(R.id.flag_iv)
        firstAnswerBtn = view.findViewById(R.id.first_answer_btn)
        secondAnswerBtn = view.findViewById(R.id.second_answer_btn)
        thirdAnswerBtn = view.findViewById(R.id.third_answer_btn)
        fourthAnswerBtn = view.findViewById(R.id.fourth_answer_btn)
        viewModel.flags.observe(viewLifecycleOwner) {
            flag = it
            loadFlagImage(flagIv, it.flag)
            val stateList = ColorStateList.valueOf(ContextCompat.getColor(Quizflags.app, R.color.colorPrimary))
            firstAnswerBtn.text = it.options[0]
            firstAnswerBtn.strokeColor = stateList
            firstAnswerBtn.isEnabled = true
            secondAnswerBtn.text = it.options[1]
            secondAnswerBtn.strokeColor = stateList
            secondAnswerBtn.isEnabled = true
            thirdAnswerBtn.text = it.options[2]
            thirdAnswerBtn.strokeColor = stateList
            thirdAnswerBtn.isEnabled = true
            fourthAnswerBtn.text = it.options[3]
            fourthAnswerBtn.strokeColor = stateList
            fourthAnswerBtn.isEnabled = true
        }
        setAnswerButtonListener(firstAnswerBtn)
        setAnswerButtonListener(secondAnswerBtn)
        setAnswerButtonListener(thirdAnswerBtn)
        setAnswerButtonListener(fourthAnswerBtn)
        return view
    }

    private fun setAnswerButtonListener(button: MaterialButton) {
        button.setOnClickListener {
            firstAnswerBtn.isEnabled = false
            secondAnswerBtn.isEnabled = false
            thirdAnswerBtn.isEnabled = false
            fourthAnswerBtn.isEnabled = false
            viewModel.checkAnswer(button.text.toString())
            when {
                firstAnswerBtn.text == flag.name -> {
                    animateBackgroundColorChange(firstAnswerBtn, Color.GREEN)
                }
                secondAnswerBtn.text == flag.name -> {
                    animateBackgroundColorChange(secondAnswerBtn, Color.GREEN)
                }
                thirdAnswerBtn.text == flag.name -> {
                    animateBackgroundColorChange(thirdAnswerBtn, Color.GREEN)
                }
                else -> {
                    animateBackgroundColorChange(fourthAnswerBtn, Color.GREEN)
                }
            }
        }
    }

    private fun animateBackgroundColorChange(button: MaterialButton, toColor: Int) {
        val fromColor = ContextCompat.getColor(Quizflags.app, R.color.colorPrimary)
        val colorAnimation = ValueAnimator.ofArgb(fromColor, toColor)
        colorAnimation.duration = 2500
        colorAnimation.addUpdateListener {
            button.strokeColor = ColorStateList.valueOf(it.animatedValue as Int)
        }
        colorAnimation.doOnEnd {
            if (!viewModel.nextQuestion()) {
                findNavController().navigate(R.id.action_quizFragment_to_scoreFragment)
            }
        }
        colorAnimation.start()
    }

    private fun loadFlagImage(flagIv: ImageView, name: String) {
        val inputStream = Quizflags.app.assets.open(name)
        val drawable = Drawable.createFromStream(inputStream, null)
        flagIv.setImageDrawable(drawable)
    }
}