package com.embehome.embehome.Services

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Handler
import android.util.Log
import com.embehome.embehome.Manager.LANManager
import com.embehome.embehome.Repository.OperationRepository
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.Utils.NetworkHosts.MqttServerBaseUrl
import info.mqtt.android.service.MqttAndroidClient


//import org.eclipse.paho.android.service.MqttAndroidClient
//import info.mqtt.android.service.MqttAndroidClient

import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.MqttClient


/** com.tronx.tt_things_app.Services
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 11-12-2019.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


object MqttClient {

    private lateinit var client: MqttAndroidClient
    private val pendingRequest: HashMap<String, ArrayList<String>> = HashMap()
    var expectResponse = ""

    fun isConnected(): Boolean {
        return if (::client.isInitialized) {
            client.isConnected
        } else false
    }

    @ExperimentalStdlibApi
    private val runnable = Runnable {
        if (expectResponse.isNotEmpty()) CacheHubData.getAllIpConnectedInNetwork()
    }

    @ExperimentalStdlibApi
    fun connect(context: Context, macID: String) {
        val clientId = MqttClient.generateClientId()
        if (!::client.isInitialized) {
            client = MqttAndroidClient(context, MqttServerBaseUrl, clientId)
        }

        try {
            if (client.isConnected) {
                subscribe(context, macID)
            } else {
                client.connect().actionCallback = object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        Log.d("Mqtt", "Connection Successful")
                        subscribe(context, macID)
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        Log.d("Mqtt", "Connection Failed")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("Mqtt", "Connection Error: ${e.message}")
        }
    }

    @ExperimentalStdlibApi
    fun subscribe(context: Context, macID: String) {
        if (macID.isNotEmpty()) {
            if (client.isConnected) {
                subscribeToTopic(macID, 0)
            } else {
                connect(context, macID)
            }
        }
    }

    fun getTopicId(macID: String) = "hub/$macID/R"
    fun getTopicIdPublish(macID: String) = "hub/$macID"

    @ExperimentalStdlibApi
    fun subscribeToTopic(macID: String, qos: Int) {
        val token = client.subscribe(getTopicId(macID), qos)

        token.actionCallback = object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                client.setCallback(object : MqttCallbackExtended {
                    override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                        Log.d("TronX", "Mqtt Connected Successfully")
                        pendingRequest[macID]?.let { requests ->
                            requests.forEach { cmd ->
                                try {
                                    client.publish(getTopicIdPublish(macID), MqttMessage(cmd.toByteArray()))
                                } catch (e: MqttException) {
                                    e.printStackTrace()
                                }
                            }
                        }
                        pendingRequest.remove(macID)
                    }

                    override fun messageArrived(topic: String?, message: MqttMessage?) {
                        Log.d("TronX_mqtt", "Mqtt Message received ${message.toString()}")
                        handleIncomingMessage(topic, message)
                    }

                    override fun connectionLost(cause: Throwable?) {
                        Log.d("TronX", "Mqtt connection Lost")
                    }

                    override fun deliveryComplete(token: IMqttDeliveryToken?) {
                        Log.d("TronX", "Mqtt message sent")
                        if (expectResponse.isNotEmpty()) Handler().postDelayed(runnable, 5000)
                    }
                })
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                Log.d("TronX", "Mqtt Subscription Failed")
            }
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun handleIncomingMessage(topic: String?, message: MqttMessage?) {
        topic?.let {
            val t = it.split('/')
            if (t.size == 3) {
                val macID = t[1]
                if (expectResponse.isNotEmpty()) {
                    handleExpectedResponse(macID, message.toString())
                } else {
                    OperationRepository.performMqttSwitchOperation(macID, message.toString())
                }
            }
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun handleExpectedResponse(macID: String, message: String) {
        if (expectResponse == "AACB") {
            val data = LANManager.getTcpData(message.removePrefix("{").removeSuffix("}"))
            if (data.size >= 5 && data[0] == expectResponse) {
                CacheHubData.getHub(macID)?.ip?.value = data[4]
                expectResponse = ""
            } else {
                OperationRepository.performMqttSwitchOperation(macID, message)
            }
        }
    }

    @ExperimentalStdlibApi
    fun publishOnMqtt(context: Context, macID: String, cmd: String, expect: String = "") {
        if (::client.isInitialized) {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

            if (networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true) {
                try {
                    if (client.isConnected) {
                        expectResponse = expect
                        client.publish(getTopicIdPublish(macID), MqttMessage(cmd.toByteArray()))
                            .also { Log.d("TronX", "Mqtt Message - $cmd") }
                    } else {
                        pendingRequest.getOrPut(macID) { ArrayList() }.add(cmd)
                        connect(context, macID)
                    }
                } catch (e: Exception) {
                    Log.e("TronX", "Publish Error: ${e.message}")
                }
            } else {
                Log.d("TronX", "Internet not available")
            }
        }
    }

    fun mqttDisconnect(context: Context) {
        if (::client.isInitialized && client.isConnected) {
            client.disconnect(context, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d("Mqtt", "Disconnect Successful")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d("Mqtt", "Disconnect Failed")
                }
            })
        }
    }

    @ExperimentalStdlibApi
    fun onLinkChange(context: Context) {
        CacheHubData.getMacIdList().value?.forEach {
            connect(context, it.macID)
        }
    }
}
