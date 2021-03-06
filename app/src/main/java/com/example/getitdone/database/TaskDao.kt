package com.example.getitdone.database

import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.*


@Dao

interface TaskDao {
    @Query("SELECT * FROM Task ORDER BY isChecked  ASC")
    fun getAllTask(): LiveData<List<Task>>
    @Query("SELECT * FROM Task WHERE id=(:id)")
    fun getTask(id: UUID): LiveData<Task?>
    @Query("SELECT*FROM Task WHERE taskDate=(:date) ORDER BY isChecked ASC")
    fun getTaskByDate(date:Date): LiveData<List<Task>>
    @Query("SELECT*FROM Task WHERE taskCategory=(:taskCat) ORDER BY isChecked ASC")
    fun getTaskByCat(taskCat:String): LiveData<List<Task>>
    @Update
    fun updateTask(task: Task)
    @Insert
    fun addTask(task:Task)
    @Delete
    fun deleteTask(task:Task)
}