package com.example.getitdone.categories

import androidx.lifecycle.ViewModel
import com.example.getitdone.database.Task
import com.example.getitdone.database.TaskRepository
import com.example.getitdone.home.todayDate

class CategoriesViewModel:ViewModel() {

    private val taskRepository = TaskRepository.get()
    val taskLiveData = taskRepository.getTaskByCat(taskCat)

    fun deleteTask(task: Task){
        taskRepository.deleteTask(task)
    }

    fun saveUpdates(task: Task){
        taskRepository.updateTask(task)
    }

}