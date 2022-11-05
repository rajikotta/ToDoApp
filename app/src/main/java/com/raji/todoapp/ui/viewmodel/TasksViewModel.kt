package com.raji.todoapp.ui.viewmodel

import androidx.lifecycle.*
import com.raji.todoapp.data.PreferenceManager
import com.raji.todoapp.data.Task
import com.raji.todoapp.data.TaskDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val preferenceManager: PreferenceManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val searchQuery = savedStateHandle.getLiveData("search_query","")

    val preferenceFlow = preferenceManager.preferenceFlow

    private val taskEventChannel = Channel<TaskEvent>()
    val taskEvent: Flow<TaskEvent> = taskEventChannel.receiveAsFlow()

    private val tasksFlow =
        combine(searchQuery.asFlow(), preferenceFlow) { query, preferences ->
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

    fun onTaskCheckedChanged(task: Task, checked: Boolean) = viewModelScope.launch {
        taskDao.update(
            task.copy(
                completed = checked
            )
        )
    }

    fun onTaskSwiped(task: Task) = viewModelScope.launch {
        taskDao.delete(task)
        taskEventChannel.send(TaskEvent.ShowUndoDeleteTaskMessage(task))
    }

    fun undoDeletedTask(task: Task) = viewModelScope.launch {
        taskDao.insert(task)
    }


    fun onTaskSelected(task: Task) = viewModelScope.launch {

        taskEventChannel.send(TaskEvent.NavigateToEditTaskScreen(task))

    }

    fun onAddNewTaskClick() = viewModelScope.launch {
        taskEventChannel.send(TaskEvent.NavigateToAddTaskScreen)
    }


    val tasks = tasksFlow.asLiveData()

}

enum class SortOrder {
    BY_NAME, BY_DATE
}


sealed class TaskEvent {
    data class ShowUndoDeleteTaskMessage(val task: Task) : TaskEvent()
    object NavigateToAddTaskScreen : TaskEvent()
    data class NavigateToEditTaskScreen(val task: Task) : TaskEvent()
}