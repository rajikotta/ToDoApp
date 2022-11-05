package com.raji.todoapp.ui.fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.raji.todoapp.R
import com.raji.todoapp.ui.TasksViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskListFragment : Fragment(R.layout.fragment_tasks) {

    private val taskListViewModel by viewModels<TasksViewModel>()
}