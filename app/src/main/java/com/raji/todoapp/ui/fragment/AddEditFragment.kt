package com.raji.todoapp.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
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

            editTextTaskName.addTextChangedListener {
                addEditTasksViewModel.taskName = it.toString()
            }

            checkBoxImportant.setOnCheckedChangeListener { _, isChecked ->
                addEditTasksViewModel.taskImportance = isChecked
            }

            fabSaveTask.setOnClickListener {
                addEditTasksViewModel.onSaveClick()
            }
        }


        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            addEditTasksViewModel.addEditTaskEvent.collect { event ->
                when (event) {
                    is AddEditTaskViewModel.AddEditTaskEvent.ShowInvalidInputMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_LONG).show()
                    }
                    is AddEditTaskViewModel.AddEditTaskEvent.NavigateBackWithResult -> {
                        binding.editTextTaskName.clearFocus()
                        setFragmentResult(
                            "add_edit_request",
                            bundleOf("add_edit_result" to event.result)
                        )
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }
}