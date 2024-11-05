package com.embehome.embehome.Utils

import android.R
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.util.Log
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main._load_screen.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext


/** com.tronx.tt_things_app.Utils
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 18-06-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


object AppDialogs {

    private lateinit var titleDialog : androidx.appcompat.app.AlertDialog

    fun openMessageDialog (context: Context, msg: String) {
        closeTitleDialog()
        titleDialog = MaterialAlertDialogBuilder(context)
            .setMessage(msg)
            .setPositiveButton("Close") {dialog, which ->
                dialog.dismiss()
            }
            .show()
    }
    fun openMessageDialogOk (context: Context, msg: String, dismissFunc: (d: DialogInterface) -> Unit) {
        closeTitleDialog()
        titleDialog = MaterialAlertDialogBuilder(context)
            .setMessage(msg)
            .setPositiveButton("Ok") {dialog, which ->
                dialog.dismiss()
            }
            .setOnDismissListener {
                dismissFunc(it)
            }
            .show()
    }
    fun openMessageDialog (context: Context, msg: String, dismissFunc: (d: DialogInterface) -> Unit) {
        closeTitleDialog()
        titleDialog = MaterialAlertDialogBuilder(context)
            .setMessage(msg)
            .setPositiveButton("Close") {dialog, which ->
                dialog.dismiss()
            }
            .setOnDismissListener {
                dismissFunc(it)
            }
            .show()
    }

    private fun openTitleDialog (
        context : Context,
        title : String,
        msg  : String,
        negButton : String,
        negFunc: () -> Unit,
        posButton : String,
        posFunc: (dialog : DialogInterface, which : Int) -> Unit,
        dismissFunc: (d : DialogInterface) -> Unit = {}
    ){
        closeTitleDialog()
        titleDialog = MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(msg)
            .setNegativeButton(negButton) { dialog, which ->
                negFunc()
            }
            .setPositiveButton(posButton) { dialog, which ->
                posFunc(dialog, which)
            }
            .setOnDismissListener {
                dismissFunc(it)
            }
            .show()
    }
    /*fun openTitleDialog (context : Context, title : String, msg  : String, negButton : String, negFunc: () -> Unit,  posButton : String, posFunc: (dialog : DialogInterface, which : Int) -> Unit) {
        openTitleDialog(context, title, msg, negButton, negFunc, posButton, posFunc)
    }*/

    fun openTitleDialog (context : Context, title : String, msg  : String, negButton : String,  posButton : String, posFunc: (dialog : DialogInterface, which : Int) -> Unit) {
        openTitleDialog(context, title, msg, negButton, {

        }, posButton, posFunc)
    }

    fun openTitleDialog (
        context: Context,
        title: String,
        msg: String,
        neutralBtn :String,
        neutralBtnFunc: (dialog: DialogInterface, which: Int) -> Unit,
        negButton: String,
        negFunc: (dialog: DialogInterface, which: Int) -> Unit,
        posButton: String,
        posFunc: (dialog: DialogInterface, which: Int) -> Unit,
        dismissFunc: (d : DialogInterface) -> Unit = {}
    ) {
        closeTitleDialog()
        titleDialog = MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(msg)
            .setNeutralButton(neutralBtn) {dialogInterface, i ->
                neutralBtnFunc(dialogInterface, i)
            }
            .setNegativeButton(negButton) { dialog, which ->
                negFunc(dialog, which)
            }
            .setPositiveButton(posButton) { dialog, which ->
                posFunc(dialog, which)
            }
            .setOnDismissListener {
                dismissFunc(it)
            }
            .show()
    }

    fun openTitleDialog (context : Context, title : String, msg  : String, negButton : String,  posButton : String, posFunc: (dialog : DialogInterface, which : Int) -> Unit, dismissFunc  : (d : DialogInterface) -> Unit) {
        openTitleDialog(context, title, msg, negButton, {

        }, posButton, posFunc, dismissFunc)
    }

    suspend fun openTitleDialogUsingCoroutine (context : Context, title : String, msg  : String, negButton : String,  posButton : String, posFunc: (dialog : DialogInterface, which : Int) -> Unit) = withContext(Main){
        openTitleDialog(context, title, msg, negButton, {

        }, posButton, posFunc)
    }

    fun openDialog (context : Context,  msg  : String, negButton : String, negFunc : ()-> Unit, posButton : String, posFunc : ()-> Unit) {
        closeTitleDialog()
        titleDialog = MaterialAlertDialogBuilder(context)
            .setMessage(msg)
            .setNegativeButton(negButton) { dialog, which ->
                negFunc()
            }
            .setPositiveButton(posButton) { dialog, which ->
                posFunc()
            }
            .show()
    }

    fun closeTitleDialog () {
        if (::titleDialog.isInitialized && titleDialog.isShowing) titleDialog.dismiss()
    }

    private lateinit var loadingDialog : Dialog

    fun startLoadScreen (context: Context) {
        if (::loadingDialog.isInitialized) loadingDialog.dismiss()
        loadingDialog = Dialog(context, R.style.Theme_Translucent_NoTitleBar)

        loadingDialog.setContentView(com.embehome.embehome.R.layout._load_screen)
        loadingDialog.loading_screen_cancel.setOnClickListener{
            Log.d("Testing", "Dialog Cancel")
            loadingDialog.dismiss()
        }
        loadingDialog.loading_layout.setBackgroundColor(Color.WHITE)
        loadingDialog.setCancelable(false)
        loadingDialog.setCanceledOnTouchOutside(false)
        loadingDialog.show()
    }

    fun startMsgLoadScreen (context: Context, msg : String) {
        if (::loadingDialog.isInitialized) loadingDialog.dismiss()
        loadingDialog = Dialog(context, R.style.Theme_Translucent_NoTitleBar)

        loadingDialog.setContentView(com.embehome.embehome.R.layout._load_screen)
        loadingDialog.textView12.text = msg
        loadingDialog.loading_screen_cancel.setOnClickListener{
            Log.d("Testing", "Dialog Cancel")
            loadingDialog.dismiss()
        }
        loadingDialog.loading_layout.setBackgroundColor(Color.WHITE)
        loadingDialog.setCancelable(false)
        loadingDialog.setCanceledOnTouchOutside(false)
        loadingDialog.show()
    }

    fun stopLoadScreen() {
        if (::loadingDialog.isInitialized){
            loadingDialog.isShowing.also {
                if (it) loadingDialog.dismiss()
            }
        }
    }

    private lateinit var networkDialog : Dialog

    fun openInternetAvailableDialog (context: Context) {
        try {
            if (::networkDialog.isInitialized) networkDialog.dismiss()
            networkDialog = Dialog(context, R.style.Theme_Translucent_NoTitleBar)

            networkDialog.setContentView(com.embehome.embehome.R.layout._load_screen)
            networkDialog.textView12.text = "Please connect to a network"
            networkDialog.loading_screen_cancel.setOnClickListener {
                Log.d("Testing", "Dialog Cancel")
                networkDialog.dismiss()
            }
            networkDialog.loading_layout.setBackgroundColor(Color.WHITE)
            networkDialog.setCancelable(false)
            networkDialog.setCanceledOnTouchOutside(false)
            networkDialog.show()
        }
        catch (e : Exception) {
            Log.e("TronX_Dialog","${e.message} Internet Dialog")
        }
    }

    fun closeInternetAvailableDialog () {
        try {
            if (::networkDialog.isInitialized) networkDialog.dismiss()
        }
        catch (e : Exception) {
            Log.e("TronX_Dialog","${e.message} Internet Dialog dismiss")
        }
    }
}