package com.embehome.embehome.powerNumber

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.embehome.embehome.Activity.PowerTimeDiff
import com.embehome.embehome.Model.PowerUnitModel
import com.embehome.embehome.R
import com.embehome.embehome.Utils.AppServices
import kotlinx.android.synthetic.main.fragment_pie.view.*


class Pie (val  data : MutableLiveData<PowerUnitModel>, val time : MutableLiveData<PowerTimeDiff>) : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_pie, container, false)

        val bar = v.pie
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
                            val d = pUnit.data.daily_units.map { it.units.toFloat() }
                            setBarData(d, bar)
                        }
                        PowerTimeDiff.WEEK -> {
                            val d = pUnit.data.weekly_units.map { it.units.toFloat() }
                            setBarData(d, bar)
                        }
                        PowerTimeDiff.MONTH -> {
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
                            val d : List<Float> = pUnit.data.daily_units.map { it.units.toFloat() }
                            setBarData(d, bar)
                        }
                        PowerTimeDiff.WEEK -> {
                            val d = pUnit.data.weekly_units.map { it.units.toFloat() }
                            setBarData(d, bar)
                        }
                        PowerTimeDiff.MONTH -> {
                            val d = pUnit.data.monthly_units.map { it.units.toFloat() }
                            setBarData(d, bar)
                        }
                    }
                }
            }
        })


        /*



        val ds = LineDataSet(List(5){
            val t = it.toFloat()
            Entry(t, t, t)
        }, "Hello")



        val pieChart = v.pie

        val NoOfEmp = ArrayList<PieEntry>()

        NoOfEmp.add(PieEntry(945f, "0"))
        NoOfEmp.add(PieEntry(1040f, "1"))
        NoOfEmp.add(PieEntry(1133f, "2"))
        NoOfEmp.add(PieEntry(1240f, "3"))
        NoOfEmp.add(PieEntry(1369f, "4"))
        NoOfEmp.add(PieEntry(1487f, "5"))
        NoOfEmp.add(PieEntry(1501f, "6"))
        NoOfEmp.add(PieEntry(1645f, "7"))
        NoOfEmp.add(PieEntry(1578f, "8"))
        NoOfEmp.add(PieEntry(1695f, "9"))
        val dataSet = PieDataSet(NoOfEmp, "Number Of Employees")

        GlobalScope.launch (Main) {
            delay(5000)
//            NoOfEmp.removeAt(1)
//            NoOfEmp.removeAt(1)
//            NoOfEmp.removeAt(1)
            val pieDataSet = PieDataSet(NoOfEmp, "Number Of Employees")
//            pieChart.data = PieData(pieDataSet)
            pieDataSet.setColors(*ColorTemplate.COLORFUL_COLORS)
            pieChart.animateXY(500, 500)
//            pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
            pieDataSet.setSliceSpace(2f);
            pieDataSet.setValueTextColor(Color.WHITE);
            pieDataSet.setValueTextSize(10f);
            pieDataSet.setSliceSpace(5f);
            dataSet.removeEntry(1)
            dataSet.removeEntry(1)
        }

        val year = ArrayList<Any>()

        year.add("2008")
        year.add("2009")
        year.add("2010")
        year.add("2011")
        year.add("2012")
        year.add("2013")
        year.add("2014")
        year.add("2015")
        year.add("2016")
        year.add("2017")
        val data = PieData(dataSet)
        pieChart.data = data

        dataSet.setColors(*ColorTemplate.COLORFUL_COLORS)
        pieChart.animateXY(500, 500)
        dataSet.setSliceSpace(2f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(10f);
        dataSet.setSliceSpace(5f);

*/
        return v
    }

    private fun setBarData (data : List<Float>, graph : PieChart) {
        if (data.size != 0) {
            val units = ArrayList<PieEntry>()
            data.forEachIndexed { index, ttPowerUnit ->
                units.add(
                    PieEntry(
                        ttPowerUnit,
                        "units"
                    )
                )
            }
            val day = PieDataSet(units, "")
            day.setDrawIcons(false)
            day.sliceSpace = 2f
            graph.animateX(1000)
            day.setColors(*ColorTemplate.COLORFUL_COLORS)
            day.setValueTextColor(Color.WHITE);
            day.setValueTextSize(10f);
            day.setSliceSpace(5f);
            graph.data = PieData(day)
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