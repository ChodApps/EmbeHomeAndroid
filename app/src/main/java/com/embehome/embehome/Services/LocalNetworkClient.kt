package com.embehome.embehome.Services

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.embehome.embehome.Manager.HUBManager
import com.embehome.embehome.Manager.LANManager
import com.embehome.embehome.Model.HubUdpData
import com.embehome.embehome.Model.LanRequestFeedback
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.NetworkHosts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.*


/** com.tronx.tt_things_app.Services
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 28-01-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


@ExperimentalStdlibApi
object LocalNetworkClient {

    private const val ConnectionTimeOut = 1000
    const val operationTcpDelay = 10000
    private const val TCP_PORT = NetworkHosts.TCP_PORT
    private const val PACKET_SIZE = 200
    private var receiveData: ByteArray = ByteArray(PACKET_SIZE)
    private lateinit var socket: DatagramSocket
    private var isGetHub = false
    //  val data  = byteArrayOf(174.toByte(), 234.toByte(), 82, 81, 73, 80, 171.toByte(), 186.toByte())


    suspend fun getHub(data : ByteArray, repeat : Int = 4) : ArrayList<HubUdpData>? = withContext(Dispatchers.IO){

        if (isGetHub)
            return@withContext null.also {
                AppServices.log("TronX", "Previous getHub Request already in progress")
            }

        var count = repeat
        isGetHub = true
        socket = DatagramSocket()
        socket.reuseAddress = true

        val packets = ArrayList<HubUdpData>()
        val receivePacket = DatagramPacket(receiveData, PACKET_SIZE)
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            var host = InetAddress.getByName(NetworkHosts.UDPHost)
            var i = 1

            while (interfaces.hasMoreElements() && (i == 1)) {
                val networkInterface = interfaces.nextElement()
                if (networkInterface.isLoopback)
                    continue
                for (interfaceAddress in networkInterface.interfaceAddresses) {
                    host = interfaceAddress.broadcast ?: continue
                    AppServices.log("TronX", "Broadcast Address is $host")
                    i = 0
                    break
                }
            }

            val packet = DatagramPacket(data, data.size, host, NetworkHosts.UDP_PORT)
            socket.soTimeout = ConnectionTimeOut

            while (true) {
                socket.send(packet)
                count--
                if (count <= 0)
                    break
                try {
                    while (socket.soTimeout > 0) {
                        socket.receive(receivePacket)
                        val temp = HUBManager.hubDataParser(receivePacket)
                        if (temp != null) packets.add(temp)
                        Log.d("TronX", "HUB Found ${receivePacket.address.hostAddress}")
                    }
                }
                catch (e : Exception) {
                    AppServices.log("TronX", "HUB DISCOVERY FAILED ${e.message}")
                    if (count <= 0 || packets.size > 0)
                        break
                }
            }
        } catch (e: Exception) {
            AppServices.log("TronX", "HUB DISCOVERY FAILED ${e.message}")
        } finally {
            socket.close()
            isGetHub = false
        }
        AppServices.log("TronX", "Number HUB of received are ${packets.size} & ${packets.toList()}")

        packets
    }

//    suspend fun addPacket(packets : ArrayList<DatagramPacket>, packet: DatagramPacket) = withContext(Dispatchers.IO){
//        packets.add(packet)
//        packets
//    }


    @ExperimentalStdlibApi
    suspend fun writeOnTcp(IP: String, port: Int, cmd: String, timeOut: Int = 10) =
        withContext(Dispatchers.IO) {
            AppServices.log("TronX_input", cmd)
            var readFromServer: DataInputStream? = null
            var writeOnServer: DataOutputStream? = null
            val result = ByteArray(300)


            try {
                val socket = Socket(IP, port)
                socket.soTimeout = timeOut * 1000
                readFromServer = DataInputStream(BufferedInputStream(socket.getInputStream()))
                writeOnServer = DataOutputStream(BufferedOutputStream(socket.getOutputStream()))
                writeOnServer.write(cmd.toByteArray())
                writeOnServer.flush()
                readFromServer.read(result)
                AppServices.log("TronX", "Data read from TCP is ${result.decodeToString()}")


            } catch (e: Exception) {
                AppServices.log("TronX", "TCP Failed ${e.message}")
            } finally {
                readFromServer?.close()
                writeOnServer?.close()
            }

        // result.decodeToString(1, (result.indexOf('}'.toByte())))
        LANManager.byteArrayToString(result)
    }


    suspend fun writeOnTcpTemp(IP: String, port: Int, cmd: String) = withContext(Dispatchers.IO) {
        AppServices.log("TronX_input", cmd)
        var readFromServer: DataInputStream? = null
        var writeOnServer: DataOutputStream? = null
        val result = ByteArray(300)
        val socket: Socket
        try {
            socket = Socket(IP, port)

            readFromServer = DataInputStream(BufferedInputStream(socket.getInputStream()))
            writeOnServer = DataOutputStream(BufferedOutputStream(socket.getOutputStream()))
            writeOnServer.write(cmd.toByteArray())
            writeOnServer.flush()
            //  readFromServer.readFully(result)
            readFromServer.read(result)
            AppServices.log("TronX", "Data read from TCP is ${result.decodeToString()}")
        } catch (e: Exception) {
            AppServices.log("TronX", "TCP Failed ${e.message}")
        } finally {
            readFromServer?.close()
            writeOnServer?.close()
        }
        // result.decodeToString(1, (result.indexOf('}'.toByte())))
        LANManager.byteArrayToString(result)
    }

    suspend fun writeOnTcpToPerformOperation(IP: String, cmd: String) =
        withContext(Dispatchers.IO) {
            AppServices.log("TronX_input", cmd)
            var readFromServer: DataInputStream? = null
            var writeOnServer: DataOutputStream? = null
            val result = ByteArray(300)
            Log.d("TronX", cmd)
            try {
                val socket = Socket(IP, TCP_PORT)
                socket.soTimeout = operationTcpDelay
                readFromServer = DataInputStream(BufferedInputStream(socket.getInputStream()))
                writeOnServer = DataOutputStream(BufferedOutputStream(socket.getOutputStream()))
                writeOnServer.write(cmd.toByteArray())
                writeOnServer.flush()
                readFromServer.read(result)
            } catch (e: Exception) {
                AppServices.log("TronX", "TCP Failed ${e.message}  ip = $IP")
            } finally {
                readFromServer?.close()
                writeOnServer?.close()
            }
            LANManager.byteArrayToString(result)
        }

    suspend fun readIRValue(IP: String, cmd: String) = withContext(Dispatchers.IO) {
        AppServices.log("TronX_input", cmd)
        var readFromServer: DataInputStream? = null
        var writeOnServer: DataOutputStream? = null
        val result = ByteArray(1024)
        var packetSize = 0

        try {
            val socket = Socket(IP, TCP_PORT)
            socket.soTimeout = operationTcpDelay
            readFromServer = DataInputStream(BufferedInputStream(socket.getInputStream()))
            writeOnServer = DataOutputStream(BufferedOutputStream(socket.getOutputStream()))
            writeOnServer.write(cmd.toByteArray())
            writeOnServer.flush()
            packetSize = readFromServer.read(result)
        } catch (e: Exception) {
            AppServices.log("TronX", "TCP Failed ${e.message}  ip = $IP")
        } finally {
            readFromServer?.close()
            writeOnServer?.close()
            socket.close()
        }
        val packet = result.dropLast(1024 - packetSize).toByteArray().also {
            AppServices.log("TronX _ success", it.decodeToString())
        }
        LANManager.byteArrayToString(packet)
    }

    suspend fun writeOnTCPWithOutResult(IP: String, cmd: String): LanRequestFeedback =
        withContext(Dispatchers.IO) {
            AppServices.log("TronX_IRB","Sending - $cmd")
            var writeOnServer: DataOutputStream? = null

            try {
                val socket = Socket(IP, TCP_PORT)
                writeOnServer = DataOutputStream(BufferedOutputStream(socket.getOutputStream()))
                writeOnServer.write(cmd.toByteArray())
                writeOnServer.flush()
            } catch (e: Exception) {
                Log.d("TronX", "TCP Failed ${e.message}  ip = $IP")
                return@withContext LanRequestFeedback(false, e.message.toString())
            } finally {
                writeOnServer?.close()
                socket.close()
            }
            LanRequestFeedback(true, "Data sent Successful")
        }

    suspend fun otaTcpHandler(ip: String, cmd: String, size : Int, progress: MutableLiveData<Int>) = withContext(IO) {
            AppServices.log("TronX_input", cmd)
            var readFromServer: DataInputStream? = null
            var writeOnServer: DataOutputStream? = null
            var result = ByteArray(512)
            Log.d("TronX", cmd)
            try {
                val socket = Socket(ip, TCP_PORT)
                readFromServer = DataInputStream(BufferedInputStream(socket.getInputStream()))
                writeOnServer = DataOutputStream(BufferedOutputStream(socket.getOutputStream()))
                writeOnServer.write(cmd.toByteArray())
                writeOnServer.flush()
                while (true) {
                    readFromServer.read(result)
                    val res = parseOtaResponse(result.clone())
                    result = ByteArray(512)
                    AppServices.log("TronX_OTA",res.data)

                    if (res.status) {
                        when (res.data) {
                            "Completed" -> {
                                readFromServer.close()
                                writeOnServer.close()
                                LocalNetworkClient.socket.close()
                                return@withContext LanRequestFeedback(true, "Installing the update please make sure hub stay powered through out the process")
                            }
                            "Corrupt data", "Found Data but failed" -> {

                            }

                            "Failed" -> {
                                readFromServer.close()
                                writeOnServer.close()
                                LocalNetworkClient.socket.close()
                                return@withContext LanRequestFeedback(false, "Hub Update failed. please restart the hub and retry")
                            }

                            else -> {
                                val s = Integer.parseInt(res.data)
                                val p : Int = s * 100 / size
                                if (progress.value ?: -1 < p) {
                                    GlobalScope.launch (Main) {
                                        progress.value = p
                                    }
                                }
                            }

                        }
                    }
                    else {
                        break
                    }
                    //AppServices.log(result.decodeToString())
                }
            } catch (e: Exception) {
                AppServices.log("TronX", "TCP Failed ${e.message}  ip = $ip")
            } finally {
                readFromServer?.close()
                writeOnServer?.close()
                socket.close()
            }
        return@withContext if (progress.value ?: 0 != 100)
            LanRequestFeedback(false, "Not Completed")
        else
            LanRequestFeedback(true, "Completed")
        }

    private suspend fun parseOtaResponse(data: ByteArray): LanRequestFeedback {
        try {

            AppServices.log(data.decodeToString())

            val lastOpen = data.lastIndexOf('{'.toByte())
            val lastClose = data.lastIndexOf('}'.toByte())


            if (lastOpen < lastClose) {
                val response = data.decodeToString(lastOpen + 1, lastClose)
                AppServices.log("TronX_OTA", response)
                val res = LANManager.getTcpData(response)
                if (res.size > 1) {
                    when (res[0]) {
                        "ACAB" -> {
                            if (res[1] != "0")
                                return LanRequestFeedback (false, "Hub Not Responding")
                        }

                        "ACAC" -> {
                            return LanRequestFeedback (true, res[1])
                        }

                        "ACAD" -> {
                            if (res[1] == "0")
                            return LanRequestFeedback(true, "Completed")
                            else
                                return LanRequestFeedback(true, "Failed")
                        }
                    }
                }

            } else {
                return LanRequestFeedback(true, "Corrupt data")
            }

        } catch (e: Exception) {
            AppServices.log(e.message.toString())
        }
        return LanRequestFeedback(true, "Found Data but failed")
    }

}