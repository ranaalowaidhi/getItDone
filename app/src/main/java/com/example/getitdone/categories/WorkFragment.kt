package com.example.getitdone.categories

import android.os.Bundle
import android.text.format.DateFormat
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

class WorkFragment : Fragment() {

    lateinit var taskRecyclerView: RecyclerView
    private val taskViewModel by lazy { ViewModelProvider(this).get(CategoriesViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_work, container, false)

        taskCat = "work"

        taskRecyclerView = view.findViewById(R.id.work_task_rcv)
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

        taskViewModel.taskLiveData.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { tasks ->
                tasks?.let {
                    updateUI(tasks)
                    mytasks = it
                }
            })

        val swipeToDeleteCallBack = object : SwipeToDeleteCallBack(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val task = mytasks[viewHolder.adapterPosition]
                when (direction) {
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

    inner class TaskViewHolder(view:View):RecyclerView.ViewHolder(view){

        private lateinit var task:Task
        val taskTitleTv: TextView = itemView.findViewById(R.id.duo_title_tv)
        val taskTagColor: ImageView = itemView.findViewById(R.id.duo_cat_tag_color)
        val taskCheckBox: ImageView = itemView.findViewById(R.id.task_check_box)
        val taskDateTv: TextView = itemView.findViewById(R.id.duo_date_tv)
        val taskItem: ConstraintLayout = itemView.findViewById(R.id.task_item)
        val taskDescTv: TextView = itemView.findViewById(R.id.task_desc_tv)
        val taskLoctionTv: TextView = itemView.findViewById(R.id.det_location_tv)
        val taskAddressTv: TextView = itemView.findViewById(R.id.det_address_tv)
        val locationTitleTv: TextView = itemView.findViewById(R.id.location_title_tv)
        val itemRoot: ConstraintLayout = itemView.findViewById(R.id.item_root)


        fun bind(task: Task){
            this.task = task
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
                "personal" -> context?.let { taskTagColor.setColorFilter(it.getColor(R.color.pink)) }
                "shopping" -> context?.let { taskTagColor.setColorFilter(it.getColor(R.color.yellow)) }
                "work" -> context?.let { taskTagColor.setColorFilter(it.getColor(R.color.green)) }
                "habit" -> context?.let { taskTagColor.setColorFilter(it.getColor(R.color.orange)) }
            }

            taskCheckBox.setOnClickListener{
                if (isCheckedDef == 0){
                    task.isChecked = 1
                    taskViewModel.saveUpdates(task)
                    taskCheckBox.setImageResource(R.drawable.ic_checked)
                    isCheckedDef = 1
                } else {
                    task.isChecked = 0
                    taskViewModel.saveUpdates(task)
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

    private inner class TaskAdapter(var tasks:List<Task>):
        RecyclerView.Adapter<WorkFragment.TaskViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkFragment.TaskViewHolder {
            val view = layoutInflater.inflate(R.layout.home_task_item, parent,false)
            return TaskViewHolder(view)
        }

        override fun onBindViewHolder(holder: WorkFragment.TaskViewHolder, position: Int) {
            val task = tasks[position]
            holder.bind(task)
        }

        override fun getItemCount(): Int {
            return tasks.size
        }
    }

}