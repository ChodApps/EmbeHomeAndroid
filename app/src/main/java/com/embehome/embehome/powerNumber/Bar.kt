package com.embehome.embehome.powerNumber

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.embehome.embehome.Activity.PowerTimeDiff
import com.embehome.embehome.Model.PowerUnitModel
import com.embehome.embehome.R
import com.embehome.embehome.Utils.AppServices
import kotlinx.android.synthetic.main.fragment_bar.view.*


class Bar (val  data : MutableLiveData<PowerUnitModel>, val time : MutableLiveData<PowerTimeDiff>) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_bar, container, false)
        val units = ArrayList<BarEntry>()
        val bar  = v.bar
        bar.description = Description().apply {
            text = ""
        }
        bar.setNoDataText("Data not available")
        bar.setNoDataTextColor(requireContext().getColor(R.color.colorAccent))



        data.observe(viewLifecycleOwner, Observer { powerUnit ->
            AppServices.log("Data Received")
            powerUnit?.let {pUnit ->
                time.value?.let {
                    when (it) {
                        PowerTimeDiff.DAY -> {
                            /*if (pUnit.data.daily_units.size != 0) {
                                units.clear()
                                pUnit.data.daily_units.forEachIndexed { index, ttPowerUnit ->
                                    units.add(
                                        BarEntry(
                                            index.toFloat(),
                                            ttPowerUnit.units.toFloat()
                                        )
                                    )
                                }
                                val day = BarDataSet(units, "Unit")
                                day.color = requireContext().getColor(R.color.colorAccent)
                                bar.data = BarData(day)

                                bar.notifyDataSetChanged()
                                bar.invalidate()
                            }
                            else {
                                bar.data = null
                                bar.notifyDataSetChanged()
                                bar.invalidate()
                            }*/
                            val d = pUnit.data.daily_units.map { it.units.toFloat() }
                            setBarData(d, bar)
                        }
                        PowerTimeDiff.WEEK -> {
                            /*units.clear()
                            pUnit.data.weekly_units.forEachIndexed { index, ttPowerUnit ->
                                units.add(BarEntry(index.toFloat(), ttPowerUnit.units.toFloat()))
                            }
                            val week = BarDataSet(units, "Unit")
                            week.color = requireContext().getColor(R.color.colorAccent)
                            bar.data = BarData(week)
                            bar.notifyDataSetChanged()
                            bar.invalidate()*/
                            val d = pUnit.data.weekly_units.map { it.units.toFloat() }
                            setBarData(d, bar)
                        }
                        PowerTimeDiff.MONTH -> {
                           /* units.clear()
                            pUnit.data.monthly_units.forEachIndexed { index, ttPowerUnit ->
                                units.add(BarEntry(index.toFloat(), ttPowerUnit.units.toFloat()))
                            }
                            val day = BarDataSet(units, "Unit")
                            day.color = requireContext().getColor(R.color.colorAccent)
                            bar.data = BarData(day)
                            bar.notifyDataSetChanged()
                            bar.invalidate()*/
                            val d = pUnit.data.monthly_units.map { it.units.toFloat() }
                            setBarData(d, bar)
                        }
                    }
                }
            }

        })

        time.observe(viewLifecycleOwner, Observer {
            AppServices.log("Day Change")
            data.value?.let { pUnit ->
                time.value?.let {
                    when (it) {
                        PowerTimeDiff.DAY -> {
                            /*if (pUnit.data.daily_units.size != 0) {
                                units.clear()
                                pUnit.data.daily_units.forEachIndexed { index, ttPowerUnit ->
                                    units.add(
                                        BarEntry(
                                            index.toFloat(),
                                            ttPowerUnit.units.toFloat()
                                        )
                                    )
                                }
                                val day = BarDataSet(units, "Unit")
                                day.color = requireContext().getColor(R.color.colorAccent)
                                bar.data = BarData(day)

                                bar.notifyDataSetChanged()
                                bar.invalidate()
                            } else {
                                bar.data = null
                                bar.notifyDataSetChanged()
                                bar.invalidate()
                            }*/
                            val d : List<Float> = pUnit.data.daily_units.map { it.units.toFloat() }
                            setBarData(d, bar)
                        }
                        PowerTimeDiff.WEEK -> {
                            val d = pUnit.data.weekly_units.map { it.units.toFloat() }
                            setBarData(d, bar)
                            /*units.clear()
                            val week = BarDataSet(units, "Unit")
                            week.color = requireContext().getColor(R.color.colorAccent)
                            bar.data = BarData(week)
                            bar.notifyDataSetChanged()
                            bar.invalidate()*/
                        }
                        PowerTimeDiff.MONTH -> {
                            /*units.clear()
                            pUnit.data.monthly_units.forEachIndexed { index, ttPowerUnit ->
                                units.add(BarEntry(index.toFloat(), ttPowerUnit.units.toFloat()))
                            }
                            val day = BarDataSet(units, "Unit")
                            day.color = requireContext().getColor(R.color.colorAccent)
                            bar.data = BarData(day)
                            bar.notifyDataSetChanged()
                            bar.invalidate()*/
                            val d = pUnit.data.monthly_units.map { it.units.toFloat() }
                            setBarData(d, bar)
                        }
                    }
                }
            }
        })

        /*val NoOfEmp = ArrayList<BarEntry>()

        NoOfEmp.add(BarEntry(945f, 10f))
        var dataSet = BarDataSet(NoOfEmp, "Number Of Employees")



        GlobalScope.launch(Main) {
            delay(5000)
            NoOfEmp.add(BarEntry(1040f, 1f))
            NoOfEmp.add(BarEntry(1133f, 2f))
            NoOfEmp.add(BarEntry(1240f, 3f))
            NoOfEmp.add(BarEntry(1369f, 4f))
            val dataSet1 = BarDataSet(NoOfEmp, "Unit")
            v.bar.data = BarData(dataSet1)
            v.bar.notifyDataSetChanged()
            v.bar.invalidate()
        }

        v.bar.data = BarData(dataSet)*/
        return v
    }


    fun setBarData (data : List<Float>, graph : BarChart) {
        if (data.size != 0) {
            val units = ArrayList<BarEntry>()
            data.forEachIndexed { index, ttPowerUnit ->
                units.add(
                    BarEntry(
                        index.toFloat(),
                        ttPowerUnit
                    )
                )
            }
            val day = BarDataSet(units, "Unit")
            day.color = requireContext().getColor(R.color.colorAccent)
            graph.data = BarData(day)

            graph.notifyDataSetChanged()
            graph.invalidate()
        } else {
            graph.data = null
            graph.setNoDataText("Data not available")
            graph.setNoDataTextColor(requireContext().getColor(R.color.colorAccent))
            graph.notifyDataSetChanged()
            graph.invalidate()
        }
    }

}