package com.embehome.embehome.Utils


/** com.tronx.tt_things_app.Constants
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 11-12-2019.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


object NetworkHosts {

    //HTTPS Data
    private const val offlineServerUrlHttp = "http://10.11.10.22:4040"
    private const val stagingUrlHttp = "http://182.72.28.110:4040"
    private const val stagingUrlHttp1 = "https://tronx-things-staging.smartron.com"
    private const val betaTestHttpServer = "https://tronx-things-beta.smartron.com"
    //private const val productionHttpServer = "https://tronx-things.smartron.com"
    //private const val productionHttpServer = "http://2ssquare.in:4040"
    //private const val productionHttpServer = "https://15.207.82.20:4040"
    private const val productionHttpServer = "https://2ssquare.in"
    const val HttpServerBaseUrl = productionHttpServer

    private const val productionHttpServerAWS = "https://25dyn0s3l1.execute-api.ap-south-1.amazonaws.com"
    const val HttpServerBaseUrlAWS = productionHttpServerAWS

    const val AboutUs = "https://embehome.in/about.html"
    const val ContactUs = "https://embehome.in/contact.html"
    const val termsAndConditionsURL = "https://tronxthings.com/terms-and-conditions/"
    const val privacyPolicyURL = "https://tronxthings.com/privacy-policy/"


    //MQTT Data
   // const val MqttServerBaseUrl = "tcp://tthings.io:1883"
    private const val offlineServerUrlMqtt = "tcp://10.11.10.22:1883"
    private const val stagingURLMQTT = "tcp://182.72.28.110:1883"
    private const val stagingURLMQTT1 = "tcp://tronx-things-mqtt.smartron.com:1883"
    private const val betaTestMqttServer = "tcp://tronx-things-mqttbeta.smartron.com:1883"
    //private const val productionMqttServer = "tcp://tronx-things-mqtt-prod.smartron.com:1883"
    private const val productionMqttServer = "tcp://ha.2ssquare.in:1883"
    const val MqttServerBaseUrl = productionMqttServer
    fun getTopic(macID : String) = "hub/$macID"

    //UDP Data
    const val UDP_PORT = 2560
    const val UDPHost = "239.255.255.255"

    //TCP Data
    const val TCP_PORT = 9760
}