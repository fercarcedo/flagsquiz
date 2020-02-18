package uo246008.quizflags.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import uo246008.quizflags.R

class IntroFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_intro, container, false)
        val doneBtn = view.findViewById<FloatingActionButton>(R.id.done_btn)
        doneBtn.setOnClickListener {
            findNavController().navigate(R.id.action_introFragment_to_quiz_nav_graph)
        }
        return view
    }
}