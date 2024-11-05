package com.embehome.embehome.Interface

import com.embehome.embehome.Model.*
import com.embehome.embehome.irb.model.CloudIrbRemoteList
import com.embehome.embehome.irb.model.RemoteAddCloudModel
import com.embehome.embehome.irb.model.RemoteCloudModel
import com.embehome.embehome.notification.model.NotificationCloudModel
import com.embehome.embehome.notification.model.NotificationPrefCloudModel
import com.embehome.embehome.ota.OtaCloudResponse
import com.embehome.embehome.rules.model.CloudRuleDetails
import com.embehome.embehome.rules.model.CloudRuleItemList
import com.embehome.embehome.rules.model.RuleDetails
import com.embehome.embehome.rules.model.RuleUpdateDetails
import okhttp3.*
import retrofit2.Call
import retrofit2.http.*
import retrofit2.http.Headers


/** com.tronx.tt_things_app.Interface
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 11-12-2019.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


interface HttpAPI {

    @GET ("{path}")
    fun getImageCloud (@Path ("path", encoded = true) path : String) : Call<ResponseBody>


    /////////****************** Auth *************************/////////////////////////
    @FormUrlEncoded
    @POST("api/user/signin")
    fun signIN(@Field("email") email : String,
               @Field("password") password : String ) : Call<CloudAPIRequestReceiver>


    @FormUrlEncoded
    @POST("api/user/signup")
    fun signUP(@Field("email") email : String,
               @Field("phone_number") phone : String,
               @Field("password") pwd : String,
               @Field("latest_terms_accepted") terms : Boolean
    ) : Call<CloudAPIRequestReceiver>

    @FormUrlEncoded
    @POST("api/user/verifyemail")
    fun verifyOTP(@Field("email") email : String,
                  @Field("otp") otp : String) : Call<CloudAPIRequestReceiver>

    @FormUrlEncoded
    @POST("api/user/profile_details")
    fun updateProfileData(@Field ("first_name") fName : String,
                          @Field ("last_name") lName : String,
                          @Field ("display_name") dName : String,
                          @Field ("area") area : String,
                          @Field ("city") city : String,
                          @Field ("state") state : String,
                          @Field ("pincode") pincode : String,
                          @Field ("location") location : String,
                          @Field ("profile_pic") dp : String) : Call<CloudAPIRequestReceiver>

    @FormUrlEncoded
    @POST("api/user/change_password")
    fun changePassword (@Field ("old_password") old_pwd : String, @Field ("new_password") new_pwd : String) : Call<CloudAPIRequestReceiver>

    @FormUrlEncoded
    @POST("api/user/forgot_password")
    fun forgotPassword(@Field ("email") email : String) : Call<CloudAPIRequestReceiver>

    @FormUrlEncoded
    @POST("api/user/signout")
    fun logout (@Field ("fcm_token")token : String) : Call<CloudAPIRequestReceiver>

    data class DeleteUserRequest(val userID: String)

    @Headers("Content-Type: application/json")
    @POST("/Store/removeUser")
    fun deleteUser(@Body request: DeleteUserRequest): Call<CloudAPIRequestReceiver>


    @FormUrlEncoded
    @POST("api/user/resend_otp")
    fun resendOtp (@Field ("email")email : String) : Call<CloudAPIRequestReceiver>

    @GET ("api/user/get_terms")
    fun getTermsAndCondition () : Call<CloudTermsAndCondition>


    @GET ("api/user/get_settings")
    fun getUserSetting () : Call<NotificationPrefCloudModel>

    @FormUrlEncoded
    @POST ("api/user/update_settings")
    fun setUserSetting (
        @Field ("fcm_enabled") allNotification : Boolean,
        @Field ("fcm_thing_status") deviceStatus : Boolean,
        @Field ("fcm_alert_status") alertStatus : Boolean
    ) : Call<CloudHubDataReceiver>

