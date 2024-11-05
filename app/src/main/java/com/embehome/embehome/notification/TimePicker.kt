package com.embehome.embehome.notification

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class TimePicker (val time : (hour: Int, min : Int) -> Unit)  : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendarInstance = Calendar.getInstance()
        val hour = calendarInstance.get((Calendar.HOUR_OF_DAY))
        val min = calendarInstance.get(Calendar.MINUTE)
        return TimePickerDialog(activity, this, hour, min, DateFormat.is24HourFormat(activity))
    }

    override fun onTimeSet(view: TimePicker?, hour: Int, min: Int) {
        Log.d("TronX","$hour : $min")
        time(hour, min)
    }

}
