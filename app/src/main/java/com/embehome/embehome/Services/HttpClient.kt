package com.embehome.embehome.Services



import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.embehome.embehome.Interface.HttpAPI
import com.embehome.embehome.Manager.HttpManager
import com.embehome.embehome.Model.*
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.FakeDB
import com.embehome.embehome.Utils.NetworkHosts.HttpServerBaseUrl
import com.embehome.embehome.Utils.NetworkHosts.HttpServerBaseUrlAWS
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/** com.tronx.tt_things_app.Services
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 11-12-2019.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/



object HttpClient {

//    private val instance : HttpAPI by lazy {
//        val retrofit = Retrofit.Builder()
//            .baseUrl(HttpServerBaseUrl)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//
//        retrofit.create(HttpAPI::class.java)
//    }

    private val client = OkHttpClient.Builder().addInterceptor {
        val request = it.request().newBuilder().addHeader("Authorization", "Bearer ${getAuthToken()}").build()
        return@addInterceptor it.proceed(request)
    }.build()

    private val retrofitAuth = Retrofit.Builder()
        .baseUrl(HttpServerBaseUrl)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(HttpAPI::class.java)
    private val retrofitAuthAWS = Retrofit.Builder()
        .baseUrl(HttpServerBaseUrlAWS)
        //.client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(HttpAPI::class.java)

    private val retrofit = Retrofit.Builder()
    .baseUrl(HttpServerBaseUrl)
    .addConverterFactory(GsonConverterFactory.create())
    .build().create(HttpAPI::class.java)

    private var responseReceived : Long = 0

    private fun getAuthToken() = FakeDB.ApplicationToken

    fun connectionFailed() = HttpErrorModel("Connection Failed",0,"Cloud is unreachable")

    suspend fun login( email : String, paswd : String)  = withContext(IO){
        val result = MutableLiveData<RequestFeedback>()
        var responseReceived : Long = 1000

        retrofit.signIN(email, paswd).enqueue(object : Callback<CloudAPIRequestReceiver>{
            override  fun onFailure(call: Call<CloudAPIRequestReceiver>, t: Throwable) {
                Log.d("TronX","hubOnBoard Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }

            override fun onResponse(
                call: Call<CloudAPIRequestReceiver>,
                response: Response<CloudAPIRequestReceiver>
            ) {
                if (response.isSuccessful) {
                    FakeDB.ApplicationToken = response.body()?.auth_token ?: ""
                    FakeDB.message = response.body()?.message ?: ""
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX","Login Successful")
                }
                else {
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "Login Failed Due to error")
                }
                responseReceived = 0
            }
        })

        delay(1000)
        while (responseReceived > 0)
            delay(responseReceived)
        result.value
    }
//    deleteUser
    suspend fun deleteUser (userID : String) = withContext(IO) {
        val result = MutableLiveData<RequestFeedback>()
        responseReceived = 1000

        retrofitAuthAWS.deleteUser(HttpAPI.DeleteUserRequest(userID)).enqueue(object : Callback<CloudAPIRequestReceiver> {
            override fun onFailure(call: Call<CloudAPIRequestReceiver>, t: Throwable) {
                Log.d("TronX","delete user Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }

            override fun onResponse(
                call: Call<CloudAPIRequestReceiver>,
                response: Response<CloudAPIRequestReceiver>
            ) {
                if (response.isSuccessful) {
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX","delete user Successful")
                }
                else {
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "delete user Failed Due to error"+response.toString())
                }
                responseReceived = 0
            }

        })

        while (responseReceived > 0) delay(responseReceived)
        result.value
    }
    suspend fun logout (token : String) = withContext(IO) {
        val result = MutableLiveData<RequestFeedback>()
        responseReceived = 1000

        retrofitAuth.logout(token).enqueue(object : Callback<CloudAPIRequestReceiver> {
            override fun onFailure(call: Call<CloudAPIRequestReceiver>, t: Throwable) {
                Log.d("TronX","Logout Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }

            override fun onResponse(
                call: Call<CloudAPIRequestReceiver>,
                response: Response<CloudAPIRequestReceiver>
            ) {
                if (response.isSuccessful) {
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX","Logout Successful")
                }
                else {
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "Logout Failed Due to error")
                }
                responseReceived = 0
            }

        })

        while (responseReceived > 0) delay(responseReceived)
        result.value
    }

    suspend fun register(email : String, mobile : String, password  : String, terms : Boolean) = withContext(IO) {
        val result = MutableLiveData<RequestFeedback>()
        var responseReceived : Long = 1000
        retrofit.signUP(email,mobile,password, terms).enqueue(object : Callback<CloudAPIRequestReceiver>{
            override fun onFailure(call: Call<CloudAPIRequestReceiver>, t: Throwable) {
                    Log.d("TronX","hubOnBoard Failed During request")
                    result.value = RequestFeedback(false, connectionFailed())
                    responseReceived = 0
            }

            override fun onResponse(
                call: Call<CloudAPIRequestReceiver>,
                response: Response<CloudAPIRequestReceiver>
            ) {
                if (response.isSuccessful) {
                    FakeDB.email = email
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX","User Registration Successful")
                }
                else{
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "User Registration Failed Due to error")
                }
                responseReceived = 0
            }
        })
        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }

    suspend fun resendOTP (email : String) = withContext(IO){

        val result = MutableLiveData<RequestFeedback>()
        responseReceived = 1000
        retrofit.resendOtp(email).enqueue(object : Callback<CloudAPIRequestReceiver>{
            override fun onFailure(call: Call<CloudAPIRequestReceiver>, t: Throwable) {
                AppServices.log("TronX","Resend Otp Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }
            override fun onResponse(
                call: Call<CloudAPIRequestReceiver>,
                response: Response<CloudAPIRequestReceiver>
            ) {
                if (response.isSuccessful) {
                    FakeDB.ApplicationToken = response.body()?.auth_token ?: ""
                    FakeDB.message = response.body()?.message ?: ""
                    result.value = RequestFeedback(true,response.body())
                    AppServices.log("TronX","Resend OTP Successful")
                }
                else {
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    AppServices.log("TronX", "Resend OTP failed Due to error")
                }
                responseReceived = 0
            }
        })
        delay(1000)
        while (responseReceived > 0)
            delay(responseReceived)
        result.value
    }

    suspend fun verifyEmail(email : String, otp : String) = withContext(IO){

        val result = MutableLiveData<RequestFeedback>()
        responseReceived = 1000
        retrofit.verifyOTP(email,otp).enqueue(object : Callback<CloudAPIRequestReceiver>{
            override fun onFailure(call: Call<CloudAPIRequestReceiver>, t: Throwable) {
                AppServices.log("TronX","verify email Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }
            override fun onResponse(
                call: Call<CloudAPIRequestReceiver>,
                response: Response<CloudAPIRequestReceiver>
            ) {
                if (response.isSuccessful) {
                    FakeDB.ApplicationToken = response.body()?.auth_token ?: ""
                    FakeDB.message = response.body()?.message ?: ""
                    result.value = RequestFeedback(true,response.body())
                    AppServices.log("TronX","Email Verification Successful")
                }
                else {
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    AppServices.log("TronX", "Email verification Failed Due to error")
                }
                responseReceived = 0
            }
        })
        delay(1000)
        while (responseReceived > 0)
            delay(responseReceived)
        result.value
    }

    suspend fun getLatestTerms () = withContext(IO) {
        val result = MutableLiveData<RequestFeedback>()
        responseReceived  = 1000
        retrofit.getTermsAndCondition().enqueue(object : Callback<CloudTermsAndCondition>{
            override fun onFailure(call: Call<CloudTermsAndCondition>, t: Throwable) {
                Log.d("TronX","Terms request Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }
            override fun onResponse(
                call: Call<CloudTermsAndCondition>,
                response: Response<CloudTermsAndCondition>
            ) {
                if (response.isSuccessful) {
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX","Terms received successful")
                }
                else{
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "Terms fetch Failed Due to error")
                }
                responseReceived = 0
            }
        })
        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }

    suspend fun getUserSetting () = withContext(IO) {
        val result = MutableLiveData<RequestFeedback>()
        responseReceived  = 1000
        retrofitAuth.getUserSetting().enqueue(object : Callback<NotificationPrefCloudModel>{
            override fun onFailure(call: Call<NotificationPrefCloudModel>, t: Throwable) {
                Log.d("TronX","Fetch User settings request Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }
            override fun onResponse(
                call: Call<NotificationPrefCloudModel>,
                response: Response<NotificationPrefCloudModel>
            ) {
                if (response.isSuccessful) {
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX","User setting received successful")
                }
                else{
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "User setting fetch Failed Due to error")
                }
                responseReceived = 0
            }
        })
        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }

    suspend fun setUserSetting (all : Boolean, s : Boolean, a : Boolean) = withContext(IO) {
        val result = MutableLiveData<RequestFeedback>()
        responseReceived  = 1000
        retrofitAuth.setUserSetting(all, s, a).enqueue(object : Callback<CloudHubDataReceiver>{
            override fun onFailure(call: Call<CloudHubDataReceiver>, t: Throwable) {
                Log.d("TronX","Set user setting request Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }
            override fun onResponse(
                call: Call<CloudHubDataReceiver>,
                response: Response<CloudHubDataReceiver>
            ) {
                if (response.isSuccessful) {
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX","User setting updated successful")
                }
                else{
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "Set user setting Failed Due to error")
                }
                responseReceived = 0
            }
        })
        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }

    suspend fun hubOnBoard(macID : String, hubName : String, ssid : String, hubVersion : String, hubImage : String) = withContext(IO) {
        val result = MutableLiveData<RequestFeedback>()
        responseReceived = 1000
        FakeDB.macID = macID
        retrofitAuth.hubOnBoard( macID, hubName,hubVersion, ssid, hubImage).enqueue(object : Callback<CloudHubDataReceiver>{
            override fun onFailure(call: Call<CloudHubDataReceiver>, t: Throwable) {
                Log.d("TronX","hubOnBoard Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }
            override fun onResponse(
                call: Call<CloudHubDataReceiver>,
                response: Response<CloudHubDataReceiver>
            ) {
                if (response.isSuccessful) {
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX","hubOnBoard Successful")
                }
                else {
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "hubOnBoard Failed Due to error")
                }
                responseReceived = 0
            }
        })
        delay(1000)
        while (responseReceived > 0)
            delay(responseReceived)
        result.value
    }

    suspend fun updateHubDetails(macID : String, serialNo : String, panID : String) = withContext(IO) {
        val result = MutableLiveData<RequestFeedback>()
        responseReceived  = 1000
        retrofitAuth.updateHubDetails(FakeDB.macID, sno = serialNo, panID = panID ).enqueue(object : Callback<CloudAPIRequestReceiver>{
                override fun onFailure(call: Call<CloudAPIRequestReceiver>, t: Throwable) {
                    Log.d("TronX","updateHubDetails Failed During request")
                    result.value = RequestFeedback(false, connectionFailed())
                    responseReceived = 0
                }
                override fun onResponse(
                    call: Call<CloudAPIRequestReceiver>,
                    response: Response<CloudAPIRequestReceiver>
                ) {
                    if (response.isSuccessful) {
                        result.value = RequestFeedback(true,response.body())
                        Log.d("TronX","updateHubDetails successful")
                    }
                    else{
                        result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                        Log.d("TronX", "updateHubDetail Failed Due to error")
                    }
                    responseReceived = 0
                }
        })
        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }

    suspend fun updateHubData ( macID: String,  name : String,  image :String,  version :String, ssid: String) = withContext(IO) {
        val result = MutableLiveData<RequestFeedback>()
        responseReceived  = 1000
        retrofitAuth.updateHubData(macID, name, image, version, ssid).enqueue(object : Callback<CloudAPIRequestReceiver>{
            override fun onFailure(call: Call<CloudAPIRequestReceiver>, t: Throwable) {
                Log.d("TronX","updateHubDetails Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }
            override fun onResponse(
                call: Call<CloudAPIRequestReceiver>,
                response: Response<CloudAPIRequestReceiver>
            ) {
                if (response.isSuccessful) {
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX","updateHubDetails successful")
                }
                else{
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "updateHubDetail Failed Due to error")
                }
                responseReceived = 0
            }
        })
        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }

    suspend fun updateHubVersion (macID: String, version: String) = withContext(IO) {
        val result = MutableLiveData<RequestFeedback>()
        responseReceived  = 1000
        retrofitAuth.updateHubVersion(macID, version).enqueue(object : Callback<CloudAPIRequestReceiver>{
            override fun onFailure(call: Call<CloudAPIRequestReceiver>, t: Throwable) {
                Log.d("TronX","updateHubDetails Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }
            override fun onResponse(
                call: Call<CloudAPIRequestReceiver>,
                response: Response<CloudAPIRequestReceiver>
            ) {
                if (response.isSuccessful) {
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX","updateHubDetails successful")
                }
                else{
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "updateHubDetail Failed Due to error")
                }
                responseReceived = 0
            }
        })
        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }

    suspend fun fetchHubList() = withContext(IO) {
        val result = MutableLiveData<RequestFeedback>()
        responseReceived = 1000
        try {
            retrofitAuth.fetchHubDetails().enqueue(object  : Callback<CloudHubDataList> {
                override fun onFailure(call: Call<CloudHubDataList>, t: Throwable) {
                    Log.d("TronX","Cloud Data fetch Failed During request")
                    result.value = RequestFeedback(false, connectionFailed())
                    responseReceived = 0
                }
                override fun onResponse(call: Call<CloudHubDataList>, response: Response<CloudHubDataList>) {
                    if (response.isSuccessful) {
                        result.value = RequestFeedback(true,response.body())
                        Log.d("TronX","Cloud Data fetched Successful")
                    }
                    else {
                        result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                        Log.d("TronX", "Cloud data fetch Failed Due to error")
                    }
                    responseReceived = 0
                }
            })
        } catch (e: Exception) {
            Log.d("TronX","Cloud Data fetch Failed ${e.message}")
            result.value = RequestFeedback(false, connectionFailed())
            responseReceived = 0
        }
        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }

    suspend fun checkHubUpdate (macID: String) = withContext(IO) {
        val result = MutableLiveData<RequestFeedback>()
        responseReceived = 1000
        try {
            retrofitAuth.otaHub(macID, "hub").enqueue(object  : Callback<OtaCloudResponse> {
                override fun onFailure(call: Call<OtaCloudResponse>, t: Throwable) {
                    Log.d("TronX","Cloud Data fetch Failed During request")
                    result.value = RequestFeedback(false, connectionFailed())
                    responseReceived = 0
                }
                override fun onResponse(call: Call<OtaCloudResponse>, response: Response<OtaCloudResponse>) {
                    if (response.isSuccessful) {
                        result.value = RequestFeedback(true,response.body())
                        Log.d("TronX","Cloud Data fetched Successful")
                    }
                    else {
                        result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                        Log.d("TronX", "Cloud data fetch Failed Due to error")
                    }
                    responseReceived = 0
                }
            })
        } catch (e: Exception) {
            Log.d("TronX","Cloud Data fetch Failed ${e.message}")
            result.value = RequestFeedback(false, connectionFailed())
            responseReceived = 0
        }
        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }


    suspend fun updateProfileDetails(fName : String, lName : String, dName : String, area : String,
                                     city : String, state : String, pincode : String, location : String,
                                     profilePic : String) = withContext(IO) {

        val result = MutableLiveData<RequestFeedback>()
        responseReceived = 1000
        retrofitAuth.updateProfileData(fName,lName, dName, area, city, state, pincode, location, profilePic).enqueue(object : Callback<CloudAPIRequestReceiver> {
            override fun onFailure(call: Call<CloudAPIRequestReceiver>, t: Throwable) {
                Log.d("TronX","User details update Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }

            override fun onResponse(
                call: Call<CloudAPIRequestReceiver>,
                response: Response<CloudAPIRequestReceiver>
            ) {
                if (response.isSuccessful) {
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX","User details updated Successful")
                }
                else {
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "User details update Failed Due to error")
                }
            }

        })
        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }

    suspend fun changePassword (old_pwd : String, new_pwd : String) = withContext(IO) {

        val result = MutableLiveData<RequestFeedback>()
        responseReceived = 1000
        retrofitAuth.changePassword(old_pwd, new_pwd).enqueue(object : Callback<CloudAPIRequestReceiver> {
            override fun onFailure(call: Call<CloudAPIRequestReceiver>, t: Throwable) {
                Log.d("TronX","Change Password Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }

            override fun onResponse(
                call: Call<CloudAPIRequestReceiver>,
                response: Response<CloudAPIRequestReceiver>
            ) {
                if (response.isSuccessful) {
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX","Password Change Successful")
                }
                else {
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "changePassword Failed Due to error")
                }
                responseReceived = 0
            }

        })
        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }

    suspend fun forgotPassword(email : String) = withContext(IO) {

        val result = MutableLiveData<RequestFeedback>()
        responseReceived = 1000
        retrofit.forgotPassword(email).enqueue(object : Callback<CloudAPIRequestReceiver>{
            override fun onFailure(call: Call<CloudAPIRequestReceiver>, t: Throwable) {
                Log.d("TronX", "Forgot password Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }

            override fun onResponse(
                call: Call<CloudAPIRequestReceiver>,
                response: Response<CloudAPIRequestReceiver>
            ) {
                if (response.isSuccessful) {
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX","Forgot Password request Sent Successful")
                }
                else {
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "forgotPassword Failed Due to error")
                }
                responseReceived = 0
            }

        })
        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }

    suspend fun getAreas () = withContext(IO) {

        val result = MutableLiveData<RequestFeedback>()
        responseReceived = 1000
        retrofitAuth.getAreas().enqueue(object : Callback<CloudAreaDataList> {
            override fun onFailure(call: Call<CloudAreaDataList>, t: Throwable) {
                Log.d("TronX","get Areas Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }

            override fun onResponse(
                call: Call<CloudAreaDataList>,
                response: Response<CloudAreaDataList>
            ) {
                if (response.isSuccessful) {
                    FakeDB.areaData = response.body()!!.data
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX","Area List fetched Successful")
                }
                else {
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "get area Failed Due to error")
                }
                responseReceived = 0
            }

        })
        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }

    suspend fun createArea (areaName : String, areaImage : MultipartBody.Part) = withContext(IO) {

        val result = MutableLiveData<RequestFeedback>()
        responseReceived = 1000
        val t = HashMap<String, Any>().apply {
            this["area_name"] = areaName
            this["area_image"] = areaImage
        }
        retrofitAuth.createArea(RequestBody.create(MediaType.parse("text/plain"), areaName), areaImage).enqueue(object : Callback<CloudHubDataReceiver>{
            override fun onFailure(call: Call<CloudHubDataReceiver>, t: Throwable) {
                Log.d("TronX","create Area Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }

            override fun onResponse(
                call: Call<CloudHubDataReceiver>,
                response: Response<CloudHubDataReceiver>
            ) {
                if (response.isSuccessful) {
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX","Area created Successfully")
                }
                else {
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "create area Failed Due to error")
                }
                responseReceived = 0
            }

        })
        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }

    suspend fun updateArea (areaID : Int, areaName: String, areaImage: MultipartBody.Part) = withContext(IO) {

        val result = MutableLiveData<RequestFeedback>()
        responseReceived = 1000
        retrofitAuth.updateArea(RequestBody.create(MediaType.parse("text/plain"), areaName), areaImage, RequestBody.create(MediaType.parse("text/plain"), areaID.toString())).enqueue(object : Callback<CloudAPIRequestReceiver> {
            override fun onFailure(call: Call<CloudAPIRequestReceiver>, t: Throwable) {
                Log.d("TronX","Update area Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }

            override fun onResponse(
                call: Call<CloudAPIRequestReceiver>,
                response: Response<CloudAPIRequestReceiver>
            ) {
                if (response.isSuccessful) {
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX","Area updated Successfully")
                }
                else {
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "update area Failed Due to error")
                }
                responseReceived = 0
            }

        })
        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }

    suspend fun addSwitchBoard(body : CloudBoardDetails) = withContext(IO) {
        val result = MutableLiveData<RequestFeedback>()
        responseReceived = 1000
        retrofitAuth.addSwitchBoard(body).enqueue(object  : Callback<CloudBoardAddFeedback> {
            override fun onFailure(call: Call<CloudBoardAddFeedback>, t: Throwable) {
                Log.d("TronX","${body.things_data.thing_type} Add Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }
            override fun onResponse(
                call: Call<CloudBoardAddFeedback>,
                response: Response<CloudBoardAddFeedback>
            ) {
                if (response.isSuccessful) {
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX","${body.things_data.thing_type} Added Successfully")
                }
                else {
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "${body.things_data.thing_type} Add Failed Due to error")
                }
                responseReceived = 0
            }

        })
        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }

    suspend fun getAllBoards(macID : String) = withContext(Dispatchers.Main){
        val result = MutableLiveData<RequestFeedback>()
        responseReceived = 1000
        retrofitAuth.getAllBoard(macID).enqueue(object : Callback<CloudBoardData>{
            override fun onFailure(call: Call<CloudBoardData>, t: Throwable) {
                Log.d("TronX","Board data request Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }

            override fun onResponse(
                call: Call<CloudBoardData>,
                response: Response<CloudBoardData>
            ) {
                if (response.isSuccessful) {
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX","Board data received Successfully")
                }
                else {
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "Board data request Failed Due to error")
                }
                responseReceived = 0
            }

        })
        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }

    suspend fun getBoard(macID : String, areaID : Int) = withContext(Dispatchers.Main){
        val result = MutableLiveData<RequestFeedback>()
        responseReceived = 1000
        retrofitAuth.getBoard(macID, areaID).enqueue(object : Callback<CloudBoardData>{
            override fun onFailure(call: Call<CloudBoardData>, t: Throwable) {
                Log.d("TronX","" +
                        "Board data request Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }

            override fun onResponse(
                call: Call<CloudBoardData>,
                response: Response<CloudBoardData>
            ) {
                if (response.isSuccessful) {
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX","Board data received Successfully")
                }
                else {
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "Board data request Failed Due to error")
                }
                responseReceived = 0
            }

        })
        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }

    suspend fun deleteHub(macID : HashMap<String,String>) = withContext(IO) {
        val result = MutableLiveData<RequestFeedback>()
        responseReceived = 1000
        retrofitAuth.deleteHub(macID).enqueue(object : Callback<CloudAPIRequestReceiver> {
            override fun onFailure(call: Call<CloudAPIRequestReceiver>, t: Throwable) {
                Log.d("TronX","Hub Delete Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }

            override fun onResponse(
                call: Call<CloudAPIRequestReceiver>,
                response: Response<CloudAPIRequestReceiver>
            ) {
                if (response.isSuccessful) {
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX","Hub Deleted Successfully")
                }
                else {
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "Hub Delete Failed Due to error")
                }
                responseReceived = 0
            }

        })
        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }

    suspend fun deleteBoard(sNO : HashMap<String,String>) = withContext(IO) {
        val result = MutableLiveData<RequestFeedback>()
        responseReceived = 1000
        retrofitAuth.deleteBoard(sNO).enqueue(object : Callback<CloudAPIRequestReceiver> {
            override fun onFailure(call: Call<CloudAPIRequestReceiver>, t: Throwable) {
                Log.d("TronX","Board Delete Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }

            override fun onResponse(
                call: Call<CloudAPIRequestReceiver>,
                response: Response<CloudAPIRequestReceiver>
            ) {
                if (response.isSuccessful) {
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX","Board Deleted Successfully")
                }
                else {
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "Board Delete Failed Due to error")
                }
                responseReceived = 0
            }

        })
        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }

    suspend fun updateDeviceData (body : CloudBoardDetails) = withContext(IO) {
        val result = MutableLiveData<RequestFeedback>()
        responseReceived = 1000

        retrofitAuth.updateDeviceDetails(body).enqueue(object : Callback<CloudAPIRequestReceiver>{
            override fun onFailure(call: Call<CloudAPIRequestReceiver>, t: Throwable) {
                Log.d("TronX","Device detail update Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }

            override fun onResponse(
                call: Call<CloudAPIRequestReceiver>,
                response: Response<CloudAPIRequestReceiver>
            ) {
                if (response.isSuccessful) {
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX","Device detail updated Successfully")
                }
                else {
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "Device detail update Failed Due to error")
                }
                responseReceived = 0
            }

        })

        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }

    suspend fun updateSwitchData (sno : String, switchID: Int, switchName : String, switchType : String, switchIcon : String, watt : Int) = withContext(IO) {
        val result = MutableLiveData<RequestFeedback>()
        responseReceived = 1000
        retrofitAuth.updateSwitchDetails(sno, switchID, switchName, switchType, switchIcon, watt).enqueue( object : Callback<CloudAPIRequestReceiver> {
            override fun onFailure(call: Call<CloudAPIRequestReceiver>, t: Throwable) {
                Log.d("TronX","Switch Update Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }

            override fun onResponse(
                call: Call<CloudAPIRequestReceiver>,
                response: Response<CloudAPIRequestReceiver>
            ) {
                if (response.isSuccessful) {
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX","Switch Updated Successfully Successfully")
                }
                else {
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "Switch Update Failed Due to error")
                }
                responseReceived = 0
            }

        })
        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }

    suspend fun getSwitchPowerData (sno: String, switchID: Int) = withContext(IO) {
        val result = MutableLiveData<RequestFeedback>()
        responseReceived = 1000
        retrofitAuth.getSwitchPowerDetails(sno, switchID).enqueue(object : Callback<PowerUnitModel> {
            override fun onFailure(call: Call<PowerUnitModel>, t: Throwable) {
                Log.d("TronX","Power data fetch Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }

            override fun onResponse(call: Call<PowerUnitModel>, response: Response<PowerUnitModel>) {
                if (response.isSuccessful) {
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX","Power data fetch Successfully")
                }
                else {
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "Power data fetch Due to error")
                }
                responseReceived = 0
            }

        })
        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }

    suspend fun getDevicePowerData (sno: String) = withContext(IO) {
        val result = MutableLiveData<RequestFeedback>()
        responseReceived = 1000
        retrofitAuth.getDevicePowerDetails(sno).enqueue(object : Callback<PowerUnitModel> {
            override fun onFailure(call: Call<PowerUnitModel>, t: Throwable) {
                Log.d("TronX","Power data fetch Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }

            override fun onResponse(call: Call<PowerUnitModel>, response: Response<PowerUnitModel>) {
                if (response.isSuccessful) {
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX","Power data fetch Successfully")
                }
                else {
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "Power data fetch Due to error")
                }
                responseReceived = 0
            }

        })
        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }

    suspend fun getAreaPowerData (macID: String ,areaID: Int) = withContext(IO) {
        val result = MutableLiveData<RequestFeedback>()
        responseReceived = 1000
        retrofitAuth.getAreaPowerDetails(macID, areaID).enqueue(object : Callback<PowerUnitModel> {
            override fun onFailure(call: Call<PowerUnitModel>, t: Throwable) {
                Log.d("TronX","Power data fetch Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }

            override fun onResponse(call: Call<PowerUnitModel>, response: Response<PowerUnitModel>) {
                if (response.isSuccessful) {
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX","Power data fetch Successfully")
                }
                else {
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "Power data fetch Due to error")
                }
                responseReceived = 0
            }

        })
        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }

    suspend fun getTodaySwitchPowerData (sno: String, switchID: Int) = withContext(IO) {
        val result = MutableLiveData<RequestFeedback>()
        responseReceived = 1000
        retrofitAuth.getTodaySwitchPowerDetails(sno, switchID).enqueue(object : Callback<CloudHubDataReceiver> {
            override fun onFailure(call: Call<CloudHubDataReceiver>, t: Throwable) {
                Log.d("TronX","Power data fetch Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }

            override fun onResponse(call: Call<CloudHubDataReceiver>, response: Response<CloudHubDataReceiver>) {
                if (response.isSuccessful) {
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX","Power data fetch Successfully")
                }
                else {
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "Power data fetch Due to error")
                }
                responseReceived = 0
            }

        })
        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }

    suspend fun getTodayDevicePowerData (sno: String) = withContext(IO) {
        val result = MutableLiveData<RequestFeedback>()
        responseReceived = 1000
        retrofitAuth.getTodayDevicePowerDetails(sno).enqueue(object : Callback<CloudHubDataReceiver> {
            override fun onFailure(call: Call<CloudHubDataReceiver>, t: Throwable) {
                Log.d("TronX","Power data fetch Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }

            override fun onResponse(call: Call<CloudHubDataReceiver>, response: Response<CloudHubDataReceiver>) {
                if (response.isSuccessful) {
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX","Power data fetch Successfully")
                }
                else {
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "Power data fetch Due to error")
                }
                responseReceived = 0
            }

        })
        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }

    suspend fun getTodayAreaPowerData (macID: String ,areaID: Int) = withContext(IO) {
        val result = MutableLiveData<RequestFeedback>()
        responseReceived = 1000
        retrofitAuth.getTodayAreaPowerDetails(macID, areaID).enqueue(object : Callback<CloudHubDataReceiver> {
            override fun onFailure(call: Call<CloudHubDataReceiver>, t: Throwable) {
                Log.d("TronX","Power data fetch Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }

            override fun onResponse(call: Call<CloudHubDataReceiver>, response: Response<CloudHubDataReceiver>) {
                if (response.isSuccessful) {
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX","Power data fetch Successfully")
                }
                else {
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "Power data fetch Due to error")
                }
                responseReceived = 0
            }

        })
        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }

    suspend fun getTwoWayList (macID: String) = withContext(IO) {
        val result = MutableLiveData<RequestFeedback>()
        responseReceived = 1000
        retrofitAuth.getTwoWayList(macID).enqueue(object : Callback<CloudTwoWayList> {
            override fun onFailure(call: Call<CloudTwoWayList>, t: Throwable) {
                Log.d("TronX","Two way data fetch Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }

            override fun onResponse(call: Call<CloudTwoWayList>, response: Response<CloudTwoWayList>) {
                if (response.isSuccessful) {
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX","Two way data fetch Successfully")
                }
                else {
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "Two way data fetch Due to error")
                }
                responseReceived = 0
            }

        })
        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }

    suspend fun getSceneItemList () = withContext(IO) {
        val result = MutableLiveData<RequestFeedback>()
        responseReceived = 1000
        retrofitAuth.getSceneItemList().enqueue(object : Callback<CloudSceneItemList> {
            override fun onFailure(call: Call<CloudSceneItemList>, t: Throwable) {
                Log.d("TronX","Scene Item fetch Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }

            override fun onResponse(call: Call<CloudSceneItemList>, response: Response<CloudSceneItemList>) {
                if (response.isSuccessful) {
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX","Scene Item fetch Successfully")
                }
                else {
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "Scene Item fetch failed Due to error")
                }
                responseReceived = 0
            }

        })
        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }

    suspend fun getSceneList (macID: String) = withContext(IO){
        val result = MutableLiveData<RequestFeedback>()
        responseReceived = 1000
        retrofitAuth.getScenesList(macID).enqueue(object : Callback<CloudSceneListRequest> {
            override fun onFailure(call: Call<CloudSceneListRequest>, t: Throwable) {
                Log.d("TronX","Scene fetch Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }

            override fun onResponse(call: Call<CloudSceneListRequest>, response: Response<CloudSceneListRequest>) {
                if (response.isSuccessful) {
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX","Scene fetch Successfully")
                }
                else {
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "Scene fetch failed Due to error")
                }
                responseReceived = 0
            }

        })
        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }

    suspend fun createTwoWay (
            data : TwoWayItemModel
    ) = withContext(IO) {
        val result = MutableLiveData<RequestFeedback>()
        responseReceived = 1000
        retrofitAuth.createTwoWay(data).enqueue(object : Callback<CloudAPIRequestReceiver> {
            override fun onFailure(call: Call<CloudAPIRequestReceiver>, t: Throwable) {
                Log.d("TronX","TwoWay create Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }

            override fun onResponse(call: Call<CloudAPIRequestReceiver>, response: Response<CloudAPIRequestReceiver>) {
                if (response.isSuccessful) {
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX","TwoWay create Successfully")
                }
                else {
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "TwoWay create failed Due to error")
                }
                responseReceived = 0
            }

        })

        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }

    suspend fun createScene (
            data : SceneCloudModel
    ) = withContext(IO) {
        val result = MutableLiveData<RequestFeedback>()
        responseReceived = 1000
        retrofitAuth.createScene(data).enqueue(object : Callback<CloudAPIRequestReceiver> {
            override fun onFailure(call: Call<CloudAPIRequestReceiver>, t: Throwable) {
                Log.d("TronX","Scene create Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }

            override fun onResponse(call: Call<CloudAPIRequestReceiver>, response: Response<CloudAPIRequestReceiver>) {
                if (response.isSuccessful) {
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX","Scene create Successfully")
                }
                else {
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "Scene create failed Due to error")
                }
                responseReceived = 0
            }
        })

        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }

    suspend fun createSceneItem (name : String, Image : MultipartBody.Part) = withContext(IO) {
        val result = MutableLiveData<RequestFeedback>()
        responseReceived = 1000
        retrofitAuth.createSceneItem(RequestBody.create(MediaType.parse("text/plain"), name), Image).enqueue(object : Callback<CloudHubDataReceiver> {
            override fun onFailure(call: Call<CloudHubDataReceiver>, t: Throwable) {
                Log.d("TronX","Scene create item Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }

            override fun onResponse(call: Call<CloudHubDataReceiver>, response: Response<CloudHubDataReceiver>) {
                if (response.isSuccessful) {
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX","Scene item create Successfully")
                }
                else {
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "Scene item create failed Due to error")
                }
                responseReceived = 0
            }

        })
        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }

    suspend fun updateSceneItem (id : Int, name : String, Image : MultipartBody.Part) = withContext(IO) {
        val result = MutableLiveData<RequestFeedback>()
        responseReceived = 1000
        retrofitAuth.updateSceneItem(RequestBody.create(MediaType.parse("text/plain"), name), Image, RequestBody.create(MediaType.parse("text/plain"), id.toString())).enqueue(object : Callback<CloudAPIRequestReceiver> {
            override fun onFailure(call: Call<CloudAPIRequestReceiver>, t: Throwable) {
                Log.d("TronX","Scene item update Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }

            override fun onResponse(call: Call<CloudAPIRequestReceiver>, response: Response<CloudAPIRequestReceiver>) {
                if (response.isSuccessful) {
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX","Scene item update Successfully")
                }
                else {
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "Scene item update failed Due to error")
                }
                responseReceived = 0
            }

        })
        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }

    suspend fun updateScene (data : SceneCloudUpdateModel) = withContext(IO) {
        val result = MutableLiveData<RequestFeedback>()
        responseReceived = 1000
        retrofitAuth.updateScene(data).enqueue(object : Callback<CloudAPIRequestReceiver> {
            override fun onFailure(call: Call<CloudAPIRequestReceiver>, t: Throwable) {
                Log.d("TronX","Scene Update Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }

            override fun onResponse(call: Call<CloudAPIRequestReceiver>, response: Response<CloudAPIRequestReceiver>) {
                if (response.isSuccessful) {
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX","Scene Update Successfully")
                }
                else {
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "Scene Update failed Due to error")
                }
                responseReceived = 0
            }

        })

        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }

    suspend fun deleteTwoWay (data : HashMap<String,Any>) = withContext(IO) {
        val result = MutableLiveData<RequestFeedback>()
        responseReceived = 1000
        retrofitAuth.deleteTwoWay(data).enqueue(object : Callback<CloudAPIRequestReceiver> {
            override fun onFailure(call: Call<CloudAPIRequestReceiver>, t: Throwable) {
                Log.d("TronX","Two Way Delete Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }

            override fun onResponse(call: Call<CloudAPIRequestReceiver>, response: Response<CloudAPIRequestReceiver>) {
                if (response.isSuccessful) {
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX","Two Way deleted Successfully")
                }
                else {
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "Two Way delete failed Due to error")
                }
                responseReceived = 0
            }

        })
        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }

    suspend fun deleteScene (data : HashMap<String,Any>) = withContext(IO) {
        val result = MutableLiveData<RequestFeedback>()
        responseReceived = 1000
        retrofitAuth.deleteScene(data).enqueue(object : Callback<CloudAPIRequestReceiver> {
            override fun onFailure(call: Call<CloudAPIRequestReceiver>, t: Throwable) {
                Log.d("TronX","Scene Delete Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }

            override fun onResponse(call: Call<CloudAPIRequestReceiver>, response: Response<CloudAPIRequestReceiver>) {
                if (response.isSuccessful) {
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX","Scene deleted Successfully")
                }
                else {
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "Scene delete failed Due to error")
                }
                responseReceived = 0
            }

        })
        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }

    suspend fun addRemote (data : RemoteCloudModel) = withContext(IO) {
        val result = MutableLiveData<RequestFeedback>()
        responseReceived = 1000

        retrofitAuth.addRemote(data).enqueue(object : Callback<RemoteAddCloudModel> {
            override fun onFailure(call: Call<RemoteAddCloudModel>, t: Throwable) {
                Log.d("TronX","Add Remote Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }

            override fun onResponse(
                call: Call<RemoteAddCloudModel>,
                response: Response<RemoteAddCloudModel>
            ) {
                if (response.isSuccessful) {
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX","Add Remote Successfully")
                }
                else {
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "Add Remote failed Due to error")
                }
                responseReceived = 0
            }

        })


        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }

    suspend fun updateRemote (data : RemoteCloudModel) = withContext(IO) {
        val result = MutableLiveData<RequestFeedback>()
        responseReceived = 1000

        retrofitAuth.updateRemote(data).enqueue(object : Callback<CloudAPIRequestReceiver> {
            override fun onFailure(call: Call<CloudAPIRequestReceiver>, t: Throwable) {
                Log.d("TronX","Update Remote Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }

            override fun onResponse(
                call: Call<CloudAPIRequestReceiver>,
                response: Response<CloudAPIRequestReceiver>
            ) {
                if (response.isSuccessful) {
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX","Update Remote Successfully")
                }
                else {
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "Update Remote failed Due to error")
                }
                responseReceived = 0
            }

        })


        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }

    suspend fun getRemote (macID: String, serialNo: String) = withContext(IO) {
        val result = MutableLiveData<RequestFeedback>()
        responseReceived = 1000

        retrofitAuth.getRemoteList(macID, serialNo).enqueue(object : Callback<CloudIrbRemoteList> {
            override fun onFailure(call: Call<CloudIrbRemoteList>, t: Throwable) {
                Log.d("TronX","Remote fetch Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }

            override fun onResponse(
                call: Call<CloudIrbRemoteList>,
                response: Response<CloudIrbRemoteList>
            ) {
                if (response.isSuccessful) {
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX","Remote Fetch Successfully")
                }
                else {
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "Remote fetch failed Due to error")
                }
                responseReceived = 0
            }
        })

        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }

    suspend fun deleteRemote (body : HashMap<String, Any>) = withContext(IO) {
        val result = MutableLiveData<RequestFeedback>()
        responseReceived = 1000

        retrofitAuth.deleteRemote(body).enqueue(object : Callback<CloudAPIRequestReceiver>{
            override fun onFailure(call: Call<CloudAPIRequestReceiver>, t: Throwable) {
                Log.d("TronX","Delete Remote Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }

            override fun onResponse(
                call: Call<CloudAPIRequestReceiver>,
                response: Response<CloudAPIRequestReceiver>
            ) {
                if (response.isSuccessful) {
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX","delete Remote Successfully")
                }
                else {
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "delete Remote failed Due to error")
                }
                responseReceived = 0
            }

        })



        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }



    suspend fun addFCMToken (token : String, type : String)  = withContext(IO) {
        val result = MutableLiveData<RequestFeedback>()
        responseReceived = 1000

        retrofitAuth.addFCM(token, type).enqueue(object : Callback<CloudAPIRequestReceiver> {
            override fun onFailure(call: Call<CloudAPIRequestReceiver>, t: Throwable) {
                Log.d("TronX","FCM Registration Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }

            override fun onResponse(call: Call<CloudAPIRequestReceiver>, response: Response<CloudAPIRequestReceiver>) {
                if (response.isSuccessful) {
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX","FCM Registered Successfully")
                }
                else {
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "FCM Register failed Due to error")
                }
                responseReceived = 0
            }

        })

        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }

    suspend fun getNotificationList (limit : Int, offset : Int) = withContext(IO) {
        val result = MutableLiveData<RequestFeedback>()
        responseReceived = 1000

        retrofitAuth.getNotification(HashMap<String, Int>().apply {
            set("limit", limit)
            set ("offset", offset)
        }).enqueue(object : Callback<NotificationCloudModel> {
            override fun onFailure(call: Call<NotificationCloudModel>, t: Throwable) {
                Log.d("TronX","Notification fetch  Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }

            override fun onResponse(call: Call<NotificationCloudModel>, response: Response<NotificationCloudModel>) {
                if (response.isSuccessful) {
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX","Notification fetched Successfully")
                }
                else {
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "Notification fetch failed Due to error")
                }
                responseReceived = 0
            }

        })

        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }

    suspend fun requestActivityPDF (sDate : String, eDate : String) = withContext(IO) {
        val result = MutableLiveData<RequestFeedback>()
        responseReceived = 1000
        retrofitAuth.activityPDFRequest(sDate, eDate).enqueue(object : Callback<CloudHubDataReceiver> {
            override fun onFailure(call: Call<CloudHubDataReceiver>, t: Throwable) {
                Log.d("TronX","Notification fetch  Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }

            override fun onResponse(call: Call<CloudHubDataReceiver>, response: Response<CloudHubDataReceiver>) {
                if (response.isSuccessful) {
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX","Notification fetched Successfully")
                }
                else {
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "Notification fetch failed Due to error")
                }
                responseReceived = 0
            }

        })

        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }



    suspend fun setNotificationAlert (
            macID : String,
            serialNo: String,
            switchID : Int,
            switchStatus : Int,
            time_interval : Int,
            alert_timeStamp : String,
            repeat : Int
    ) = withContext(IO) {
        val result = MutableLiveData<RequestFeedback>()
        responseReceived = 1000

        val alert = CloudSetAlertModel(macID, serialNo, switchID, switchStatus, time_interval, alert_timeStamp, repeat)

        retrofitAuth.setAlert(
                alert
        ).enqueue(object : Callback<CloudAlertData>{
            override fun onFailure(call: Call<CloudAlertData>, t: Throwable) {
                Log.d("TronX","Set Alert Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }

            override fun onResponse(call: Call<CloudAlertData>, response: Response<CloudAlertData>) {
                if (response.isSuccessful) {
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX"," Alert Set Successfully")
                }
                else {
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "Set Alert failed Due to error")
                }
                responseReceived = 0
            }

        })

        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }

    suspend fun deleteNotificationAlert (body : HashMap<String, Int>) = withContext(IO) {
        val result = MutableLiveData<RequestFeedback>()
        responseReceived = 1000
        retrofitAuth.deleteAlert(body).enqueue(object : Callback<CloudAPIRequestReceiver> {
            override fun onFailure(call: Call<CloudAPIRequestReceiver>, t: Throwable) {
                Log.d("TronX","delete Alert Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }

            override fun onResponse(call: Call<CloudAPIRequestReceiver>, response: Response<CloudAPIRequestReceiver>) {
                if (response.isSuccessful) {
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX"," Alert delete Successfully")
                }
                else {
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "delete Alert failed Due to error")
                }
                responseReceived = 0
            }

        })

        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }

    suspend fun getCloudImage (path : String) = withContext(IO) {
        val result = MutableLiveData<RequestFeedback>()
        responseReceived = 1000
        retrofitAuth.getImageCloud(path).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("TronX","Image fetch Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX","Image fetched Successfully")
                }
                else {
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "Image fetch failed Due to error")
                }
                responseReceived = 0
            }

        })

        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }


    /////////////////////////// RULE /////////////////////////////////

    suspend fun getRuleItemList () = withContext(IO) {
        val result = MutableLiveData<RequestFeedback>()
        responseReceived = 1000
        retrofitAuth.getRuleItemList().enqueue(object : Callback<CloudRuleItemList> {
            override fun onFailure(call: Call<CloudRuleItemList>, t: Throwable) {
                Log.d("TronX","Rule Item fetch Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }

            override fun onResponse(call: Call<CloudRuleItemList>, response: Response<CloudRuleItemList>) {
                if (response.isSuccessful) {
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX","Rule Item fetch Successfully")
                }
                else {
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "Rule Item fetch failed Due to error")
                }
                responseReceived = 0
            }

        })
        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }

    suspend fun createRuleItem (name : String, Image : MultipartBody.Part) = withContext(IO) {
        val result = MutableLiveData<RequestFeedback>()
        responseReceived = 1000
        retrofitAuth.createRuleItem(RequestBody.create(MediaType.parse("text/plain"), name), Image).enqueue(object : Callback<CloudHubDataReceiver> {
            override fun onFailure(call: Call<CloudHubDataReceiver>, t: Throwable) {
                Log.d("TronX","Rule create Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }

            override fun onResponse(call: Call<CloudHubDataReceiver>, response: Response<CloudHubDataReceiver>) {

                if (response.isSuccessful) {
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX","Rule create Successfully")
                }
                else {
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "Rule Item create failed Due to error")
                }
                responseReceived = 0
            }

        })
        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }

    suspend fun updateRuleItem (id : Int, name : String, Image : MultipartBody.Part) = withContext(IO) {
        val result = MutableLiveData<RequestFeedback>()
        responseReceived = 1000
        retrofitAuth.updateRuleItem(RequestBody.create(MediaType.parse("text/plain"), name), Image, RequestBody.create(MediaType.parse("text/plain"), id.toString())).enqueue(object : Callback<CloudAPIRequestReceiver> {
            override fun onFailure(call: Call<CloudAPIRequestReceiver>, t: Throwable) {
                Log.d("TronX","Rule item update Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }

            override fun onResponse(call: Call<CloudAPIRequestReceiver>, response: Response<CloudAPIRequestReceiver>) {
                if (response.isSuccessful) {
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX","Rule item update Successfully")
                }
                else {
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "Rule item update failed Due to error")
                }
                responseReceived = 0
            }

        })
        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }

    suspend fun deleteRule (data : HashMap<String,Any>) = withContext(IO) {
        val result = MutableLiveData<RequestFeedback>()
        responseReceived = 1000
        retrofitAuth.deleteRule(data).enqueue(object : Callback<CloudAPIRequestReceiver> {
            override fun onFailure(call: Call<CloudAPIRequestReceiver>, t: Throwable) {
                Log.d("TronX","Rule Delete Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }

            override fun onResponse(call: Call<CloudAPIRequestReceiver>, response: Response<CloudAPIRequestReceiver>) {
                if (response.isSuccessful) {
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX","Rule deleted Successfully")
                }
                else {
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "Rule delete failed Due to error")
                }
                responseReceived = 0
            }

        })
        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }

    suspend fun getRuleList (macID: String) = withContext(IO){
        val result = MutableLiveData<RequestFeedback>()
        responseReceived = 1000
        retrofitAuth.getRuleList(macID).enqueue(object : Callback<CloudRuleDetails> {
            override fun onFailure(call: Call<CloudRuleDetails>, t: Throwable) {
                Log.d("TronX","Rule fetch Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }

            override fun onResponse(call: Call<CloudRuleDetails>, response: Response<CloudRuleDetails>) {
                if (response.isSuccessful) {
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX","Rule fetch Successfully")
                }
                else {
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "Rule fetch failed Due to error")
                }
                responseReceived = 0
            }

        })
        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }

    suspend fun createRule (
        data : RuleDetails
    ) = withContext(IO) {
        val result = MutableLiveData<RequestFeedback>()
        responseReceived = 1000
        retrofitAuth.createRule(data).enqueue(object : Callback<CloudAPIRequestReceiver> {
            override fun onFailure(call: Call<CloudAPIRequestReceiver>, t: Throwable) {
                Log.d("TronX","Rule create Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }

            override fun onResponse(call: Call<CloudAPIRequestReceiver>, response: Response<CloudAPIRequestReceiver>) {
                if (response.isSuccessful) {
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX","Rule create Successfully")
                }
                else {
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "Rule create failed Due to error")
                }
                responseReceived = 0
            }
        })

        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }

    suspend fun updateRule (data : RuleUpdateDetails) = withContext(IO) {
        val result = MutableLiveData<RequestFeedback>()
        responseReceived = 1000
        retrofitAuth.updateRule(data).enqueue(object : Callback<CloudAPIRequestReceiver> {
            override fun onFailure(call: Call<CloudAPIRequestReceiver>, t: Throwable) {
                Log.d("TronX","Rule Update Failed During request")
                result.value = RequestFeedback(false, connectionFailed())
                responseReceived = 0
            }

            override fun onResponse(call: Call<CloudAPIRequestReceiver>, response: Response<CloudAPIRequestReceiver>) {
                if (response.isSuccessful) {
                    result.value = RequestFeedback(true,response.body())
                    Log.d("TronX","Rule Update Successfully")
                }
                else {
                    result.value = RequestFeedback(false,HttpManager.getErrorData(response.errorBody()))
                    Log.d("TronX", "Rule Update failed Due to error")
                }
                responseReceived = 0
            }

        })

        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }

    suspend fun httpClientTemplate () = withContext(IO) {
        val result = MutableLiveData<RequestFeedback>()
        responseReceived = 1000

        delay(1000)
        while (responseReceived > 0) delay(responseReceived)
        result.value
    }

}