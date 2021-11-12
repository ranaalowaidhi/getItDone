package com.example.getitdone.add_update_task

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.getitdone.*
import com.example.getitdone.database.Task
import com.example.getitdone.DatePickerFragment
import com.example.getitdone.all_tasks.AllTasksFragment
import com.example.getitdone.home.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*


const val TASK_DATE_KEY = "crime date "

class AddUpdateTaskFragment : Fragment(), DatePickerFragment.DAtePickerCallBack {

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

    private lateinit var taskDate:Date

    var taskCat:String = ""

    var catCount:Int = 0

    private val addUpdateTaskViewModel by lazy { ViewModelProvider(this).get(AddUpdateTaskViewModel::class.java)}


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


        personalCatBtn.setOnClickListener{
            taskCat = "personal"
            if (catCount == 0){
                shoppingCatBtn.visibility = View.GONE
                workCatBtn.visibility = View.GONE
                habitCatBtn.visibility = View.GONE
                catCount = 1
            } else {
                shoppingCatBtn.visibility = View.VISIBLE
                workCatBtn.visibility = View.VISIBLE
                habitCatBtn.visibility = View.VISIBLE
                catCount = 0
            }
        }

        shoppingCatBtn.setOnClickListener {
            taskCat = "shopping"
            if (catCount == 0){
                personalCatBtn.visibility = View.GONE
                workCatBtn.visibility = View.GONE
                habitCatBtn.visibility = View.GONE
                catCount = 1
            } else {
                personalCatBtn.visibility = View.VISIBLE
                workCatBtn.visibility = View.VISIBLE
                habitCatBtn.visibility = View.VISIBLE
                catCount = 0
            }

        }

        workCatBtn.setOnClickListener {
            taskCat = "work"
            if (catCount == 0){
                personalCatBtn.visibility = View.GONE
                shoppingCatBtn.visibility = View.GONE
                habitCatBtn.visibility = View.GONE
                catCount = 1
            } else {
                personalCatBtn.visibility = View.VISIBLE
                shoppingCatBtn.visibility = View.VISIBLE
                habitCatBtn.visibility = View.VISIBLE
                catCount = 0
            }
        }

        habitCatBtn.setOnClickListener {
            taskCat = "habit"
            if (catCount == 0){
                personalCatBtn.visibility = View.GONE
                shoppingCatBtn.visibility = View.GONE
                workCatBtn.visibility = View.GONE
                catCount = 1
            } else {
                personalCatBtn.visibility = View.VISIBLE
                shoppingCatBtn.visibility = View.VISIBLE
                workCatBtn.visibility = View.VISIBLE
                catCount = 0
            }
        }

        return view
    }

    override fun onStart() {
        super.onStart()

        dateBtn.setOnClickListener {
            taskDate = Date()
            val args = Bundle()
            args.putSerializable(TASK_DATE_KEY, taskDate)
            val datePicker = DatePickerFragment()
            datePicker.arguments = args
            datePicker.setTargetFragment(this, 0)

            datePicker.show(this.parentFragmentManager, "date picker")

        }

        addTaskBtn.setOnClickListener {
            if(updateTask =="update"){
                updateTask = ""
                val taskTitle = taskInputEt.text.toString()
                val taskDesc = descInputEt.text.toString()
                task.taskTitle = taskTitle
                task.taskDescription = taskDesc
                task.taskCategory = taskCat
                task.taskDate = taskDate
                addUpdateTaskViewModel.saveUpdates(task)
                val args = Bundle()
                val fragment = TaskFragment()
                fragment.arguments = args
                activity?.let {
                    it.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragmentContainerView,fragment)
                        .commit()
                }
            } else {
                if (dateTv.text.isBlank()){
                    dateTv.text = getString(R.string.pick_date_warrning)
                    dateTv.setTextColor(Color.RED)
                } else {
                    val taskTitle = taskInputEt.text.toString()
                    val taskDesc = descInputEt.text.toString()
                    task.taskTitle = taskTitle
                    task.taskDescription = taskDesc
                    task.taskCategory = taskCat
                    task.taskDate = taskDate
                    addUpdateTaskViewModel.addTask(task)
                    val args = Bundle()
                    val fragment = TaskFragment()
                    fragment.arguments = args
                    activity?.let {
                        it.supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.fragmentContainerView,fragment)
                            .commit()
                    }
                }
            }


        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(updateTask == "update"){
            addUpdateTaskViewModel.taskLiveData.observe(viewLifecycleOwner, Observer{
                it?.let {
                    task = it
                    taskInputEt.setText(it.taskTitle)
                    dateTv.text = DateFormat.format(DATE_FORMAT, it.taskDate)
                    descInputEt.setText(it.taskDescription)
                }
            })
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        task = Task()
        if ( updateTask == "update"){
            val taskID = arguments?.getSerializable(KEY_ID) as UUID
            addUpdateTaskViewModel.loadTask(taskID)
        }


    }

    override fun onDateSelected(date: Date) {
        taskDate = date
        dateTv.text = DateFormat.format(DATE_FORMAT, taskDate)
    }



}