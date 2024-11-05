package com.embehome.embehome.Activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.embehome.embehome.Adapter.SelectAreaAdapter
import com.embehome.embehome.BaseActivity
import com.embehome.embehome.R
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.Utils.Validators
import com.embehome.embehome.databinding.ActivityAddSSBBinding
import com.embehome.embehome.deRegisterNotification
import com.embehome.embehome.instructions.DeviceInstruction
import com.embehome.embehome.registerNotification
import com.embehome.embehome.viewModel.AddBoardActivityViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalStdlibApi::class)
class AddSSB : BaseActivity() {

    @ExperimentalStdlibApi
    lateinit var callBack: ConnectivityManager.NetworkCallback

    val viewModel: AddBoardActivityViewModel by viewModels()
    lateinit var dialog: Dialog
    lateinit var areaAdapter : SelectAreaAdapter

    @OptIn(ExperimentalStdlibApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityAddSSBBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_add_s_s_b)



        viewModel.macID.value = CacheHubData.selectedMacID

        areaAdapter = SelectAreaAdapter(this, viewModel.area)
        binding.recyclerViewSelectAreaAddDevice.adapter = areaAdapter

//        if (FakeDB.areaData.size > 0) viewModel.area.value = FakeDB.areaData[0]

        viewModel.addBoardBack.observe(this, Observer {
            if (it) {
                finish()
            }
        })

        viewModel.area.observe(this, Observer {
            if (it != null) viewModel.updateView(viewModel.sAdd)
            if (::dialog.isInitialized) if (dialog.isShowing) dialog.dismiss()
            viewModel.areaName.value = it.area_name
            if (it.image == null) {
                GlobalScope.launch(Dispatchers.Main) {
                    val res = AppServices.imageSave(this@AddSSB, it.area_image)
                    if (res != null && it.image == null) {
                        it.image = res
                        binding.addBoardAreaImage.setImageBitmap(it.image)
                    }
                }
            }
            it.image?.let {
                binding.addBoardAreaImage.setImageBitmap(it)
            }
//            Toast.makeText(this, it.area_name, Toast.LENGTH_SHORT).show()
        })

        viewModel.selectBoard.observe(this, Observer {
            if (it) {
                /*dialog = Dialog(this, android.R.style.Theme_Translucent_NoTitleBar)

                dialog.setContentView(R.layout._select_area)
                dialog.show()
                dialog.list_area_select.visibility = View.GONE
                dialog.grid_area_select.visibility = View.VISIBLE
                dialog.grid_area_select.adapter = SelectAreaAdapter(this, viewModel.area)
                dialog.setCanceledOnTouchOutside(true)
                dialog.goto_create_area.visibility = View.GONE
                dialog.select_item_or_text.visibility = View.GONE
                dialog.select_area_parent.setOnClickListener{

                }
                dialog.select_area_root.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.goto_create_area.setOnClickListener {
                    dialog.dismiss()
                  //  findNavController().navigate(AddBoardDirections.actionAddBoardToCreateArea())
                }*/
                viewModel.updateView(viewModel.sImage)
            }
        })

        viewModel.loading.observe(this, Observer {
            if (it) {
                AppServices.startLoadScreen(this)
            } else {
                AppServices.stopLoadScreen()
            }
        })

        viewModel.createArea.observe(this, Observer {
            if (it) {
                // findNavController().navigate(AddBoardDirections.actionAddBoardToCreateArea())
            }
        })

        viewModel.showToast.observe(this, Observer {
            if (it) {
                Toast.makeText(this, viewModel.toastMessage.value, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.performAction.observe(this, Observer {
            if (it) {
                when (viewModel.action) {
                    "AddDevice" -> {
                        viewModel.boardName.value?.let {
                            if (it.isNotEmpty())
                                viewModel.tempAddDevice(
                                    this,
                                    viewModel.area.value?.area_id ?: 0,
                                    it
                                )
                            else
//                                viewModel.tempAddDevice(
//                                    this,
//                                    viewModel.area.value?.area_id ?: 0,
//                                    "new Board"
//                                )
                                AppServices.toastShort(this, "Please enter valid device name.")
                        }
                    }

                    "createArea" -> {
                        val intent = Intent(this, FragmentHandler::class.java)
                        intent.putExtra("fragment", "createArea")
                        startActivity(intent)
                    }

                    "editArea" -> {
                        val intent = Intent(this, FragmentHandler::class.java)
                        intent.putExtra("fragment", "editArea")
                        intent.putExtra("id", 1)
                        startActivity(intent)
                    }

                    "Config" -> {
                        if (viewModel.board != null) {
                            viewModel.board?.let {
                                viewModel.boardName.value = ""
//                                supportFragmentManager.beginTransaction().replace(R.id.config, DeviceConfig(it)).addToBackStack("setting").commit()
                                val i = getConfigIntent(
                                    this,
                                    CacheHubData.selectedMacID,
                                    it.thing_serial_number
                                )
                                startActivity(i)
                            }
                        } else {
                            viewModel.addBoardBack()
                        }
                    }
                }
                viewModel.action = ""
            }
        })

        binding.textView221.setOnClickListener {
            val d = DeviceInstruction()
            d.show(supportFragmentManager, "Device")
        }

        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    @ExperimentalStdlibApi
    override fun onStart() {
        super.onStart()
        if (::areaAdapter.isInitialized) areaAdapter.notifyDataSetChanged()
        registerNotification(this, this)
        callBack = Validators.registerToWifi(this, lo = {
//            AppServices.toastShort(this, "Internet Connectivity lost.")
            finish()
        })
    }

    @ExperimentalStdlibApi
    override fun onStop() {
        super.onStop()
        deRegisterNotification(this)
        if (::callBack.isInitialized)Validators.unregisterToWifi(this, callBack)
    }

    private fun getConfigIntent(context: Context, macID: String, thingsID: String): Intent {
        val intent = Intent(context, FragmentHandler::class.java)
        intent.putExtra("fragment", "ConfigCur")
        intent.putExtra("macID", macID)
        intent.putExtra("thingsID", thingsID)
        return intent
    }

    @ExperimentalStdlibApi
    override fun onDestroy() {
        super.onDestroy()

    }
}
