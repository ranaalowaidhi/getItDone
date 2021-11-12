package com.example.getitdone.home

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.format.DateFormat.format
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.getitdone.R
import com.example.getitdone.SwipeToDeleteCallBack
import com.example.getitdone.add_update_task.AddUpdateTaskFragment
import com.example.getitdone.add_update_task.AddUpdateTaskViewModel
import com.example.getitdone.all_tasks.AllTasksFragment
import com.example.getitdone.database.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*


const val KEY_ID = "myTaskId"
const val DATE_FORMAT = "EEE dd MMM yyyy"

var todayDate = Date()

var viewIsClicked:Int = 0

var updateTask = ""

var isCheckedDef = 0


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class TaskFragment : Fragment() {

    lateinit var taskRecyclerView: RecyclerView
    private lateinit var fabBtn: FloatingActionButton
        private val taskViewModel by lazy { ViewModelProvider(this).get(TaskViewModel::class.java) }
        private val addUpdateTaskViewModel by lazy { ViewModelProvider(this).get(AddUpdateTaskViewModel::class.java)}



    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)


        val sdf = SimpleDateFormat("dd/MM/yyyy")

        todayDate = sdf.parse(sdf.format(todayDate))



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_task, container, false)

        taskRecyclerView = view.findViewById(R.id.home_task_rcv)
        val linearLayoutManager = LinearLayoutManager(context)
        taskRecyclerView.layoutManager = linearLayoutManager

        fabBtn = view.findViewById(R.id.add_btn)
        fabBtn.setOnClickListener {
            val args = Bundle()
            val fragment = AddUpdateTaskFragment()
            fragment.arguments = args
            activity?.let {
                it.supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragmentContainerView,fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }

        return view
    }

    private fun updateUI(tasks:List<Task>) {
        val taskAdapter = TaskAdapter(tasks)
        taskRecyclerView.adapter = taskAdapter

    }
    var mytasks = listOf<Task>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        taskViewModel.taskLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer { tasks ->
            tasks?.let {
                updateUI(tasks)
                mytasks = it
            }
        })

        val swipeToDeleteCallBack = object : SwipeToDeleteCallBack(requireContext()){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val task = mytasks[viewHolder.adapterPosition]
                when(direction) {
                    ItemTouchHelper.LEFT -> taskViewModel.deleteTask(task)
                    ItemTouchHelper.RIGHT -> {
                        updateTask = "update"
                        val args = Bundle()
                        args.putSerializable(KEY_ID, task.id)
                        val fragment = AddUpdateTaskFragment()
                        fragment.arguments = args
                        activity?.let {
                            it.supportFragmentManager
                                .beginTransaction()
                                .replace(R.id.fragmentContainerView, fragment)
                                .addToBackStack(null)
                                .commit()
                        }
                    }
                }
            }

        }

        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallBack)
        itemTouchHelper.attachToRecyclerView(taskRecyclerView)



    }

    private inner class TaskViewHolder(view:View):RecyclerView.ViewHolder(view){

        private lateinit var task:Task
        val taskTitleTv: TextView = itemView.findViewById(R.id.task_title_tv)
        val taskTagColor: ImageView = itemView.findViewById(R.id.task_cat_tag_color)
        val taskCheckBox: ImageView = itemView.findViewById(R.id.task_check_box)
        val taskDateTv: TextView = itemView.findViewById(R.id.task_date_tv)
        val taskItem:ConstraintLayout = itemView.findViewById(R.id.task_item)
        val taskDescTv:TextView = itemView.findViewById(R.id.task_desc_tv)


        fun bind(task: Task){
            this.task = task
            taskTitleTv.text = task.taskTitle
            taskDateTv.text = format(DATE_FORMAT,task.taskDate)
            taskDescTv.visibility = View.GONE
            if (task.isChecked == 1){
                taskCheckBox.setImageResource(R.drawable.ic_checked)
            }

            when(task.taskCategory){
                    "personal" -> context?.let { taskTagColor.setColorFilter(it.getColor(R.color.pink)) }
                    "shopping" -> context?.let { taskTagColor.setColorFilter(it.getColor(R.color.yellow)) }
                    "work" -> context?.let { taskTagColor.setColorFilter(it.getColor(R.color.green)) }
                    "habit" -> context?.let { taskTagColor.setColorFilter(it.getColor(R.color.orange)) }
            }

            taskCheckBox.setOnClickListener{
                if (isCheckedDef == 0){
                    task.isChecked = 1
                    addUpdateTaskViewModel.saveUpdates(task)
                    taskCheckBox.setImageResource(R.drawable.ic_checked)
                    isCheckedDef = 1
                } else {
                    task.isChecked = 0
                    addUpdateTaskViewModel.saveUpdates(task)
                    taskCheckBox.setImageResource(R.drawable.ic_not_checked)
                    isCheckedDef = 0
                }

            }

            taskItem.setOnClickListener{
                if (viewIsClicked == 0){
                    viewIsClicked = 1
                    taskDescTv.visibility = View.VISIBLE
                    taskDescTv.text = task.taskDescription
                } else if (viewIsClicked == 1){
                    taskDescTv.visibility = View.GONE
                    viewIsClicked = 0

                }
            }

        }

    }

    private inner class TaskAdapter(var tasks:List<Task>):
        RecyclerView.Adapter<TaskViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
            val view = layoutInflater.inflate(R.layout.home_task_item, parent,false)
            return TaskViewHolder(view)
        }

        override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
            val task = tasks[position]
            holder.bind(task)
        }

        override fun getItemCount(): Int {
            return tasks.size
        }
    }



}