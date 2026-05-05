package com.example.lab08.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TaskDao {

    // Obtener todas las tareas
    @Query("SELECT * FROM tasks")
    suspend fun getAllTasks(): List<Task>

    // Insertar una nueva tarea
    @Insert
    suspend fun insertTask(task: Task)

    // Actualizar tarea
    @Update
    suspend fun updateTask(task: Task)

    // Eliminar todas las tareas
    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()
}