//////////////******************** Hub **************************////////////////////

    @FormUrlEncoded
    @POST("api/hub/onboard")
    fun hubOnBoard(@Field ("macID") macID : String,
                   @Field ("hub_name") name : String,
                   @Field ("hub_version") hubVersion : String,
                   @Field ("wifi_SSID") ssid : String,
                   @Field ("hub_image") hub_image : String) : Call<CloudHubDataReceiver>

    @FormUrlEncoded
    @POST("api/hub/update_details")
    fun updateHubDetails(@Field ("macID") macID : String,
                         @Field ("serial_number") sno : String,
                         @Field ("pan_ID") panID : String) : Call<CloudAPIRequestReceiver>


    @GET("api/hub/list")
    fun fetchHubDetails() : Call<CloudHubDataList>

    @HTTP(method = "DELETE", path = "api/hub/delete", hasBody =  true)
    fun deleteHub(@Body body : HashMap<String,String>) : Call<CloudAPIRequestReceiver>

    @FormUrlEncoded
    @POST("api/hub/update_details")
    fun updateHubData (@Field("macID") macID: String, @Field("hub_name") name : String, @Field("hub_image") image :String, @Field("hub_version") version :String, @Field("wifi_SSID")ssid: String) : Call<CloudAPIRequestReceiver>

    @FormUrlEncoded
    @POST("api/hub/update_details")
    fun updateHubVersion (@Field("macID") macID: String, @Field("hub_version") version :String) : Call<CloudAPIRequestReceiver>


    @FormUrlEncoded
    @POST ("api/ota/ota_checkupdate")
    fun otaHub (@Field("macID") macID: String, @Field("module_type") moduleType : String) : Call<OtaCloudResponse>
    ////////////////*************** Areas **************************////////////////////////////


    @Multipart
    @POST("api/areas/create")
    fun createArea (@Part ("area_name") area_name : RequestBody,
                    @Part area_image : MultipartBody.Part
                    ) : Call<CloudHubDataReceiver>

    @Multipart
    @POST("api/areas/update")
    fun updateArea (@Part ("area_name") area_name: RequestBody,
                    @Part  area_image : MultipartBody.Part,
                    @Part ("area_id") areaID : RequestBody) :Call<CloudAPIRequestReceiver>


    @GET("api/areas/list")
    fun getAreas () : Call<CloudAreaDataList>

    //////////////*************** Things *******************///////////////////////

    @POST("api/things/add")
    fun addSwitchBoard(@Body body : CloudBoardDetails) : Call<CloudBoardAddFeedback>

    @FormUrlEncoded
    @POST("api/things/list")
    fun getAllBoard(@Field("macID") macID : String) : Call<CloudBoardData>

    @FormUrlEncoded
    @POST("api/things/list")
    fun getBoard(@Field("macID") macID : String, @Field("area_id") areaID : Int) : Call<CloudBoardData>

    @HTTP(method = "DELETE", path = "api/things/delete", hasBody =  true)
    fun deleteBoard(@Body body : HashMap<String,String>) : Call<CloudAPIRequestReceiver>

    @FormUrlEncoded
    @POST ("api/things/update_switch")
    fun updateSwitchDetails (
        @Field ("thing_serial_number") thing_serial_number: String,
        @Field ("switchId") switchId: Int,
        @Field ("switchName") switchName : String,
        @Field ("switchType") switchType: String,
        @Field ("switchIconId") switchIconId: String,
        @Field ("switchWattage") switchWattage : Int
    ) : Call<CloudAPIRequestReceiver>

    @POST ("api/things/update")
    fun updateDeviceDetails (
        @Body body : CloudBoardDetails
    ) : Call<CloudAPIRequestReceiver>

    @FormUrlEncoded
    @POST ("api/things/power_units")
    fun getSwitchPowerDetails (@Field ("thing_serial_number") sno: String, @Field ("switchId")switchId: Int) : Call<PowerUnitModel>

    @FormUrlEncoded
    @POST ("api/things/thing_units")
    fun getDevicePowerDetails (@Field ("thing_serial_number") sno: String) : Call<PowerUnitModel>

    @FormUrlEncoded
    @POST ("api/things/area_units")
    fun getAreaPowerDetails (@Field("macID") macID: String, @Field ("area_id") areaId: Int) : Call<PowerUnitModel>

    @FormUrlEncoded
    @POST ("api/things/power_units")
    fun getTodaySwitchPowerDetails (@Field ("thing_serial_number") sno: String, @Field ("switchId")switchId: Int) : Call<CloudHubDataReceiver>

    @FormUrlEncoded
    @POST ("api/things/thing_units")
    fun getTodayDevicePowerDetails (@Field ("thing_serial_number") sno: String) : Call<CloudHubDataReceiver>

    @FormUrlEncoded
    @POST ("api/things/area_units")
    fun getTodayAreaPowerDetails (@Field("macID") macID: String, @Field ("area_id") areaId: Int) : Call<CloudHubDataReceiver>

    /////////////************ Two Way APIs ***********////////////////////

    @FormUrlEncoded
    @POST("api/things/list_twoway")
    fun getTwoWayList(@Field ("macID") macID: String) : Call<CloudTwoWayList>

    @POST("api/things/create_twoway")
    fun createTwoWay (
            @Body body : TwoWayItemModel
    ) : Call<CloudAPIRequestReceiver>

    @HTTP(method = "DELETE", path = "api/things/delete_twoway", hasBody = true)
    fun deleteTwoWay (@Body body : HashMap<String,Any>) : Call<CloudAPIRequestReceiver>


    ///////////////************ Scene item ***************///////////////

    @GET("api/scenes/list_sceneitem")
    fun getSceneItemList() : Call<CloudSceneItemList>

    @Multipart
    @POST("api/scenes/create_sceneitem")
    fun createSceneItem(
            @Part("scene_name") name : RequestBody,
            @Part image : MultipartBody.Part
    ) : Call<CloudHubDataReceiver>


    @Multipart
    @POST("api/scenes/update_sceneitem")
    fun updateSceneItem (@Part ("scene_name") scene_name: RequestBody,
                    @Part  scene_image : MultipartBody.Part,
                    @Part ("scenename_id") scene_id : RequestBody) :Call<CloudAPIRequestReceiver>


    ////////////////////**************** Scenes **********************//////////////////

    @HTTP(method = "DELETE", path = "api/scenes/delete_scene", hasBody = true)
    fun deleteScene (@Body body : HashMap<String, Any>) : Call<CloudAPIRequestReceiver>

    @FormUrlEncoded
    @POST("api/scenes/list_scenes")
    fun getScenesList(@Field ("macID") macID: String ) : Call<CloudSceneListRequest>


    @POST("api/scenes/create_scene")
    fun createScene(
            @Body body : SceneCloudModel
     ) : Call<CloudAPIRequestReceiver>

    @POST ("api/scenes/update_scene")
    fun updateScene(@Body body : SceneCloudUpdateModel) : Call<CloudAPIRequestReceiver>

    ////////////////////************** Remote ************//////////////

    @POST("api/remote/add")
    fun addRemote (@Body body : RemoteCloudModel) : Call<RemoteAddCloudModel>

    @POST("api/remote/update")
    fun updateRemote (@Body body : RemoteCloudModel) : Call<CloudAPIRequestReceiver>

    @FormUrlEncoded
    @POST ("api/remote/list")
    fun getRemoteList (@Field ("macID") macID : String, @Field ("thing_serial_number") thing_serial_number : String) : Call<CloudIrbRemoteList>

    @HTTP(method = "DELETE", path = "api/remote/delete", hasBody = true)
    fun deleteRemote(@Body body : HashMap<String, Any>) : Call<CloudAPIRequestReceiver>

    /////////********************* FCM ******************//////////////////

    @FormUrlEncoded
    @POST("/api/fcm/add")
    fun addFCM( @Field ("fcm_token") token : String, @Field ("device_type") type : String) : Call<CloudAPIRequestReceiver>


    @POST ("api/fcm/list_notifications")
    fun getNotification (
       /* @Field ("limit") limit : Int,
        @Field ("offset") offset : Int*/
    @Body body : HashMap <String, Int>
    ) : Call<NotificationCloudModel>

    @FormUrlEncoded
    @POST ("api/fcm/send_pdf")
    fun activityPDFRequest (
        @Field ("start_date") sDate : String,
        @Field ("end_date") eDate : String
    ) : Call<CloudHubDataReceiver>

    ///////////////////************** Alert *****************/////////////////

    /*@FormUrlEncoded
    @POST ("api/alerts/add")
    fun setAlert (
            @Field ("macID") macID : String,
            @Field ("thing_serial_number") thing_serial_number : String,
            @Field (value = "switchId") switchId : Int,
            @Field ("switchstatus") switchstatus : Int,
            @Field ("time_interval") time_interval : Int,
            @Field ("alert_timestamp") alert_timestamp : String,
            @Field ("repeat_alert") alert : Int
    ) : Call<CloudAlertData>*/

    @POST ("api/alerts/add")
    fun setAlert (
        @Body body : CloudSetAlertModel
    ) : Call<CloudAlertData>

    @HTTP (method = "DELETE", path = "api/alerts/delete", hasBody = true)
    fun deleteAlert (@Body body : HashMap<String, Int>) : Call<CloudAPIRequestReceiver>

    ///////////////************ Rule item ***************///////////////

    @GET("api/rules/list_ruleitem")
    fun getRuleItemList() : Call<CloudRuleItemList>

    @Multipart
    @POST("api/rules/create_ruleitem")
    fun createRuleItem(
        @Part("rule_name") name : RequestBody,
        @Part image : MultipartBody.Part
    ) : Call<CloudHubDataReceiver>


    @Multipart
    @POST("api/rules/update_ruleitem")
    fun updateRuleItem (@Part ("rule_name") scene_name: RequestBody,
                         @Part  scene_image : MultipartBody.Part,
                         @Part ("rulename_id") scene_id : RequestBody) :Call<CloudAPIRequestReceiver>


    ////////////////////**************** Rule **********************//////////////////

    @HTTP(method = "DELETE", path = "api/rules/delete_rule", hasBody = true)
    fun deleteRule (@Body body : HashMap<String, Any>) : Call<CloudAPIRequestReceiver>

    @FormUrlEncoded
    @POST("api/rules/list_rules")
    fun getRuleList(@Field ("macID") macID: String ) : Call<CloudRuleDetails>


    @POST("api/rules/create_rule")
    fun createRule(
        @Body body : RuleDetails
    ) : Call<CloudAPIRequestReceiver>

    @POST ("api/rules/update_rule")
    fun updateRule (@Body body : RuleUpdateDetails) : Call<CloudAPIRequestReceiver>


}