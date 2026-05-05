package com.example.lab08

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lab08.data.TaskDatabase
import com.example.lab08.ui.theme.Lab08Theme
import com.example.lab08.viewmodel.TaskViewModel
import com.example.lab08.viewmodel.TaskViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab08Theme {
                val database = TaskDatabase.getDatabase(applicationContext)
                val factory = TaskViewModelFactory(database.taskDao())
                val viewModel: TaskViewModel = viewModel(factory = factory)

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TaskScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun TaskScreen(viewModel: TaskViewModel, modifier: Modifier = Modifier) {

    val tasks by viewModel.tasks.collectAsState()
    var newTaskDescription by remember { mutableStateOf("") }
    var searchText by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("all") }
    var selectedPriority by remember { mutableStateOf("medium") }

    val priorityColor = mapOf(
        "high" to Color(0xFFE24B4A),
        "medium" to Color(0xFFEF9F27),
        "low" to Color(0xFF639922)
    )

    Column(modifier = modifier.fillMaxSize()) {

        // ── Header estilo TickTick ──────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF4A6CF7))
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "Mis tareas",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val pending = tasks.count { !it.isCompleted }
                    val done = tasks.count { it.isCompleted }
                    listOf(
                        "Pendientes: $pending",
                        "Hechas: $done"
                    ).forEach { label ->
                        Surface(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = label,
                                color = Color.White,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(
                                    horizontal = 10.dp,
                                    vertical = 4.dp
                                )
                            )
                        }
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(12.dp)) {

            // ── Barra de búsqueda ───────────────────────
            OutlinedTextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                    viewModel.searchTasks(it)
                },
                label = { Text("Buscar tarea...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ── Chips de filtro ─────────────────────────
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(
                    "all" to "Todas",
                    "pending" to "Pendientes",
                    "completed" to "Hechas"
                ).forEach { (key, label) ->
                    FilterChip(
                        selected = selectedFilter == key,
                        onClick = {
                            selectedFilter = key
                            viewModel.filterTasks(key)
                        },
                        label = { Text(label, fontSize = 12.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── Lista de tareas ─────────────────────────
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(tasks) { task ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Punto de prioridad
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(
                                        priorityColor[task.priority] ?: Color.Gray
                                    )
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            // Texto de la tarea
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = task.description,
                                    style = if (task.isCompleted)
                                        MaterialTheme.typography.bodyMedium.copy(
                                            textDecoration = TextDecoration.LineThrough,
                                            color = MaterialTheme.colorScheme.onSurface
                                                .copy(alpha = 0.4f)
                                        )
                                    else MaterialTheme.typography.bodyMedium
                                )
                            }

                            // Botón completar
                            TextButton(
                                onClick = { viewModel.toggleTaskCompletion(task) }
                            ) {
                                Text(
                                    text = if (task.isCompleted) "✓" else "○",
                                    color = if (task.isCompleted) Color(0xFF4A6CF7)
                                    else MaterialTheme.colorScheme.onSurface
                                        .copy(alpha = 0.4f),
                                    fontSize = 18.sp
                                )
                            }

                            // Botón eliminar
                            IconButton(
                                onClick = { viewModel.deleteTask(task) }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Eliminar",
                                    tint = Color(0xFFE24B4A)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Selector de prioridad ───────────────────
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(
                    "high" to "Alta",
                    "medium" to "Media",
                    "low" to "Baja"
                ).forEach { (key, label) ->
                    FilterChip(
                        selected = selectedPriority == key,
                        onClick = { selectedPriority = key },
                        label = { Text(label, fontSize = 12.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // ── Agregar nueva tarea ─────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = newTaskDescription,
                    onValueChange = { newTaskDescription = it },
                    label = { Text("Nueva tarea") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                Button(
                    onClick = {
                        if (newTaskDescription.isNotBlank()) {
                            viewModel.addTask(newTaskDescription, selectedPriority)
                            newTaskDescription = ""
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Text("Agregar")
                }
            }
        }
    }
}