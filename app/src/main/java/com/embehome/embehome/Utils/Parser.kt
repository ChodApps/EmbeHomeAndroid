package com.embehome.embehome.Utils


/** com.tronx.tt_things_app.Utils
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 29-01-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


@ExperimentalStdlibApi
object Parser {

    fun getHubIP (value : ByteArray) = if (value.size == 4) "${hexToDec(toHex(value[0]))}.${hexToDec(toHex(value[1]))}.${hexToDec(toHex(value[2]))}.${hexToDec(toHex(value[3]))}" else "Error IndexOutOfBound"

    private fun toHex(value : Byte) = String.format("%02X",value)

    private fun hexToDec(value : String) = Integer.parseInt(value,16)

    fun getHubVersion(value : ByteArray) = if (value.size == 2) "${hexToDec(toHex(value[1]))}.${if (hexToDec(toHex(value[0])).toString().length == 1) "0${hexToDec(toHex(value[0]))}"  else "${hexToDec(toHex(value[0]))}"}" else "Error IndexOutOfBound"

    fun getHubMac(value : ByteArray) = if (value.size == 17)"${value[0].toChar()}${value[1].toChar()}${value[2].toChar()}${value[3].toChar()}${value[4].toChar()}${value[5].toChar()}${value[6].toChar()}${value[7].toChar()}${value[8].toChar()}${value[9]
        .toChar()}${value[10].toChar()}${value[11].toChar()}${value[12].toChar()}${value[13].toChar()}${value[14].toChar()}${value[15].toChar()}${value[16].toChar()}" else "Error IndexOutOfBound"



}