package com.example.getitdone
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.getitdone.database.Task
import com.example.getitdone.database.TaskRepository
import java.util.*


class TaskViewModel:ViewModel() {


        private val taskRepository = TaskRepository.get()
    val taskLiveData = taskRepository.getTaskByDate(todayDate)

    fun addTask(task: Task) {
        taskRepository.addTask(task)
    }



}