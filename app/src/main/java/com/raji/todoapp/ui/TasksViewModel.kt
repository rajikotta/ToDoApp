package com.raji.todoapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.raji.todoapp.data.TaskDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapConcat
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(private val taskDao: TaskDao) : ViewModel() {

    val searchQuery = MutableStateFlow("")

    private val tasksFlow = searchQuery.flatMapConcat {
        taskDao.getTasks(it)

    }
    val tasks = tasksFlow.asLiveData()

}