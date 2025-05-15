package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.myapplication.ui.TodoDetailScreen
import com.example.myapplication.ui.TodoListScreen
import com.example.myapplication.viewmodel.TodoDetailViewModel
import com.example.myapplication.viewmodel.TodoListViewModel
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val listVm: TodoListViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Persist theme across rotations & process death
            var isDark by rememberSaveable { mutableStateOf(false) }
            // Show dialog when ViewModel.error != null
            var showNetworkError by rememberSaveable { mutableStateOf(false) }

            MyApplicationTheme(darkTheme = isDark) {
                val navController = rememberNavController()
                val backStack     by navController.currentBackStackEntryAsState()
                val route         = backStack?.destination?.route ?: "list"
                val showBack      = route.startsWith("detail")

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(if (route=="list") "My ToDos" else "Detail") },
                            navigationIcon = {
                                if (showBack) {
                                    IconButton(onClick = { navController.popBackStack() }) {
                                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                                    }
                                }
                            },
                            actions = {
                                Row(
                                    Modifier.padding(end = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Dark", color = MaterialTheme.colorScheme.onPrimary)
                                    Switch(
                                        checked = isDark,
                                        onCheckedChange = { isDark = it }
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor             = MaterialTheme.colorScheme.primary,
                                titleContentColor          = MaterialTheme.colorScheme.onPrimary,
                                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                                actionIconContentColor     = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
                    NavHost(
                        navController   = navController,
                        startDestination= "list",
                        modifier        = Modifier.padding(innerPadding)
                    ) {
                        composable("list") {
                            // Collect UI state
                            val state by listVm.uiState.collectAsState()
                            // Show dialog whenever error pops in
                            LaunchedEffect(state.error) {
                                showNetworkError = state.error != null
                            }

                            TodoListScreen(
                                todos      = state.todos,
                                isLoading  = state.isLoading,
                                onRefresh  = { listVm.refresh() },
                                onItemClick= { id -> navController.navigate("detail/$id") }
                            )
                        }

                        composable(
                            "detail/{todoId}",
                            arguments = listOf(navArgument("todoId"){ type=NavType.IntType })
                        ) {
                            val todoId = it.arguments!!.getInt("todoId")
                            val detailVm: TodoDetailViewModel = viewModel(
                                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                                        @Suppress("UNCHECKED_CAST")
                                        return TodoDetailViewModel(todoId) as T
                                    }
                                }
                            )
                            val todo by detailVm.todo.collectAsState()
                            todo?.let { TodoDetailScreen(it) }
                        }
                    }

                    if (showNetworkError) {
                        AlertDialog(
                            onDismissRequest = {
                                showNetworkError = false
                                listVm.clearError()
                            },
                            title         = { Text("Network Error") },
                            text          = { Text("Network connection failed. Check your internet.") },
                            confirmButton = {
                                TextButton(onClick = {
                                    showNetworkError = false
                                    listVm.clearError()
                                }) {
                                    Text("OK")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
