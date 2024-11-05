package com.embehome.embehome.Fragments

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.embehome.embehome.R
import com.embehome.embehome.databinding.FanControllerFragmentBinding
import com.embehome.embehome.viewModel.FanControllerViewModel


class FanController(var curLevel: Int, val action: (level: Int) -> Unit) : Fragment() {

    val viewModel: FanControllerViewModel by viewModels()
    private val angleDiff = 4f


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FanControllerFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fan_controller_fragment,
            container,
            false
        )
        val fan = BitmapFactory.decodeResource(resources, R.drawable.tt_fan_regulator_knob)

        binding.imageView94.setImageBitmap(fan)

        viewModel.level.observe(viewLifecycleOwner, Observer {
            if (it) {
                curLevel = viewModel.action
                action(curLevel)
//                binding.imageView94.setImageResource(getFanImage(it))
            }
        })

        var angle = getAngle(curLevel)
        getAnimation(angle, angle)?.let {
            binding.imageView94.startAnimation(it)
        }


        var x = 0f
        var y = 0f
        var touch = false

        binding.imageView94.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    touch = true
                    if (touch) {
                        x = event.x
                        y = event.y
                        touch = false
                    }
                }

                MotionEvent.ACTION_MOVE -> {
                    if (v.width >= event.x && v.height >= event.y && event.x > 0 && event.y > 0 && (x != event.x || y != event.y)) {
                        val xDiff = (event.x - x)
                        val yDiff = (event.y - y)
                        val nxt: Float = if (v.width / 2 >= event.x && v.height / 2 >= event.y) {
                            if (xDiff < 0 && yDiff > 0) {
                                angle - angleDiff
                            } else if (xDiff > 0 && yDiff < 0) {
                                angle + angleDiff
                            } else angle
                        } else if (v.width / 2 >= event.x && v.height / 2 < event.y) {
                            if (xDiff > 0 && yDiff > 0) {
                                angle - angleDiff

                            } else if (xDiff < 0 && yDiff < 0) {
                                angle + angleDiff
                            } else angle
                        } else if (v.width / 2 < event.x && v.height / 2 >= event.y) {
                            if (xDiff < 0 && yDiff < 0) {
                                angle - angleDiff
                            } else if (xDiff > 0 && yDiff > 0) {
                                angle + angleDiff
                            } else angle
                        } else if (v.width / 2 < event.x && v.height / 2 < event.y) {
                            if (xDiff > 0 && yDiff < 0) {
                                angle - angleDiff
                            } else if (xDiff < 0 && yDiff > 0) {
                                angle + angleDiff
                            } else angle

                        } else angle
                        if (angle != nxt) {
                            getAnimation(angle, nxt)?.let {
                                v.startAnimation(it)
                            }
                            angle = nxt
                        }
                        x = event.x
                        y = event.y
                    }

                }

                MotionEvent.ACTION_UP -> {
                    angle %= 360
                    Log.d("TronX_Fan", "################################ $angle % 360")
                    if (angle < 0) angle += 360
                    Log.d("TronX_Fan", "################################ $angle")
                    val nxt = if (angle > 270 || (angle <= 23 && angle > 0)) {
                        0f
                    } else if (angle in 1f..70f) {
                        45f
                    } else if (angle in 46f..115f) {
                        90f
                    } else if (angle in 91f..160f) {
                        135f
                    } else {
                        180f
                    }
                    getAnimation(angle, nxt)?.let {
                        v.startAnimation(it)
                        angle = nxt
                    }
                    action(getLevel(angle))
                }
            }
            true
        }

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    private fun getAngle(level: Int): Float {
        return when (level) {
            1 -> {
                45f
            }
            2 -> {
                90f
            }
            3 -> {
                135f
            }
            4 -> {
                180f
            }
            else -> 0f
        }
    }

    private fun getLevel(angle: Float): Int {
        return when (angle) {
            45f -> {
                1
            }
            90f -> {
                2
            }
            135f -> {
                3
            }
            180f -> {
                4
            }
            else -> {
                0
            }
        }
    }


    private fun getAnimation(cur: Float, nxt: Float): RotateAnimation? {
        /*if (cur == nxt)
            return null*/
        val rAnimAntiClockWise = RotateAnimation(
            cur, nxt,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        rAnimAntiClockWise.fillAfter = true
        rAnimAntiClockWise.interpolator = LinearInterpolator()
        rAnimAntiClockWise.duration = 0
        return rAnimAntiClockWise
    }
}






