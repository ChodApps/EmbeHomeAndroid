package com.embehome.embehome.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.embehome.embehome.R
import kotlinx.android.synthetic.main.fragment_dialer.view.*


/** com.tronx.tt_things_app.Fragments
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 09-07-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class DialerFragment  (var curLevel: Int, val action : (level : Int) -> Unit) : Fragment() {

    val l = HashMap<Int, Int> ().apply {
        this[270] = 0
        this[288] = 1
        this[306] = 2
        this[324] = 3
        this[342] = 4
        this[0] = 5
        this[18] = 6
        this[36] = 7
        this[54] = 8
        this[72] = 9
        this[90] = 10
    }

    val angle = HashMap<Int, Int> ().apply {
        this[0] = 270
        this[1] = 288
        this[2] = 306
        this[3] = 324
        this[4] = 342
        this[5] = 0
        this[6] = 18
        this[7] = 36
        this[8] = 54
        this[9] = 72
        this[10] = 90
    }




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_dialer, container, false)
        v.textView232.text = "${curLevel*10}%"
        val p = v.imageView236.layoutParams as ConstraintLayout.LayoutParams
        p.circleAngle = angle[curLevel]?.toFloat() ?: 10f

        var level = -1
        v.imageView234.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                val p = v.imageView236.layoutParams as ConstraintLayout.LayoutParams
                getDimmerAngle(p0?.width ?: 0, p0?.height ?: 0, p1?.x ?: 0.0f, p1?.y ?: 0f)?.let {
                    Log.d("TronX",it.toString())
                    p.circleAngle = it.toFloat()
                    level = l[it] ?: 0
                }
                v.imageView236.layoutParams = p
                if (level != -1)
                v.textView232.text = "${level * 10}%"
                p1?.action?.let {
                    when (it) {
                        MotionEvent.ACTION_UP -> {
                            if (level != -1) action(level ?: 0)
                        }
                        else -> {

                        }
                    }
                }

                return true
            }

        })

        return v
    }

    fun getDimmerAngle (w : Int, h :Int, x : Float, y : Float) : Int? {
        val p : Float = (70 / 300f * w).toFloat()
        val aa = w/2f - p
        val bb = w/2f + p
        val cc = (bb - aa) / 11f
        if (x in aa..bb && y in aa..w/2f) {
            val ff = ((x - aa) / cc).toInt()
            val angle = ((180 / 10 * ff) + 270) % 360
            return angle
        }
//        val f = x / w * 130
////        if (p > 85 && p < 215) {
////           p = p - 85
////            p = 270
////        }
//        val a = 270 + (f / p * 180) % 360
        return  null// if (a > 90 && a < 270) 90f else if( a > 180 && a < 270) 270f else a
    }
}