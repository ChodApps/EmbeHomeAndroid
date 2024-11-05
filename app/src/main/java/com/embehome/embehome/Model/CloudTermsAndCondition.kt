package com.embehome.embehome.Model


/** com.tronx.tt_things_app.Model
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 28-07-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


class CloudTermsAndCondition (
    val status : String,
    val message : String,
    val data : TermsAndCondition
)

class TermsAndCondition (
    val terms_data : TermsData
)

class TermsData (
    val latest_version_id : String,
    val terms_url : String
)