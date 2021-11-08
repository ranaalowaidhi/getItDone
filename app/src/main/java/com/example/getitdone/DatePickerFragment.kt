package com.example.getitdone

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.getitdone.add_update_task.TASK_DATE_KEY
import java.util.*

class DatePickerFragment:DialogFragment() {

    interface DAtePickerCallBack{
        fun onDateSelected(date: Date)
    }



    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val date = arguments?.getSerializable(TASK_DATE_KEY) as Date
        val calendar = Calendar.getInstance()
        calendar.time = date
        val year =calendar.get(Calendar.YEAR)
        val month =calendar.get(Calendar.MONTH)
        val day =calendar.get(Calendar.DAY_OF_MONTH)

        val dateListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->

            val resultDate = GregorianCalendar(year, month, dayOfMonth).time
            targetFragment?.let {
                (it as DAtePickerCallBack).onDateSelected(resultDate)
            }

        }

        return DatePickerDialog(
            requireContext(),
            dateListener,
            year,
            month,
            day
        )

    }

}