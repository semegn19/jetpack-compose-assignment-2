package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.TodoEntity
import com.example.myapplication.repository.TodoRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class TodoListUiState(
    val todos: List<TodoEntity> = emptyList(),
    val isLoading: Boolean       = false,
    val error: String?           = null
)

class TodoListViewModel : ViewModel() {
    private val _state = MutableStateFlow(TodoListUiState(isLoading = true))
    val uiState: StateFlow<TodoListUiState> = _state.asStateFlow()

    init {
        // 1) Observe Room cache immediately
        viewModelScope.launch {
            TodoRepository.cachedTodos()
                .onEach { list ->
                    _state.update { it.copy(todos = list, isLoading = false) }
                }
                .launchIn(this)
        }
        // 2) Attempt a network refresh
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                TodoRepository.refreshFromNetwork()
            } catch (e: Exception) {
                _state.update { it.copy(error = "Network failed", isLoading = false) }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
