package com.example.getitdone

import android.app.DatePickerDialog
import android.app.FragmentTransaction
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.example.getitdone.database.Task
import java.util.*

class AddTaskActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView2)
        if (currentFragment == null){
            val fragment =TaskFragment()

            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainerView2,fragment).commit()
        }

    }
}