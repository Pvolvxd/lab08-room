package com.example.lab08.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.lab08.data.Task
import com.example.lab08.data.TaskDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TaskViewModel(private val dao: TaskDao) : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    init {
        viewModelScope.launch {
            _tasks.value = dao.getAllTasks()
        }
    }

    fun addTask(description: String, priority: String = "medium") {
        viewModelScope.launch {
            dao.insertTask(Task(description = description, priority = priority))
            _tasks.value = dao.getAllTasks()
        }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            dao.updateTask(task.copy(isCompleted = !task.isCompleted))
            _tasks.value = dao.getAllTasks()
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            dao.deleteTask(task)
            _tasks.value = dao.getAllTasks()
        }
    }

    fun filterTasks(filter: String) {
        viewModelScope.launch {
            _tasks.value = when (filter) {
                "pending"   -> dao.getPendingTasks()
                "completed" -> dao.getCompletedTasks()
                else        -> dao.getAllTasks()
            }
        }
    }

    fun searchTasks(query: String) {
        viewModelScope.launch {
            _tasks.value = if (query.isBlank()) dao.getAllTasks()
            else dao.searchTasks(query)
        }
    }
}

class TaskViewModelFactory(private val dao: TaskDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}