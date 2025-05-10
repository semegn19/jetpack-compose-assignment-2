@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.myapplication

import android.os.Bundle
import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.theme.MyApplicationTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isDarkTheme by rememberSaveable { mutableStateOf(false) }
            MyApplicationTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                val backStack by navController.currentBackStackEntryAsState()
                val showBack = backStack?.destination?.route == "courses"

                Scaffold(
                    topBar = {
                        MyTopBar(
                            title = "My Courses",
                            showBack = showBack,
                            isDark = isDarkTheme,
                            onBack = { navController.popBackStack() },
                            onToggleDark = { isDarkTheme = it }
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.background
                ) { padding ->
                    Box(
                        Modifier
                            .padding(padding)
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = "welcome",
                            modifier = Modifier.fillMaxSize()
                        ) {
                            composable("welcome") {
                                WelcomeScreen(onContinue = { navController.navigate("courses") })
                            }
                            composable("courses") {
                                CoursesScreen(courses = sampleCourses())
                            }
                        }
                    }
                }
            }
        }
    }
}

// Reusable Top Bar

@Composable
fun MyTopBar(
    title: String,
    showBack: Boolean,
    isDark: Boolean,
    onBack: () -> Unit,
    onToggleDark: (Boolean) -> Unit
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (showBack) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        actions = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Dark Mode", color = MaterialTheme.colorScheme.onPrimary)
                Spacer(Modifier.width(8.dp))
                Switch(
                    checked = isDark,
                    onCheckedChange = onToggleDark,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        uncheckedThumbColor = MaterialTheme.colorScheme.onPrimary,
                        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                        uncheckedTrackColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.24f)
                    )
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@Composable
fun WelcomeScreen(onContinue: () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(top = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Welcome to My Courses", style = MaterialTheme.typography.headlineLarge)
            Spacer(Modifier.height(16.dp))
            Button(onClick = onContinue) { Text("List of Courses") }
        }
    }
}

@Composable
fun CoursesScreen(courses: List<Course>) {
    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(courses) { course ->
            ExpandableCourseCard(course)
        }
    }
}

@Composable
fun ExpandableCourseCard(course: Course) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(course.title, style = MaterialTheme.typography.titleMedium)
                    Text(course.code, style = MaterialTheme.typography.bodyMedium)
                    Text("Credits: ${course.credits}", style = MaterialTheme.typography.bodyMedium)
                }
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                        contentDescription = if (expanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ),
                exit = shrinkVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
            ) {
                Column(Modifier.padding(top = 8.dp)) {
                    Text(course.description, style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(4.dp))
                    Text("Prerequisites: ${course.prerequisites}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

// Data model

data class Course(
    val title: String,
    val code: String,
    val credits: Int,
    val description: String,
    val prerequisites: String
)

fun sampleCourses() = listOf(
    Course("Intro to Programming", "CS101", 3, "Dive in to the world of programming with this introductory course", "A foundational understanding of computer science"),
    Course("Data Structures", "CS201", 4, "Learn the fundamentals of data structures and algorithms", "CS101"),
    Course("Operating Systems", "CS301", 4, "Explore the world of operating systems and concurrency management systems", "CS201"),
    Course("Human Computer Interaction", "CS401", 4, "Learn how to design and develop user-friendly interfaces", "CS101, A foundational understanding of computer science"),
    Course("Artificial Intelligence", "CS501", 4, "Explore the intersection of computer science and artificial intelligence", "CS201"),
    Course("Computer Networks", "CS601", 4, "Study the fundamental concepts of computer networks", "CS301"),
    Course("Software Engineering", "CS701", 4, "Learn the principles of software engineering", "CS401"),
    Course("Database Systems", "CS801", 4, "Explore the design and implementation of database systems", "CS501")
)

// Previews for WelcomeScreen
@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Welcome Light", showBackground = true)
@Composable
fun PreviewWelcomeLight() {
    MyApplicationTheme(darkTheme = false) {
        Scaffold(
            topBar = { MyTopBar("My Courses", showBack = false, isDark = false, onBack = {}, onToggleDark = {}) },
            containerColor = MaterialTheme.colorScheme.background
        ) { padding ->
            Box(Modifier.padding(padding)) {
                WelcomeScreen(onContinue = {})
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Welcome Dark", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun PreviewWelcomeDark() {
    MyApplicationTheme(darkTheme = true) {
        Scaffold(
            topBar = { MyTopBar("My Courses", showBack = false, isDark = true, onBack = {}, onToggleDark = {}) },
            containerColor = MaterialTheme.colorScheme.background
        ) { padding ->
            Box(Modifier.padding(padding)) {
                WelcomeScreen(onContinue = {})
            }
        }
    }
}

// Previews for CoursesScreen
@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Courses Light", showBackground = true)
@Composable
fun PreviewCoursesLight() {
    MyApplicationTheme(darkTheme = false) {
        Scaffold(
            topBar = { MyTopBar("My Courses", showBack = true, isDark = false, onBack = {}, onToggleDark = {}) },
            containerColor = MaterialTheme.colorScheme.background
        ) { padding ->
            Box(Modifier.padding(padding)) {
                CoursesScreen(sampleCourses())
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Courses Dark", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun PreviewCoursesDark() {
    MyApplicationTheme(darkTheme = true) {
        Scaffold(
            topBar = { MyTopBar("My Courses", showBack = true, isDark = true, onBack = {}, onToggleDark = {}) },
            containerColor = MaterialTheme.colorScheme.background
        ) { padding ->
            Box(Modifier.padding(padding)) {
                CoursesScreen(sampleCourses())
            }
        }
    }
}
