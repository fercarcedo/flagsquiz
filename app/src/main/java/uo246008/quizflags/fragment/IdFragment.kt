package uo246008.quizflags.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.preference.PreferenceManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import uo246008.quizflags.Quizflags
import uo246008.quizflags.R
import uo246008.quizflags.viewmodel.QuizViewModel

private const val ID_KEY = "USER_ID"

class IdFragment : Fragment() {

    private val viewModel by navGraphViewModels<QuizViewModel>(R.id.quiz_nav_graph)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_id, container, false)
        val idEt = view.findViewById<EditText>(R.id.id_et)
        idEt.setText(PreferenceManager.getDefaultSharedPreferences(Quizflags.app).getString(
            ID_KEY, ""))
        val doneBtn = view.findViewById<FloatingActionButton>(R.id.done_btn)
        idEt.doAfterTextChanged {
            doneBtn.isEnabled = it != null && it.isNotEmpty()
        }
        doneBtn.setOnClickListener {
            PreferenceManager.getDefaultSharedPreferences(Quizflags.app).edit().putString(
                ID_KEY, idEt.text.toString()).apply()
            viewModel.userId = idEt.text.toString()
            findNavController().navigate(R.id.action_idFragment_to_quizFragment)
        }
        return view
    }
}