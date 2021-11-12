package com.example.getitdone.add_update_task

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.getitdone.database.Task
import com.example.getitdone.database.TaskRepository
import java.util.*

class AddUpdateTaskViewModel:ViewModel() {

    val taskRepository = TaskRepository.get()

    private val taskIdLiveData = MutableLiveData<UUID>()


    var taskLiveData: LiveData<Task?> =
        Transformations.switchMap(taskIdLiveData){
            taskRepository.getTask(it)
        }


    fun loadTask (taskId:UUID){
        taskIdLiveData.value = taskId
    }

    fun addTask (task:Task){
        taskRepository.addTask(task)
    }


    fun saveUpdates(task: Task){
        taskRepository.updateTask(task)
    }

}