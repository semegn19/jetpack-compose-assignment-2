// TodoDetailViewModel.kt
package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.TodoEntity
import com.example.myapplication.repository.TodoRepository
import kotlinx.coroutines.flow.*

class TodoDetailViewModel(todoId: Int) : ViewModel() {
    val todo: StateFlow<TodoEntity?> = TodoRepository
        .cachedTodos()
        .map { list -> list.find { it.id == todoId } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)
}
