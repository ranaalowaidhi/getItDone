package com.example.getitdone.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import java.util.*


@Dao

interface TaskDao {
    @Query("SELECT * FROM Task")
    fun getAllTask(): LiveData<List<Task>>
    @Query("SELECT * FROM Task WHERE id=(:id)")
    fun getTask(id: UUID): LiveData<Task?>
    @Query("SELECT*FROM Task WHERE taskDate=(:date)")
    fun getTaskByDate(date:Date): LiveData<List<Task>>
    @Update
    fun updateTask(task:Task)
    @Insert
    fun addTask(task:Task)
}