package com.raji.todoapp.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.raji.todoapp.R
import com.raji.todoapp.databinding.FragmentAddEditBinding
import com.raji.todoapp.ui.viewmodel.AddEditTaskViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEditFragment : Fragment(R.layout.fragment_add_edit) {

    val addEditTasksViewModel by viewModels<AddEditTaskViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentAddEditBinding.bind(view)

        binding.apply {
            editTextTaskName.setText(addEditTasksViewModel.taskName)
            checkBoxImportant.isChecked = addEditTasksViewModel.taskImportance
            checkBoxImportant.jumpDrawablesToCurrentState()
            textViewDateCreated.isVisible = addEditTasksViewModel.task != null
            textViewDateCreated.text =
                "Created: ${addEditTasksViewModel.task?.createdDateFormatted}"
        }
    }
}