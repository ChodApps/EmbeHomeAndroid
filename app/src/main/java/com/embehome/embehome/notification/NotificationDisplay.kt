package com.embehome.embehome.notification

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.embehome.embehome.R
import com.embehome.embehome.Utils.AppDialogs
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.databinding.ActivityNotificationDisplayBinding
import com.embehome.embehome.deRegisterNotification
import com.embehome.embehome.registerNotification
import kotlinx.android.synthetic.main._notification_pdf_date_selector.*
import java.text.DateFormatSymbols
import java.util.*

class NotificationDisplay : AppCompatActivity() {

    val viewModel : NotificationDisplayViewModel by viewModels()
    lateinit var dialog : Dialog

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding : ActivityNotificationDisplayBinding = DataBindingUtil.setContentView(this, R.layout.activity_notification_display)
        val notificationAdapter = NotificationListAdapter(this, viewModel.notificationData)
        binding.rcvNotificationList.adapter  = notificationAdapter

        binding.rcvNotificationList.apply {
            try {
                (layoutManager as LinearLayoutManager).let {layout ->
                    addOnScrollListener(object : RecyclerView.OnScrollListener() {
                        override fun onScrollStateChanged(
                            recyclerView: RecyclerView,
                            newState: Int
                        ) {

                        }

                        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                            val lp = layout.findLastCompletelyVisibleItemPosition()
                            viewModel.notificationData.value?.let {
                                if ((it.size - 1) == lp) {
                                    viewModel.loadMoreNotification(viewModel.limit, it.size)
                                    binding.progressBar5.visibility = View.VISIBLE
                                }
                            }
                        }
                    })
                }
            }
            catch (e : Exception) {
                Log.e("TronX_Notification","Notification scroll ${e.message}")
            }
        }


            var y = 0
            var m = 0
            var day = 0

            viewModel.notificationData.observe(this, Observer {
                if (it.size != 0) {
                    // notificationAdapter = NotificationListAdapter(this, viewModel.notificationData.value ?: ArrayList())
                    binding.progressBar5.visibility = View.GONE
                    notificationAdapter.notifyDataSetChanged()
                }
            })

            viewModel.performAction.observe(this, Observer {
                if (it) {
                    when (viewModel.action) {
                        "Back" -> {
                            finish()
                        }

                        "DismissDialog" -> {
                            if (::dialog.isInitialized) dialog.dismiss()
                        }

                        "PDF" -> {
                            dialog = Dialog(this , android.R.style.Theme_Translucent_NoTitleBar)
                            dialog.setContentView(R.layout._notification_pdf_date_selector)
                            dialog.show()
                            dialog.input_text_16.setOnClickListener {
                                val d = DatePickerDialog(this)
                                d.datePicker.maxDate = Date().time
                                d.setOnDateSetListener { view, year, month, dayOfMonth ->
                                    AppServices.log("TronX _ Date", "$year $month $dayOfMonth")
                                    y = year
                                    m = m
                                    val t = DateFormatSymbols().shortMonths[month]
                                    day = dayOfMonth
                                    dialog.input_text_16.setText("${getTwoDigit(dayOfMonth)}-$t-$year")
                                    dialog.input_text_17.setText("")
                                }
                                d.show()


                            }
                            dialog.input_text_17.setOnClickListener {
                                if (dialog.input_text_16.text.isNullOrEmpty()) {
                                    AppServices.toastShort(this, "Please select start date")
                                } else {
                                    val d = DatePickerDialog(this)
                                    d.datePicker.maxDate = Date().time
                                    val i = Calendar.getInstance()
                                    i.set(Calendar.YEAR, y)
                                    i.set(Calendar.MONTH, m)
                                    i.set(Calendar.DAY_OF_MONTH, day)
                                    d.datePicker.minDate = i.timeInMillis
                                    d.show()
                                    d.setOnDateSetListener { view, year, month, dayOfMonth ->
                                        AppServices.log("TronX _ Date", "$year $month $dayOfMonth")
                                        val t = DateFormatSymbols().shortMonths[month]
                                        dialog.input_text_17.setText(
                                            "${getTwoDigit(dayOfMonth)}-$t-$year"
                                        )
                                    }
                                }
                            }

                            dialog.button51.setOnClickListener {
                                if (dialog.input_text_16.text.isNullOrEmpty()) {
                                    AppServices.toastShort(this, "Please select start date")
                                }
                                else {
                                    if (dialog.input_text_17.text.isNullOrEmpty()) {
                                        AppServices.toastShort(this, "Please select end date")
                                    }
                                    else {
                                        AppDialogs.startLoadScreen(this)
                                        viewModel.sendPdf(this, dialog.input_text_16.text.toString(), dialog.input_text_17.text.toString())
                                    }
                                }
                            }

                            dialog.dialog_content.setOnClickListener {

                            }

                            dialog.parentLayoutNotification.setOnClickListener {
                                dialog.dismiss()
                            }
                        }
                    }
                }
            })

            binding.viewModel = viewModel
            binding.lifecycleOwner = this
        }

    private fun getTwoDigit (number : Int) : String {
        return if (number < 10)
            "0$number"
        else number.toString()
    }

    override fun onStart() {
        super.onStart()
        registerNotification(this, this)
    }

    override fun onStop() {
        super.onStop()
        deRegisterNotification(this)
    }

}

