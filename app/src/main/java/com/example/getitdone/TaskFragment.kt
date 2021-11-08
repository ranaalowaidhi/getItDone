package com.example.getitdone

import android.os.Bundle
import android.text.format.DateFormat.format
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.getitdone.add_update_task.AddUpdateTaskFragment
import com.example.getitdone.add_update_task.AddUpdateTaskViewModel
import com.example.getitdone.database.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*

const val KEY_ID = "myTaskId"
const val DATE_FORMAT = "EEE dd MMM yyyy"

var todayDate = Date()


class TaskFragment : Fragment() {

    lateinit var taskRecyclerView: RecyclerView
    private lateinit var fabBtn: FloatingActionButton
    private val taskViewModel by lazy { ViewModelProvider(this).get(TaskViewModel::class.java) }
    private val addUpdateTaskViewModel by lazy { ViewModelProvider(this).get(AddUpdateTaskViewModel::class.java)}



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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        taskViewModel.taskLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer { tasks ->
            tasks?.let {
                updateUI(tasks)
            }
        })

    }

    private inner class TaskViewHolder(view:View):RecyclerView.ViewHolder(view){

        private lateinit var task:Task
        val taskTitleTv: TextView = itemView.findViewById(R.id.task_title_tv)
        val taskTagColor: ImageView = itemView.findViewById(R.id.task_cat_tag_color)
        val taskCheckBox: ImageView = itemView.findViewById(R.id.task_check_box)
        val taskDateTv: TextView = itemView.findViewById(R.id.task_date_tv)


        fun bind(task: Task){
            this.task = task
                taskTitleTv.text = task.taskTitle
                taskDateTv.text = format(DATE_FORMAT,task.taskDate)
            if (task.isChecked){
                taskCheckBox.setImageResource(R.drawable.ic_checked)
            }

            when(task.taskCategory){
                    "personal" -> taskTagColor.setImageResource(R.drawable.cat_tag_personal)
                    "shopping" -> taskTagColor.setImageResource(R.drawable.cat_tag_shopping)
                    "work" -> taskTagColor.setImageResource(R.drawable.cat_tag_work)
                    "habit" -> taskTagColor.setImageResource(R.drawable.cat_tag_habit)
            }

            taskCheckBox.setOnClickListener{
                task.isChecked = true
                addUpdateTaskViewModel.saveUpdates(task)
                taskCheckBox.setImageResource(R.drawable.ic_checked)
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