package com.example.lab08.data

import androidx.room.*

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks")
    suspend fun getAllTasks(): List<Task>

    @Insert
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()

    @Query("SELECT * FROM tasks WHERE is_completed = 0")
    suspend fun getPendingTasks(): List<Task>

    @Query("SELECT * FROM tasks WHERE is_completed = 1")
    suspend fun getCompletedTasks(): List<Task>

    @Query("SELECT * FROM tasks WHERE description LIKE '%' || :query || '%'")
    suspend fun searchTasks(query: String): List<Task>
}