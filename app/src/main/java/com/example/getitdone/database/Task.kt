package com.example.getitdone.database
import android.app.ActivityManager
import android.location.Address
import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Task (
    @PrimaryKey var id: UUID = UUID.randomUUID(),
    var taskTitle:String = "",
    var taskDescription:String = "",
    var taskCategory:String = "",
    var taskDate:Date = Date(),
    var creationDate:Date = Date(),
    var isChecked:Int = 0,
    var taskLocation:String="",
    var taskAddress:String =""){
}