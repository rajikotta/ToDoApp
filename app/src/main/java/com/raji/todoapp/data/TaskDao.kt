package com.raji.todoapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM task_table WHERE name LIKE '%'||:query  ||'%' ORDER BY important DESC ")
    fun getTasks(query: String): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)
}