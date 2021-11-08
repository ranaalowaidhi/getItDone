package com.example.getitdone.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.Executors

private const val DATABASE_NAME = "task-database"
class TaskRepository private constructor(context: Context) {

    private val database : TaskDatabase = Room.databaseBuilder(
        context.applicationContext,
        TaskDatabase::class.java,
        DATABASE_NAME
    ).build()

    private val taskDao = database.taskDao()


    fun getAllTask(): LiveData<List<Task>> = taskDao.getAllTask()

    private val executor = Executors.newSingleThreadExecutor()

    fun getTask(id: UUID):LiveData<Task?>{
        return taskDao.getTask(id)
    }

    fun getTaskByDate(date:Date): LiveData<List<Task>>{
        return taskDao.getTaskByDate(date)
    }

    fun updateTask(task:Task){
        executor.execute {
            taskDao.updateTask(task)
        }
    }

    fun addTask(task: Task) {
        executor.execute{
            taskDao.addTask(task)
        }
    }

    companion object {
        var INSTANCE: TaskRepository? = null
        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = TaskRepository(context)
            }
        }

        fun get():TaskRepository{
            return INSTANCE ?: throw IllegalStateException("TaskRepository must initialize")
        }
    }

}

