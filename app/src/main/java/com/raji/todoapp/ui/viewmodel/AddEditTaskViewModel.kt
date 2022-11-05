package com.raji.todoapp.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.raji.todoapp.data.Task

class AddEditTaskViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val task = savedStateHandle.get<Task>("task")

    var taskName = savedStateHandle.get<String>("taskName") ?: task?.name ?: ""
        set(value) {
            field = value
            savedStateHandle.set("taskName", field)
        }

    var taskImportance = savedStateHandle.get<Boolean>("taskImportance") ?: task?.important ?: false
        set(value) {
            field = value
            savedStateHandle.set("taskImportance", value)
        }

}
