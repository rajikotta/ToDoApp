package com.raji.todoapp.ui

import androidx.lifecycle.ViewModel
import com.raji.todoapp.data.TaskDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(private val taskDao: TaskDao) : ViewModel() {
}