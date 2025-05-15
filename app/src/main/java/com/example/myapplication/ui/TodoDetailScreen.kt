package com.example.myapplication.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.TodoEntity

@Composable
fun TodoDetailScreen(todo: TodoEntity) {
    Column(Modifier.padding(16.dp)) {
        Text(todo.title, style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text("User ID: ${todo.userId}", style = MaterialTheme.typography.bodyMedium)
        Text("Todo ID:  ${todo.id}",    style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            if (todo.completed) "Status: Completed" else "Status: Pending",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
