package com.example.getitdone.all_tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.getitdone.database.Task
import com.example.getitdone.database.TaskRepository
import com.example.getitdone.home.todayDate
import java.util.*

class AllTaskViewModel:ViewModel() {

    val taskRepository = TaskRepository.get()

    val taskLiveDataAll = taskRepository.getAllTask()

//    val taskOverDue = taskRepository.getOverDueCount(todayDate)

    fun saveUpdates(task: Task){
        taskRepository.updateTask(task)
    }

    fun deleteTask(task:Task){
        taskRepository.deleteTask(task)
    }

}