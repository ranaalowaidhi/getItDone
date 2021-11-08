package com.example.getitdone.add_update_task

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.getitdone.*
import com.example.getitdone.database.Task
import java.util.*

const val TASK_DATE_KEY = "crime date "

class AddUpdateTaskFragment : Fragment(),DatePickerFragment.DAtePickerCallBack {

    private lateinit var taskInputEt: EditText
    private lateinit var descInputEt: EditText
    private lateinit var personalCatBtn: LinearLayout
    private lateinit var shoppingCatBtn: LinearLayout
    private lateinit var workCatBtn: LinearLayout
    private lateinit var habitCatBtn: LinearLayout
    private lateinit var dateBtn: LinearLayout
    private lateinit var dateTv: TextView
    private lateinit var addTaskBtn: Button
    private lateinit var task: Task

    var taskCat:String = ""
    var taskDate = Date()

    var catCount:Int = 0

    private val addUpdateTaskViewModel by lazy { ViewModelProvider(this).get(AddUpdateTaskViewModel::class.java)}
    private val taskViewModel by lazy { ViewModelProvider(this).get(TaskViewModel::class.java) }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view  = inflater.inflate(R.layout.fragment_add_update_task, container, false)

        taskInputEt = view.findViewById(R.id.task_in_et)
        descInputEt = view.findViewById(R.id.desc_in_et)
        personalCatBtn = view.findViewById(R.id.personal_cat_task)
        shoppingCatBtn = view.findViewById(R.id.shopping_cat_task)
        workCatBtn = view.findViewById(R.id.work_cat_task)
        habitCatBtn = view.findViewById(R.id.habit_cat_task)
        dateBtn = view.findViewById(R.id.date_btn)
        dateTv = view.findViewById(R.id.date_tv)
        addTaskBtn = view.findViewById(R.id.add_task_btn)

        dateTv.apply {
            text = DateFormat.format(DATE_FORMAT, taskDate)
        }

        personalCatBtn.setOnClickListener{
            taskCat = "personal"
            if (catCount == 0){
                shoppingCatBtn.isVisible = false
                workCatBtn.isVisible = false
                habitCatBtn.isVisible = false
                catCount = 1
            } else {
                shoppingCatBtn.isVisible = true
                workCatBtn.isVisible = true
                habitCatBtn.isVisible = true
                catCount = 0
            }
        }

        shoppingCatBtn.setOnClickListener {
            taskCat = "shopping"
            val color = Color.GRAY
            shoppingCatBtn.backgroundTintList = ColorStateList.valueOf(color)

        }

        workCatBtn.setOnClickListener {
            taskCat = "work"
            val color = Color.GRAY
            workCatBtn.backgroundTintList = ColorStateList.valueOf(color)
        }

        habitCatBtn.setOnClickListener {
            taskCat = "habit"
            val color = Color.GRAY
            habitCatBtn.backgroundTintList = ColorStateList.valueOf(color)
        }





        return view
    }

    override fun onStart() {
        super.onStart()

        dateBtn.setOnClickListener {

            val args = Bundle()
            args.putSerializable(TASK_DATE_KEY, taskDate)
            val datePicker = DatePickerFragment()
            datePicker.arguments = args
            datePicker.setTargetFragment(this, 0)

            datePicker.show(this.parentFragmentManager, "date picker")

        }

        addTaskBtn.setOnClickListener {
            val taskTitle = taskInputEt.text.toString()
            val taskDesc = descInputEt.text.toString()
            task.taskTitle = taskTitle
            task.taskDescription = taskDesc
            task.taskCategory = taskCat
            task.taskDate = taskDate

            taskViewModel.addTask(task)

            val args = Bundle()
            val fragment = TaskFragment()
            fragment.arguments = args
            activity?.let {
                it.supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragmentContainerView,fragment)
                    .commit()
                addUpdateTaskViewModel.saveUpdates(task)
            }

        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addUpdateTaskViewModel.taskLiveData.observe(viewLifecycleOwner, Observer{
            it?.let {
                task = it
                taskInputEt.setText(it.taskTitle)
                dateTv.text = it.taskDate.toString()
                descInputEt.setText(it.taskDescription)
            }
        })

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        task = Task()

    }

    override fun onDateSelected(date: Date) {
        taskDate = date
        dateTv.text = DateFormat.format(DATE_FORMAT, taskDate)
    }

}