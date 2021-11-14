package com.example.getitdone.all_tasks

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
import com.google.firebase.database.collection.LLRBNode
import java.text.SimpleDateFormat
import java.util.*


class AllTasksFragment : Fragment() {
    lateinit var taskRecyclerView: RecyclerView
    private val allTaskViewModel by lazy { ViewModelProvider(this).get(AllTaskViewModel::class.java) }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_all_tasks, container, false)
        taskRecyclerView = view.findViewById(R.id.all_task_rcv)
        val linearLayoutManager = LinearLayoutManager(context)
        taskRecyclerView.layoutManager = linearLayoutManager


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
//        var counter = 0
        val taskTitleTv: TextView = itemView.findViewById(R.id.duo_title_tv)
        val taskTagColor: ImageView = itemView.findViewById(R.id.duo_cat_tag_color)
        val taskCheckBox: ImageView = itemView.findViewById(R.id.task_check_box)
        val taskDateTv: TextView = itemView.findViewById(R.id.duo_date_tv)
        val taskItem: ConstraintLayout = itemView.findViewById(R.id.task_item)
        val taskDescTv: TextView = itemView.findViewById(R.id.task_desc_tv)
        val taskLoctionTv:TextView = itemView.findViewById(R.id.det_location_tv)
        val taskAddressTv:TextView = itemView.findViewById(R.id.det_address_tv)
        val locationTitleTv:TextView = itemView.findViewById(R.id.location_title_tv)

        @SuppressLint("ResourceAsColor")
        fun bind(task: Task){
            this.task = task
//            this.counter = counter
            task.taskDate = formatDate(task.taskDate)
            val checkDate = Date()
//
//            if (task.taskDate.before(formatDate(checkDate))){
//                this@TaskViewHolder.counter = counter - 1
//                if (counter > 0){
//                    dueTasksLo.visibility = View.VISIBLE
//                    dueTasksTv.visibility = View.VISIBLE
//                    dueTasksTv.text = "You Have $counter Tasks Over Due!"
//                Log.d("Rana","$counter")
//                } else if (counter < 0) {
//                    dueTasksLo.visibility = View.INVISIBLE
//                    dueTasksTv.visibility = View.INVISIBLE
//                }
//            }
            if (task.taskDate.before(formatDate(checkDate))){
                taskTitleTv.text = task.taskTitle
                taskDateTv.text = "Over Duo"
                context?.let { taskDateTv.setTextColor(it.getColor(R.color.red)) }
                context?.let { taskItem.setBackgroundColor(it.getColor(R.color.gray)) }
                taskDescTv.visibility = View.GONE
                taskLoctionTv.visibility = View.GONE
                taskAddressTv.visibility = View.GONE
                locationTitleTv.visibility = View.GONE
                taskCheckBox.visibility = View.GONE
            }else{
                taskTitleTv.text = task.taskTitle
                taskDateTv.text = DateFormat.format(DATE_FORMAT, task.taskDate)
                taskDescTv.visibility = View.GONE
                taskLoctionTv.visibility = View.GONE
                taskAddressTv.visibility = View.GONE
                locationTitleTv.visibility = View.GONE
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
                        locationTitleTv.visibility = View.VISIBLE
                        taskLoctionTv.visibility = View.VISIBLE
                        taskLoctionTv.text = task.taskLocation
                        taskAddressTv.visibility =View.VISIBLE
                        taskAddressTv.text = task.taskAddress
                    } else if (viewIsClicked == 1){
                        taskDescTv.visibility = View.GONE
                        locationTitleTv.visibility = View.GONE
                        taskLoctionTv.visibility = View.GONE
                        taskAddressTv.visibility = View.GONE
                        viewIsClicked = 0

                    }
                }
            }

        }

    }



    private inner class TaskAdapter(var tasks:List<Task>):
        RecyclerView.Adapter<AllTasksFragment.TaskViewHolder>() {
        var counter = 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllTasksFragment.TaskViewHolder {
            val view = layoutInflater.inflate(R.layout.home_task_item, parent,false)
            return TaskViewHolder(view)
        }

        override fun onBindViewHolder(holder: AllTasksFragment.TaskViewHolder, position: Int) {
            val task = tasks[position]
            counter = tasks.size
            Log.d("Rana","hi $counter")
            holder.bind(task)


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