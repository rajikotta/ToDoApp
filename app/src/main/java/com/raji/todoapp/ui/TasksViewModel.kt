package com.raji.todoapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.raji.todoapp.data.PreferenceManager
import com.raji.todoapp.data.Task
import com.raji.todoapp.data.TaskDao
import com.raji.todoapp.ui.TaskEvent.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    val searchQuery = MutableStateFlow("")

    val preferenceFlow = preferenceManager.preferenceFlow

    private val taskEventChannel = Channel<TaskEvent>()
    val taskEvent = taskEventChannel.consumeAsFlow()

    private val tasksFlow =
        combine(searchQuery, preferenceFlow) { query, preferences ->
            Pair(query, preferences)
        }.flatMapLatest { (sortOrder, filterPreferences) ->
            taskDao.getTasks(
                sortOrder,
                filterPreferences.sortOrder,
                filterPreferences.hideCompleted
            )
        }

    fun onSortSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferenceManager.updateSortOrder(sortOrder)
    }

    fun onHideCompleteClicked(hideCompleted: Boolean) = viewModelScope.launch {
        preferenceManager.updateHideCompleted(hideCompleted)
    }

    fun onTaskSelected(task: Task) {

    }

    fun onTaskCheckedChanged(task: Task, checked: Boolean) = viewModelScope.launch {
        taskDao.update(
            task.copy(
                completed = checked
            )
        )
    }

    fun onTaskSwiped(task: Task) = viewModelScope.launch {
        taskDao.delete(task)
        taskEventChannel.send(ShowUndoDeleteTaskMessage(task))
    }

    fun undoDeletedTask(task: Task) = viewModelScope.launch {
        taskDao.insert(task)
    }


    val tasks = tasksFlow.asLiveData()

}

enum class SortOrder {
    BY_NAME, BY_DATE
}


sealed class TaskEvent {
    data class ShowUndoDeleteTaskMessage(val task: Task) : TaskEvent()
}