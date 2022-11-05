package com.raji.todoapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.raji.todoapp.data.TaskDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(private val taskDao: TaskDao) : ViewModel() {

    val searchQuery = MutableStateFlow("")
    val sortOrder = MutableStateFlow(SortOrder.BY_DATE)
    val hideCompleted = MutableStateFlow(false)


    private val tasksFlow =
        combine(searchQuery, sortOrder, hideCompleted) { query, sortOrder, completed ->
            Triple(query, sortOrder, completed)
        }.flatMapLatest {
            taskDao.getTasks(it.first, it.second, it.third)

        }

    val tasks = tasksFlow.asLiveData()

}

enum class SortOrder {
    BY_NAME, BY_DATE
}