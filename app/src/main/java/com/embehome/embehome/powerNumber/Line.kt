package com.embehome.embehome.powerNumber

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.embehome.embehome.Activity.PowerTimeDiff
import com.embehome.embehome.Model.PowerUnitModel
import com.embehome.embehome.R
import com.embehome.embehome.Utils.AppServices
import kotlinx.android.synthetic.main.fragment_line.view.*

class Line (val  data : MutableLiveData<PowerUnitModel>, val time : MutableLiveData<PowerTimeDiff>) : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_line, container, false)

        val bar = v.line
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

        /*val ds = LineDataSet(List(5){
            val t = it.toFloat()
            Entry(t, t, t)
        }, "Hello")

        v.line.data = LineData(ds)*/

        return v
    }

    fun setBarData (data : List<Float>, graph : LineChart) {
        if (data.size != 0) {
            val units = ArrayList<Entry>()
            data.forEachIndexed { index, ttPowerUnit ->
                units.add(
                    Entry(
                        index.toFloat(),
                        ttPowerUnit
                    )
                )
            }
            val day = LineDataSet(units, "Unit")
            day.setDrawIcons(false)

            day.enableDashedLine(5f, 5.5f, 0f)
            day.enableDashedHighlightLine(5f, 5.5f, 0f)

            day.setColor(Color.BLACK)
            day.setCircleColor(Color.BLACK)
            day.lineWidth = 1f
            day.circleRadius = 3f
            day.setDrawCircleHole(false)
            day.valueTextSize = 10f
            day.formLineWidth = 1f
            day.formSize = 15f

            day.fillAlpha = 1
            day.setDrawFilled(true)
            day.color = Color.BLACK


            graph.data = LineData(day)
            graph.axisLeft.axisMinimum = 0f
            graph.axisRight.axisMinimum = 0f
            graph.xAxis.axisMinimum = 0f
            graph.xAxis.granularity = 1f
            graph.animateXY(1000,1000)
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