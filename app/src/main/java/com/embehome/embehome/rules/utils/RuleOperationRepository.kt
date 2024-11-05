package com.embehome.embehome.rules.utils

import android.content.Context
import com.embehome.embehome.Manager.HttpManager
import com.embehome.embehome.Manager.LANManager
import com.embehome.embehome.Utils.AppDialogs
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.rules.RuleCondition
import com.embehome.embehome.rules.model.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext


/** com.tronx.tt_things_app.rules.utils
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 24-07-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


object RuleOperationRepository {


    suspend fun fetchRuleItemList () = withContext(Main) {
        val res = HttpManager.getRuleItemList()
        if (res.status) {
            val data = res.body as CloudRuleItemList
            RuleDataRepository.updateRuleItem(data.data)
        }
    }

    suspend fun fetchRules(macId: String) = withContext(Main) {
        val res = HttpManager.getRuleList(macId)
        if (res.status) {
            try {
                RuleDataRepository.deleteAllRule(macId)
                (res.body as CloudRuleDetails).data.forEach {
                    RuleDataRepository.addRule(it)
                }
            }
            catch (e : Exception) {
                AppServices.log("TronX_Rule", "error while requesting the rules : ${e.message}")
            }
        }
    }

    @ExperimentalStdlibApi
    suspend fun addRuleCounter (context : Context,
                                macId : String,
                                ruleNameId : Int,
                                ruleType : RuleCondition,
                                ruleData : RuleDeviceDataDetail,
                                ruleDetail : RuleTypeDetail

    ) = withContext(Main) {
        AppDialogs.startLoadScreen(context)
        val id = RuleDataRepository.generateRuleId(macId)
        val rule = RuleDetails(macId, id, ruleNameId, ruleType.name, RuleDataDetail(ruleData, ruleDetail))
        val result = LANManager.createRule(CacheHubData.selectedHubIP, rule)
        if (result.status) {
            val res = HttpManager.createRule(rule)
            if (res.status) {
                RuleDataRepository.addRule(rule)
                AppDialogs.stopLoadScreen()
                return@withContext true
            }
        }
        else {
            AppServices.toastShort(context, result.data)
        }
        AppDialogs.stopLoadScreen()
        false
    }

    @ExperimentalStdlibApi
    suspend fun updateRuleCounter (context : Context,
                                   macId : String,
                                   oldRuleId : Int,
                                   ruleNameId : Int,
                                   ruleType : RuleCondition,
                                   ruleData : RuleDeviceDataDetail,
                                   ruleDetail : RuleTypeDetail

    ) = withContext(Main) {
        AppDialogs.startLoadScreen(context)
        val id = RuleDataRepository.generateRuleId(macId)
        val rule = RuleUpdateDetails(macId, id, oldRuleId, ruleNameId, ruleType.name, RuleDataDetail(ruleData, ruleDetail))
        val result = LANManager.editRule(CacheHubData.selectedHubIP, rule)
        if (result.status) {
            val res = HttpManager.updateRule(rule)
            if (res.status) {
                RuleDataRepository.updateRule(rule)
                AppDialogs.stopLoadScreen()
                return@withContext true
            }
        }
        else {
            AppServices.toastShort(context, result.data)
        }
        AppDialogs.stopLoadScreen()
        false
    }

    @ExperimentalStdlibApi
    suspend fun deleteRule (macId: String, ruleId : Int) = withContext(Main) {
        val lan = LANManager.deleteRule(CacheHubData.selectedHubIP, ruleId)
        if (lan.status) {
            val res = HttpManager.deleteRule(macId, ruleId)
            if (res.status) {
                RuleDataRepository.deleteRule(macId, ruleId)
                return@withContext true
            }
            else return@withContext false
        }
        false
    }

}