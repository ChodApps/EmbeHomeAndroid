package com.embehome.embehome.Manager

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.gson.Gson
import com.embehome.embehome.Model.*
import com.embehome.embehome.Services.HttpClient
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.CacheSceneTwoWay
import com.embehome.embehome.Utils.FakeDB
import com.embehome.embehome.irb.model.RemoteCloudModel
import com.embehome.embehome.rules.model.RuleDetails
import com.embehome.embehome.rules.model.RuleUpdateDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.ResponseBody


/** com.tronx.tt_things_app.Manager
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 19-02-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/



object HttpManager {

    //if value  = true the request is in process else you can initiate a new request

    private var isLogin = false
    private var isUserRegister = false
    private var isVerifyEmail = false
    private var isHubONBoard = false
    private var isUpdateHubDetails = false
    private var isFetchCloudData = false
    private var isUpdateProfileData = false
    private var isChangePassword = false
    private var isForgotPassword = false
    private var isGetArea = false
    private var isCreateArea = false
    private var isUpdateArea = false
    private var isAddSwitchBoard = false

    private fun validateHttpResult (result : RequestFeedback?) : RequestFeedback {
        return if (result != null){
            if (result.status)
                RequestFeedback(true, result.body)
            else
                RequestFeedback(false, (result.body as HttpErrorModel).message)
        } else
            RequestFeedback(false, "Connection Failed")
    }

    private fun validateHttpResultAuth (result : RequestFeedback?) : RequestFeedback {
        return if (result != null){
            if (result.status)
                RequestFeedback(true, result.body)
            else
                RequestFeedback(false, (result.body as HttpErrorModel))
        } else
            RequestFeedback(false, "Connection Failed")
    }

    fun getErrorData (response : ResponseBody?): HttpErrorModel = try {
        Gson().fromJson(response?.charStream(), HttpErrorModel::class.java)
    }
    catch (e : Throwable) {
        HttpErrorModel("NA",0,"${e.message}")
    }

    private fun bodyDataMacHub(key : String, value : String) : HashMap<String,String> {
        val temp = HashMap<String, String>()
        temp[key] = value
        return temp
    }

    suspend fun getCloudImage (path : String) = withContext(Main) {
        val a = validateHttpResult (HttpClient.getCloudImage(path))
        if (a.status) {
            try {
                val body = a.body as ResponseBody
                val pic = BitmapFactory.decodeStream(body.byteStream())
                return@withContext RequestFeedback(true, Bitmap.createScaledBitmap(pic, 960, 540, false)).also {
                    AppServices.log("TronX_ImageCld", "image received and decoded")
                }
            }
            catch (e : Exception) {
                AppServices.log("TronX_ImageCld", e.message.toString())
            }
        }
        a
    }

    suspend fun login(email : String, password : String) = withContext(Dispatchers.Main) {
        if (isLogin)
            return@withContext RequestFeedback(false,"Previous request is in Process")

        isLogin = true
        validateHttpResultAuth(HttpClient.login(email, password)).also { isLogin = false }
    }

    suspend fun logout (token :String) = withContext(Main) {
        validateHttpResultAuth(HttpClient.logout(token))
    }
    suspend fun deleteUser (userID :String) = withContext(Main) {
        validateHttpResultAuth(HttpClient.deleteUser(userID))
    }

    suspend fun getTerms () = withContext(Main) {
        validateHttpResult(HttpClient.getLatestTerms())
    }

    suspend fun getUserSettings () = withContext(Main) {
        validateHttpResult(HttpClient.getUserSetting())
    }

    suspend fun setUserSettings (all : Boolean, s : Boolean, a : Boolean) = withContext(Main) {
        validateHttpResult(HttpClient.setUserSetting(all, s, a))
    }

    suspend fun userRegister (email : String, mobile : String, password  : String, terms : Boolean) = withContext(Dispatchers.Main){
        if (isUserRegister)
            return@withContext RequestFeedback(false,"Previous request is in Process")
        isUserRegister = true
        validateHttpResult(HttpClient.register(email, mobile, password, terms)).also { isUserRegister = false }
    }

    suspend fun verifyEmail (email : String, otp : String) = withContext(Dispatchers.Main) {
        if (isVerifyEmail)
            return@withContext RequestFeedback(false,"Previous request is in Process")

        isVerifyEmail = true
        validateHttpResult(HttpClient.verifyEmail(email, otp)).also { isVerifyEmail = false }
    }

    suspend fun resend (email : String) = withContext(Dispatchers.Main) {
        validateHttpResult(HttpClient.resendOTP(email))
    }

    suspend fun hubONBoard (packet : HubUdpData, hubName : String, image : String, ssid : String)  = withContext(Dispatchers.Main){

        if (isHubONBoard)
            return@withContext RequestFeedback(false,"Previous request is in Process")

        isHubONBoard = true
        FakeDB.HubIp = packet.ip
        val macID = packet.macID
        FakeDB.macID = macID
        val hubImage = image
        val hubVersion = packet.version
        validateHttpResult(HttpClient.hubOnBoard(macID, hubName, ssid, hubVersion.toString(), hubImage)).also { isHubONBoard = false }

    }

    suspend fun updateHubDetails (macID: String, serialNumber : String, panID : String)  = withContext(Dispatchers.Main){

        if (isUpdateHubDetails)
            return@withContext RequestFeedback(false,"Previous request is in Process")

        isUpdateHubDetails = true
        validateHttpResultAuth(HttpClient.updateHubDetails(macID, serialNumber, panID)).also { isUpdateHubDetails = false }
    }

    suspend fun fetchCloudData () = withContext(Dispatchers.Main) {
        if (isFetchCloudData)
            return@withContext RequestFeedback(false,"Previous request is in Process")

        isFetchCloudData = true
        validateHttpResult(HttpClient.fetchHubList()).also { isFetchCloudData = false }
    }

    suspend fun updateHubData (macID: String,  name : String,  image :String,  version :String, ssid: String) = withContext(Main) {
        validateHttpResult(HttpClient.updateHubData(macID, name, image, version, ssid))
    }

    suspend fun updateHubVersion (macID: String, version :String) = withContext(Main) {
        validateHttpResult(HttpClient.updateHubVersion(macID, version))
    }

    suspend fun updateProfileDetail(fName : String, lName : String, dName : String, area : String,
                                    city : String, state : String, pincode : String, location : String,
                                    profilePic : String) = withContext(Dispatchers.IO) {

        if (isUpdateProfileData)
            return@withContext RequestFeedback(false,"Previous request is in Process")

        isUpdateProfileData = true

        validateHttpResult(HttpClient.updateProfileDetails(fName, lName, dName, area, city, state, pincode, location, profilePic)).also { isUpdateProfileData = false }
    }

    suspend fun checkDeviceOta (macID: String) = withContext(Main){
        validateHttpResult(HttpClient.checkHubUpdate(macID))
    }

    suspend fun changePassword (old_pwd : String, new_pwd : String) = withContext(Dispatchers.Main) {

        if (isChangePassword)
            return@withContext RequestFeedback(false,"Previous request is in Process")

        isChangePassword = true
        validateHttpResult(HttpClient.changePassword(old_pwd, new_pwd)).also { isChangePassword = false }
    }

    suspend fun forgotPassword (email: String) = withContext(Dispatchers.Main) {

        if (isForgotPassword)
            return@withContext RequestFeedback(false,"Previous request is in Process")

        isForgotPassword = true
        validateHttpResult(HttpClient.forgotPassword(email)).also { isForgotPassword = false }
    }

    suspend fun getAreas () = withContext(Dispatchers.Main) {

        if (isGetArea)
            return@withContext RequestFeedback(false,"Previous request is in Process")

        isGetArea = true
        validateHttpResult(HttpClient.getAreas()).also { isGetArea = false }
    }

    suspend fun createArea (areaName : String, areaImage : MultipartBody.Part) = withContext(Dispatchers.Main) {

        if (isCreateArea)
            return@withContext RequestFeedback(false,"Previous request is in Process")

        isCreateArea = true
        validateHttpResult(HttpClient.createArea(areaName, areaImage)).also { isCreateArea = false }
    }

    suspend fun updateArea (areaID : Int, areaName : String, areaImage : MultipartBody.Part) = withContext(Dispatchers.Main) {

        if (isUpdateArea)
            return@withContext RequestFeedback(false,"Previous request is in Process")

        isUpdateArea = true
        validateHttpResult(HttpClient.updateArea(areaID, areaName, areaImage)).also { isUpdateArea = false }
    }

    suspend fun addSwitchBoard(macID: String, boardData : BoardDetails) = withContext(Dispatchers.IO) {
        if (isAddSwitchBoard)
            return@withContext RequestFeedback(false,"Previous request is in Process")

        isAddSwitchBoard = true
        validateHttpResult(HttpClient.addSwitchBoard(CloudBoardDetails(macID, boardData))).also { isAddSwitchBoard = false }
    }

    private var isGetAllBoard = false
    private var isGetBoard = false
    private var isDeleteHub = false
    private var isDeleteBoard = false

    suspend fun getAllBoards(macID : String) = withContext(Dispatchers.Main) {
        if (isGetAllBoard)
            return@withContext RequestFeedback(false,"Previous request is in Process")

        isGetAllBoard = true
        validateHttpResultAuth(HttpClient.getAllBoards(macID)).also { isGetAllBoard = false }
    }

    suspend fun getBoard(macID : String, areaID : Int) = withContext(Dispatchers.Main) {
        if (isGetBoard)
            return@withContext RequestFeedback(false,"Previous request is in Process")

        isGetBoard = true
        validateHttpResult(HttpClient.getBoard(macID, areaID)).also { isGetBoard = false }
    }

    suspend fun deleteHub (macID : String) = withContext(Dispatchers.Main){
        if (isDeleteHub)
            return@withContext RequestFeedback(false,"Previous request is in Process")

        isDeleteHub = true
        validateHttpResult(HttpClient.deleteHub(bodyDataMacHub("macID", macID))).also { isDeleteHub = false }
    }

    suspend fun deleteBoard (sno : String) = withContext(Dispatchers.Main){
        if (isDeleteBoard)
            return@withContext RequestFeedback(false,"Previous request is in Process")

        isDeleteBoard = true
        validateHttpResult(HttpClient.deleteBoard(bodyDataMacHub("thing_serial_number", sno))).also { isDeleteBoard = false }
    }

    suspend fun updateDeviceDetails (macID: String, boardData: BoardDetails) = withContext(Main) {
        validateHttpResult(HttpClient.updateDeviceData(CloudBoardDetails(macID, boardData)))
    }

    suspend fun updateSwitchDetails (sno : String, switchID: Int, switchName : String, switchType : String, switchIcon : String, watt : Int) = withContext(Main){
        validateHttpResult(HttpClient.updateSwitchData(sno, switchID, switchName, switchType, switchIcon, watt))
    }

    suspend fun getSwitchPowerData (sno: String, switchID: Int) = withContext(Main){
        validateHttpResult(HttpClient.getSwitchPowerData(sno, switchID))
    }

    suspend fun getDevicePowerData (sno: String) = withContext(Main){
        validateHttpResult(HttpClient.getDevicePowerData(sno))
    }

    suspend fun getAreaPowerData (macID : String ,areaID: Int) = withContext(Main){
        validateHttpResult(HttpClient.getAreaPowerData(macID, areaID))
    }

    suspend fun getTodaySwitchPowerData (sno: String, switchID: Int) = withContext(Main){
        validateHttpResult(HttpClient.getTodaySwitchPowerData(sno, switchID))
    }

    suspend fun getTodayDevicePowerData (sno: String) = withContext(Main){
        validateHttpResult(HttpClient.getTodayDevicePowerData(sno))
    }

    suspend fun getTodayAreaPowerData (macID : String ,areaID: Int) = withContext(Main){
        validateHttpResult(HttpClient.getTodayAreaPowerData(macID, areaID))
    }


    suspend fun getTwoWayList (macID: String) = withContext(Main){
        validateHttpResult(HttpClient.getTwoWayList(macID))
    }

    suspend fun getSceneItemList () = withContext(Main) {
        validateHttpResult(HttpClient.getSceneItemList())
    }

    suspend fun getSceneList (macID: String) = withContext(Main) {
        validateHttpResult(HttpClient.getSceneList(macID))
    }

    suspend fun createTwoWay (data : TwoWayItemModel) = withContext(Main) {
        validateHttpResult(HttpClient.createTwoWay(data))
    }

    suspend fun createScene (data : SceneCloudModel) = withContext(Main) {
        validateHttpResult(HttpClient.createScene(data))
    }

    suspend fun updateScene (data : SceneCloudUpdateModel) = withContext(Main) {
        validateHttpResult(HttpClient.updateScene(data))
    }

    suspend fun deleteTwoWay (macID: String, twoWayID: Int) = withContext(Main){
        val data = HashMap<String, Any>().apply {
            this["macID"] = macID
            this["twoway_id"] = twoWayID
        }
        validateHttpResult(HttpClient.deleteTwoWay(data))
    }

    suspend fun deleteScene (macID: String, scenID : Int) = withContext(Main){
        val data = HashMap<String, Any>().apply {
            this["macID"] = macID
            this["scene_id"] = scenID
        }
        validateHttpResult(HttpClient.deleteScene(data))
    }


    suspend fun createSceneItem (name : String, image : MultipartBody.Part) = withContext(Main) {
        validateHttpResult(HttpClient.createSceneItem(name, image))
    }

    suspend fun updateSceneItem (id : Int, name : String, image : MultipartBody.Part) = withContext(Main) {
        validateHttpResult(HttpClient.updateSceneItem(id, name, image))
    }

    ///********************** Remote API Calls **************************///

    suspend fun addRemote (data : RemoteCloudModel) = withContext(Main) {
        validateHttpResult(HttpClient.addRemote(data))
    }

    suspend fun getRemote (macID: String, thing_serial_number : String) = withContext(Main) {
        validateHttpResult(HttpClient.getRemote(macID, thing_serial_number))
    }

    suspend fun deleteRemote (macID: String, thing_serial_number: String, remoteId : Int) = withContext(Main) {
        val body : HashMap<String, Any> = HashMap()
        body["macID"] = macID
        body["thing_serial_number"] = thing_serial_number
        body["remote_id"] = remoteId
        validateHttpResult(HttpClient.deleteRemote(body))
    }

    suspend fun updateRemote (data: RemoteCloudModel) = withContext(Main){
        validateHttpResult(HttpClient.updateRemote(data))
    }

    ///************************FireBase API Calls***********************///

    suspend fun addFCMToken (token : String) = withContext(Main) {
        validateHttpResult(HttpClient.addFCMToken(token, "Android"))
    }

    suspend fun getNotificationList (limit : Int, offset : Int) = withContext(Main){
        validateHttpResult(HttpClient.getNotificationList(limit, offset))
    }

    suspend fun setAlert (
            macID : String,
            thingsID : Int,
            serialNo: String,
            switchID : Int,
            switchStatus : Int,
            time_interval : Int,
            alert_timeStamp : String,
            repeat : Int
    ) = withContext(Main) {
        val res = validateHttpResult(HttpClient.setNotificationAlert(macID, serialNo, switchID, switchStatus, time_interval, alert_timeStamp, repeat))
        if (res.status) {
           val board = CacheSceneTwoWay.getBoardData(macID, thingsID)
            val alert = (res.body as CloudAlertData).data
            board?.switchesList?.forEach {
                if (it.switchId == switchID) {
                    it.alert_data = alert
                }
            }
        }
        res
    }

    suspend fun requestActivityPDF (sDate : String, eDate : String) = withContext(Main) {
        validateHttpResult(HttpClient.requestActivityPDF(sDate, eDate))
    }

    suspend fun deleteAlert (alert_id : Int) = withContext(Main){
        val data = HashMap<String, Int>()
        data["alert_id"] = alert_id
        validateHttpResult(HttpClient.deleteNotificationAlert(data))
    }

    //////////////////////////////// RULE ////////////////////////////////////////////

    suspend fun getRuleItemList () = withContext(Main) {
        validateHttpResult(HttpClient.getRuleItemList())
    }

    suspend fun createRuleItem (name : String, image : MultipartBody.Part) = withContext(Main) {
        validateHttpResult(HttpClient.createRuleItem(name, image))
    }

    suspend fun updateRuleItem (id : Int, name : String, image : MultipartBody.Part) = withContext(Main) {
        validateHttpResult(HttpClient.updateRuleItem(id, name, image))
    }

    suspend fun deleteRule (macID: String, ruleId : Int) = withContext(Main){
        val data = HashMap<String, Any>().apply {
            this["macID"] = macID
            this["rule_id"] = ruleId
        }
        validateHttpResult(HttpClient.deleteRule(data))
    }

    suspend fun getRuleList (macID: String) = withContext(Main) {
        validateHttpResult(HttpClient.getRuleList(macID))
    }

    suspend fun createRule (data : RuleDetails) = withContext(Main) {
        validateHttpResult(HttpClient.createRule(data))
    }

    suspend fun updateRule (data : RuleUpdateDetails) = withContext(Main) {
        validateHttpResult(HttpClient.updateRule(data))
    }

}