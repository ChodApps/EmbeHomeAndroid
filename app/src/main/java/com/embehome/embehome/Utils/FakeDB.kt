package com.embehome.embehome.Utils

import com.embehome.embehome.Model.AreaData
import com.embehome.embehome.Model.BoardDetails
import com.embehome.embehome.Model.HubUdpData


/** com.tronx.tt_things_app
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 07-02-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/

object FakeDB {

    var HubIp = ""
    var ApplicationToken = ""
    var message = "NA"
    var AuthToken = "NA"
    var macID = "00:00:00:00:00:00"
    var result = false
    var email = ""
    var hubInLAN = false
    var hubData = ArrayList<HubUdpData>()
    var currentHubData : HubUdpData = HubUdpData("",0,"0.0","","")
    lateinit var packet : HubUdpData
    lateinit var areaData : ArrayList<AreaData>
    var deviceID = 0
    lateinit var hubBoardData : HashMap<Int, ArrayList<BoardDetails>>

    var pid = -1
    var sid = -1

}