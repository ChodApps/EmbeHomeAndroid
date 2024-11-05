package com.embehome.embehome.rules.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RuleAddTimeViewModel : ViewModel() {

    val time = MutableLiveData<Int>(0)
    val timeRepeat = MutableLiveData<Boolean>()
    val weekDays = ArrayList<Int>()
    val sun = MutableLiveData (false)
    val mon = MutableLiveData (false)
    val tue = MutableLiveData (false)
    val wed = MutableLiveData (false)
    val thu = MutableLiveData (false)
    val fri = MutableLiveData (false)
    val sat = MutableLiveData (false)

    fun setWeekDay () {
        weekDays.forEach {day ->
            when (day) {
                0 -> sun.value = true
                1 -> mon.value = true
                2 -> tue.value = true
                3 -> wed.value = true
                4 -> thu.value = true
                5 -> fri.value = true
                6 -> sat.value = true
            }
        }
    }

    fun selectWeekDay (day : Int) {
        if (weekDays.contains(day)) {
            removeWeekDay(day)
        }
        else {
            addWeekDay(day)
        }
    }

    fun addWeekDay (day : Int) {
        weekDays.filter { it == day }.also {
            weekDays.removeAll(it)
        }
        weekDays.add(day).also {
            if (it) {
                when (day) {
                    0 -> sun.value = true
                    1 -> mon.value = true
                    2 -> tue.value = true
                    3 -> wed.value = true
                    4 -> thu.value = true
                    5 -> fri.value = true
                    6 -> sat.value = true
                }
            }
        }
    }

    private fun removeWeekDay (day: Int) {
        weekDays.remove(day).also {
            if (it) {
                when (day) {
                    0 -> sun.value = false
                    1 -> mon.value = false
                    2 -> tue.value = false
                    3 -> wed.value = false
                    4 -> thu.value = false
                    5 -> fri.value = false
                    6 -> sat.value = false
                }
            }
        }
    }
}