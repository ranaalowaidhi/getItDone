package com.example.getitdone.all_tasks

import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.getitdone.R
import com.example.getitdone.SwipeToDeleteCallBack
import com.example.getitdone.add_update_task.AddUpdateTaskFragment
import com.example.getitdone.database.Task
import com.example.getitdone.home.*
import java.text.SimpleDateFormat
import java.util.*


class AllTasksFragment : Fragment() {
    lateinit var  dataFormat:Date
    lateinit var taskRecyclerView: RecyclerView
    lateinit var dueTasksLo: LinearLayout
    lateinit var dueTasksTv: TextView
    private val allTaskViewModel by lazy { ViewModelProvider(this).get(AllTaskViewModel::class.java) }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_all_tasks, container, false)
        taskRecyclerView = view.findViewById(R.id.due_task_rcv)
        val linearLayoutManager = LinearLayoutManager(context)
        taskRecyclerView.layoutManager = linearLayoutManager

        dueTasksLo = view.findViewById(R.id.due_task_lo)
        dueTasksTv = view.findViewById(R.id.due_task_tv)

        return view
    }

    private fun updateUI(tasks:List<Task>) {
        val taskAdapter = TaskAdapter(tasks)
        taskRecyclerView.adapter = taskAdapter

    }


    var mytasks = listOf<Task>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        allTaskViewModel.taskLiveDataAll.observe(viewLifecycleOwner, androidx.lifecycle.Observer { tasks ->
            tasks?.let {
                updateUI(tasks)
                mytasks = it
            }
        })

        val swipeToDeleteCallBack = object : SwipeToDeleteCallBack(requireContext()){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val task = mytasks[viewHolder.adapterPosition]
                when(direction) {
                    ItemTouchHelper.LEFT -> allTaskViewModel.deleteTask(task)
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
        var counter = 0
        val taskTitleTv: TextView = itemView.findViewById(R.id.task_title_tv)
        val taskTagColor: ImageView = itemView.findViewById(R.id.task_cat_tag_color)
        val taskCheckBox: ImageView = itemView.findViewById(R.id.task_check_box)
        val taskDateTv: TextView = itemView.findViewById(R.id.task_date_tv)
        val taskItem: ConstraintLayout = itemView.findViewById(R.id.task_item)
        val taskDescTv: TextView = itemView.findViewById(R.id.task_desc_tv)


        fun bind(task: Task, counter: Int){
            this.task = task
            this.counter = counter
            task.taskDate = formatDate(task.taskDate)
            val checkDate = Date()

            if (task.taskDate.before(formatDate(checkDate))){
                counter - 1
                if (counter > 0){
                    dueTasksLo.visibility = View.VISIBLE
                    dueTasksTv.visibility = View.VISIBLE
                    dueTasksTv.text = "You Have $counter Tasks Over Due!"
                Log.d("Rana","$counter")
                } else if (counter < 0) {
                    dueTasksLo.visibility = View.INVISIBLE
                    dueTasksTv.visibility = View.INVISIBLE
                }
            }
                taskTitleTv.text = task.taskTitle
                taskDateTv.text = DateFormat.format(DATE_FORMAT, task.taskDate)
                taskDescTv.visibility = View.GONE
                if (task.isChecked == 1){
                    taskCheckBox.setImageResource(R.drawable.ic_checked)
                }

                when(task.taskCategory){
                    "personal" -> taskTagColor.setImageResource(R.drawable.cat_tag_personal)
                    "shopping" -> taskTagColor.setImageResource(R.drawable.cat_tag_shopping)
                    "work" -> taskTagColor.setImageResource(R.drawable.cat_tag_work)
                    "habit" -> taskTagColor.setImageResource(R.drawable.cat_tag_habit)
                }

                taskCheckBox.setOnClickListener{
                    if (isCheckedDef == 0){
                        task.isChecked = 1
                        allTaskViewModel.saveUpdates(task)
                        taskCheckBox.setImageResource(R.drawable.ic_checked)
                        isCheckedDef = 1
                    } else {
                        task.isChecked = 0
                        allTaskViewModel.saveUpdates(task)
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
        RecyclerView.Adapter<AllTasksFragment.TaskViewHolder>() {
        var counter = 1

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllTasksFragment.TaskViewHolder {
            val view = layoutInflater.inflate(R.layout.home_task_item, parent,false)
            return TaskViewHolder(view)
        }

        override fun onBindViewHolder(holder: AllTasksFragment.TaskViewHolder, position: Int) {
            val task = tasks[position]
            counter +=1
            Log.d("from bind holder","hi $counter")
            holder.bind(task,counter)


        }

        override fun getItemCount(): Int {
            return tasks.size
        }
    }

    private fun formatDate(fotmatDate: Date):Date{
        val sdf = SimpleDateFormat("dd/MM/yyyy")
       return sdf.parse(sdf.format(fotmatDate))
    }


}