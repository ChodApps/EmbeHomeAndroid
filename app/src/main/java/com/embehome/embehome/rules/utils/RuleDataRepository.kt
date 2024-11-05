package com.embehome.embehome.rules.utils

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.CacheSceneTwoWay
import com.embehome.embehome.rules.model.RuleDetails
import com.embehome.embehome.rules.model.RuleItemDetails
import com.embehome.embehome.rules.model.RuleUpdateDetails
import kotlin.random.Random


/** com.tronx.tt_things_app.rules.utils
 *   TT_THINGS_APP
 *
 *   Created by Aasim Kutub on 24-07-2020.
 *   Copyright © 2019 tronX things. All rights reserved.
 **/


object RuleDataRepository {

    private val ruleData = MutableLiveData<HashMap<String,MutableLiveData<ArrayList<RuleDetails>>>>(HashMap())
    private val ruleItem = ArrayList<RuleItemDetails>()


    fun deleteAll () {
        ruleData.value?.clear()
    }

    fun addRuleItem (id : Int, name : String, img : String, image : Bitmap?) {
        val  o = RuleItemDetails (id, name, img, false, image)
        ruleItem.add(o)
    }

    fun getRuleItem (id : Int): RuleItemDetails {
        ruleItem.forEach {
            if (it.rulename_id == id)
                return it
        }
        return RuleItemDetails(0,"","",false)
    }

    fun getRuleItemList () : ArrayList<RuleItemDetails> {
        return ruleItem
    }

    fun updateRuleItem (data : ArrayList<RuleItemDetails>) {
        ruleItem.clear()
        ruleItem.addAll(data)
    }

    fun getRule (macId: String, ruleId: Int) : RuleDetails? {
        getRuleList(macId)?.value?.let {
            it.forEach {
                if (it.rule_id == ruleId)
                    return it
            }
        }
        return null
    }

    fun getRuleList (macId: String): MutableLiveData<ArrayList<RuleDetails>>? {
        if (ruleData.value?.get(macId) == null) {
            val t: MutableLiveData<ArrayList<RuleDetails>> = MutableLiveData(ArrayList())
            ruleData.value?.set(macId, t)
            return t
        }
        return ruleData.value?.get(macId)
    }

    fun addRule (rule : RuleDetails) {
        try {
            if (ruleData.value?.get(rule.macID) == null)
                ruleData.value?.set(rule.macID, MutableLiveData(ArrayList()))

            ruleData.value?.get(rule.macID)?.value?.let {
                it.filter { it.rule_id == rule.rule_id }.also { res ->
                    if (res.isEmpty())  {
                        it.add(rule)
                        ruleData.value?.get(rule.macID)?.value = ruleData.value?.get(rule.macID)?.value
                    }
                }
            }
        }
        catch (e : Exception) {
            AppServices.log("TronX _ Rules", "Rule add - ${e.message}")
        }
    }

    fun updateRule (rule : RuleUpdateDetails) {
        try {
            val r = RuleDetails(
                rule.macID,
                rule.rule_id_new,
                rule.rulename_id,
                rule.rule_type,
                rule.rule_data
            )
            deleteRule(rule.macID, rule.rule_id_old)
            addRule(r)
        }
        catch (e : Exception) {
            AppServices.log("TronX _ Rules", "Rule update - ${e.message}")
        }

    }

    fun deleteAllRule (macId: String) {
        ruleData.value?.get(macId)?.value?.clear()
    }

    fun deleteRule (macId: String, ruleId : Int) {
        try {
            ruleData.value?.get(macId)?.value?.let {
                it.filter { it.rule_id == ruleId }.also { res ->
                    it.removeAll(res)
                    ruleData.value?.get(macId)?.value = ruleData.value?.get(macId)?.value
                }
            }
        }
        catch (e : Exception) {
            AppServices.log("TronX _ Rules", "Rule delete - ${e.message}")
        }
    }

    fun generateRuleId (macId : String) : Int {
        val ids = CacheSceneTwoWay.getIDs(99)
        val rules = getRuleList(macId)
        return Random.nextInt(1, 60000)
        rules?.let {
            it.value?.let {
                it.forEach {
                    if (ids.contains(it.rule_id)) {
                        ids[ids.indexOf(it.rule_id)] = 9999
                    }
                }
                return ids.min() ?: 1
            }
        }
        return 1
    }
}