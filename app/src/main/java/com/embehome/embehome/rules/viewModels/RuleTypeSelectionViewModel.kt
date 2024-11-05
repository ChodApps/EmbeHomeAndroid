package com.embehome.embehome.rules.viewModels

import android.content.Context
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.embehome.embehome.Model.SceneSwitchDetailModel
import com.embehome.embehome.Utils.AppDialogs
import com.embehome.embehome.Utils.AppServices
import com.embehome.embehome.Utils.CacheHubData
import com.embehome.embehome.rules.RuleCondition
import com.embehome.embehome.rules.RuleSelection
import com.embehome.embehome.rules.model.*
import com.embehome.embehome.rules.utils.RuleDataRepository
import com.embehome.embehome.rules.utils.RuleOperationRepository
import kotlinx.coroutines.launch

class RuleTypeSelectionViewModel : ViewModel() {

    val ruleItemView = "SELECT_ITEM"
    val ruleCreateView = "CREATE_ITEM"

    val ruleName = MutableLiveData<String>()
    val ruleNameId = MutableLiveData<Int> ()
    val condition = MutableLiveData<RuleCondition>()
    private val actionType = MutableLiveData<RuleSelection> ()
    val ruleDetail = MutableLiveData<RuleTypeDetail>()
    val ruleAction = MutableLiveData<RuleDeviceDataDetail> ()

    val ruleConditionDisplay = MutableLiveData<String>()
    val ruleActionDisplay = MutableLiveData<String>()

    val selectImage = MutableLiveData(View.VISIBLE)
    val createRule = MutableLiveData(View.GONE)

    fun updateView (v : String) {
        if (v == ruleCreateView) {
            selectImage.value = View.GONE
            createRule.value = View.VISIBLE
        }
        else if (v == ruleItemView) {
            selectImage.value = View.VISIBLE
            createRule.value = View.GONE
        }
        else {
            ruleExit()
        }
    }


    val performAction = MutableLiveData(false)
    var action = ""
    fun performAction (Data : String) {
        action = Data
        performAction.value = true
        performAction.value = false
    }

    fun createRuleItem () {
        performAction("CreateItem")
    }

    fun popBackStack () {
        performAction("POP")
    }

    fun ruleBack () {
        performAction("Back")
    }

    fun ruleExit () {
        performAction("Exit")
    }

    ///////////////////////////////// Condition

    fun selectName () {
        updateView(ruleItemView)
    }

    fun addCondition () {
        performAction("selectCondition")
    }

    fun addAction () {
        performAction("selectAction")
    }

    fun saveRule () {
        performAction("SaveRule")
    }

    @ExperimentalStdlibApi
    fun saveRule (context : Context) = viewModelScope.launch {

        if (condition.value != null) {
            condition.value?.let { condition ->
                if (ruleDetail.value != null) {
                    ruleDetail.value?.let {type ->
                        if (ruleAction.value != null) {
                            ruleAction.value?.let {data ->
                                if (condition == RuleCondition.EVENT) {
                                    try {
                                        type.sensor?.let {
                                            if (data.device_list.size > 0) {
                                                if (data.device_list[0].thing_id == it.thing_id) {
                                                    AppDialogs.openMessageDialog(context, "You can not add same switch for condition and action for the rule")
                                                }
                                                else {
                                                    val res = RuleOperationRepository.addRuleCounter(
                                                        context,
                                                        CacheHubData.selectedMacID,
                                                        ruleNameId.value ?: 1,
                                                        condition,
                                                        data,
                                                        type
                                                    )
                                                    if (res) ruleExit() else {}
                                                }
                                            }
                                            else {
                                                val res = RuleOperationRepository.addRuleCounter(
                                                    context,
                                                    CacheHubData.selectedMacID,
                                                    ruleNameId.value ?: 1,
                                                    condition,
                                                    data,
                                                    type
                                                )
                                                if (res) ruleExit() else {}
                                            }
                                        }
                                    }
                                    catch (e : Exception) {

                                    }
                                } else if (condition == RuleCondition.COUNTER) {
                                    val rl = RuleDataRepository.getRuleList(CacheHubData.selectedMacID)
                                    if (rl != null && rl.value != null) {
                                        var exist = false
                                        rl.value?.forEach {rule ->
                                            if (rule.rule_type == RuleCondition.COUNTER.name && rule.rule_data.rule_action.device_list.size > 0) {
                                                if (rule.rule_data.rule_action.device_list[0].thing_id == data.device_list[0].thing_id && rule.rule_data.rule_action.device_list[0].switchId == data.device_list[0].switchId) {
                                                    exist = true
                                                }
                                            }
                                        }
                                        if (exist) {
                                            AppDialogs.openMessageDialog(context, "Counter rule already exist for this switch. Please use different switch")
                                        }else {
                                            val res = RuleOperationRepository.addRuleCounter(
                                                context,
                                                CacheHubData.selectedMacID,
                                                ruleNameId.value ?: 1,
                                                condition,
                                                data,
                                                type
                                            )
                                            if (res) ruleExit() else {}
                                        }
                                    }
                                    else {
                                        val res = RuleOperationRepository.addRuleCounter(
                                            context,
                                            CacheHubData.selectedMacID,
                                            ruleNameId.value ?: 1,
                                            condition,
                                            data,
                                            type
                                        )
                                        if (res) ruleExit() else {}
                                    }
                                }
                                else {
                                    val res = RuleOperationRepository.addRuleCounter(
                                        context,
                                        CacheHubData.selectedMacID,
                                        ruleNameId.value ?: 1,
                                        condition,
                                        data,
                                        type
                                    )
                                    if (res) ruleExit() else {}
                                }
                            }
                        }
                        else {
                            AppServices.toastShort(context, "Please add switch or scenes in add action")
                        }
                    }
                }
                else {
                    AppServices.toastShort(context, "Please add condition for the rule")
                }
            }
        }
        else {
            AppServices.toastShort(context, "Please add condition for the rule")
        }
    }
    @ExperimentalStdlibApi
    fun updateRule (context : Context, ruleId : Int) = viewModelScope.launch {

        if (condition.value != null) {
            condition.value?.let { condition ->
                if (ruleDetail.value != null) {
                    ruleDetail.value?.let {type ->
                        if (ruleAction.value != null) {
                            ruleAction.value?.let { data ->
                                if (condition == RuleCondition.EVENT) {
                                    try {
                                        type.sensor?.let {
                                            if (data.device_list.size > 0) {
                                                if (data.device_list[0].thing_id == it.thing_id) {
                                                    AppDialogs.openMessageDialog(
                                                        context,
                                                        "You can not add same switch for condition and action for the rule"
                                                    )
                                                } else {
                                                    val res =
                                                        RuleOperationRepository.updateRuleCounter(
                                                            context,
                                                            CacheHubData.selectedMacID,
                                                            ruleId,
                                                            ruleNameId.value ?: 1,
                                                            condition,
                                                            data,
                                                            type
                                                        )
                                                    if (res) ruleExit() else {
                                                    }
                                                }
                                            } else {
                                                val res = RuleOperationRepository.updateRuleCounter(
                                                    context,
                                                    CacheHubData.selectedMacID,
                                                    ruleId,
                                                    ruleNameId.value ?: 1,
                                                    condition,
                                                    data,
                                                    type
                                                )
                                                if (res) ruleExit() else {
                                                }
                                            }
                                        }
                                    } catch (e: Exception) {

                                    }
                                } else if (condition == RuleCondition.COUNTER) {
                                    val rl = RuleDataRepository.getRuleList(CacheHubData.selectedMacID)
                                    if (rl != null && rl.value != null) {
                                        var exist = false
                                        rl.value?.forEach {rule ->
                                            if (rule.rule_type == RuleCondition.COUNTER.name && rule.rule_data.rule_action.device_list.size > 0) {
                                                if (rule.rule_data.rule_action.device_list[0].thing_id == data.device_list[0].thing_id && rule.rule_data.rule_action.device_list[0].switchId == data.device_list[0].switchId) {
                                                    exist = true
                                                }
                                            }
                                        }
                                        if (exist) {
                                            AppDialogs.openMessageDialog(context, "Counter rule already exist for this switch. Please use different switch")
                                        }else {
                                            val res = RuleOperationRepository.addRuleCounter(
                                                context,
                                                CacheHubData.selectedMacID,
                                                ruleNameId.value ?: 1,
                                                condition,
                                                data,
                                                type
                                            )
                                            if (res) ruleExit() else {}
                                        }
                                    }
                                    else {
                                        val res = RuleOperationRepository.addRuleCounter(
                                            context,
                                            CacheHubData.selectedMacID,
                                            ruleNameId.value ?: 1,
                                            condition,
                                            data,
                                            type
                                        )
                                        if (res) ruleExit() else {}
                                    }
                                }


                                else {
                                    val res = RuleOperationRepository.updateRuleCounter(
                                        context,
                                        CacheHubData.selectedMacID,
                                        ruleId,
                                        ruleNameId.value ?: 1,
                                        condition,
                                        data,
                                        type
                                    )
                                    if (res) ruleExit() else {
                                    }
                                }
                            }
                        }
                        else {
                            AppServices.toastShort(context, "Please add switch or scenes in add action")
                        }
                    }
                } else {
                    AppServices.toastShort(context, "Please add condition for the rule")
                }
            }
        } else {
            AppServices.toastShort(context, "Please add condition for the rule")
        }
    }



    //////////////////////////// Counter
    val counterTime = MutableLiveData<String>()

    fun setConditionCounter () {
        /*counterTime.value?.let {
            if (it.isNotEmpty()) {
                condition.value = RuleCondition.COUNTER
                ruleBack()
            }
        }*/
        performAction("ConditionCounterDone")
    }

    fun setConditionCounter (time : String) {
        counterTime.value = time
        ruleAction.value?.scenes_list?.let {
            if (it.size > 0) {
                ruleAction.value = null
                ruleActionDisplay.value = ""
            }
        }
        ruleConditionDisplay.value = "Counter - ${time.toInt()/60} : ${time.toInt()%60}"
        condition.value = RuleCondition.COUNTER
        ruleDetail.value = RuleTypeDetail(null, null, RuleConditionCounter(time.toInt()))
        ruleAction.value =  null
        ruleActionDisplay.value = ""

    }

    ///////////////////////////// Time
    /*val time = MutableLiveData<Int>(0)
    val timeRepeat = MutableLiveData<Boolean>()
    val weekDays = ArrayList<Int>()
    val sun = MutableLiveData (false)
    val mon = MutableLiveData (false)
    val tue = MutableLiveData (false)
    val wed = MutableLiveData (false)
    val thu = MutableLiveData (false)
    val fri = MutableLiveData (false)
    val sat = MutableLiveData (false)


    fun selectWeekDay (day : Int) {
        if (weekDays.contains(day)) {
            removeWeekDay(day)
        }
        else {
            addWeekDay(day)
        }
    }

    private fun addWeekDay (day : Int) {
        weekDays.add(day).also {
            if (it) {
                when (day) {
                    0 -> sun.value = true
                    1 -> mon.value = true
                    2 -> tue.value = true
                    3 -> wed.value = true
                    4 -> thu.value = true
                    5 -> fri.value = true
                    6 -> sat.value = true
                }
            }
        }
    }

    private fun removeWeekDay (day: Int) {
        weekDays.remove(day).also {
            if (it) {
                when (day) {
                    0 -> sun.value = false
                    1 -> mon.value = false
                    2 -> tue.value = false
                    3 -> wed.value = false
                    4 -> thu.value = false
                    5 -> fri.value = false
                    6 -> sat.value = false
                }
            }
        }
    }
*/
    fun setConditionTime () {
        /*if (time.value != null && time.value!! >= 0){
            AppServices.log("TronX _ Rule", "time - ${time.value}, days - ${weekDays.toList()}, repeat - ${timeRepeat.value} ")
            condition.value = RuleCondition.TIMER.also {
                counterTime.value = null
            }
            ruleBack()
        }*/
        performAction("setConditionTime")
    }

    fun setConditionTime (context : Context, weekDays : ArrayList<Int>, time : Int, repeat : Boolean) {
        condition.value = RuleCondition.TIMER
        var d = ""
        weekDays.forEach{
          d =  when (it) {
               0 -> "$d Sun,"
               1 -> "$d Mon,"
               2 -> "$d Tue,"
               3 -> "$d Wed,"
               4 -> "$d Thu,"
               5 -> "$d Fri,"
               6 -> "$d Sat,"
               else -> d
           }
        }
        ruleConditionDisplay.value = "Timer - ${time / 60} : ${time % 60} -${d.dropLast(1)} "
        ruleDetail.value = RuleTypeDetail (RuleTimer(weekDays, time, repeat), null, null)
    }

    ////////////////////////////////Sensor switches
    /*val conditionDeviceId = MutableLiveData<Int>()
    val conditionSwitchId = MutableLiveData<Int>()
    val conditionSwitchStatus = MutableLiveData<Int>()*/

    fun setConditionSensorSwitch () {
        /*if (conditionSwitchId.value != null) {
            condition.value = RuleCondition.SENSOR_SWITCH.also {
                counterTime.value = null
            }
            ruleBack()
        }*/

        performAction("setConditionSensorSwitch")
    }

    fun setConditionSensorSwitch (context : Context, tid : Int, sno : String, sid : Int, ss : Int, l : String) {
        condition.value = RuleCondition.EVENT
        ruleConditionDisplay.value = l
        ruleDetail.value = RuleTypeDetail(null, RuleSensor(RuleSelection.SWITCHES.name,"=",sid, ss, sno, tid), null)
    }

    /////////////////////////////////////////////////////Action

    ////////////////////////////////Sub scene
    val selectedSceneID = MutableLiveData<ArrayList<Int>> (ArrayList())

    fun actionSubSceneSave () {
        performAction("actionSubSceneSave")
    }

    fun actionSubSceneSave(scene : ArrayList<Int>, l: String) {
        selectedSceneID.value = scene
        ruleActionDisplay.value = l
        ruleAction.value = RuleDeviceDataDetail(scenes_list = scene)
    }


    //////////////////////////////Switches
    val selectedBoard = MutableLiveData<ArrayList<Int>>(ArrayList())
    val selectedSwitch = MutableLiveData<ArrayList<SceneSwitchDetailModel>>(ArrayList())

    fun continueToSwitches () {
        performAction("Continue")
    }

    fun actionSwitchesSave () {
        performAction("actionSwitchesSave")
    }

    fun actionSwitchesSave (context: Context, switches : ArrayList<SceneSwitchDetailModel>, board : ArrayList<Int>, l : String) {
        selectedBoard.value?.addAll(board)
        selectedSwitch.value?.clear()
        selectedSwitch.value?.addAll(switches)
        selectedSwitch.value?.let {
            ruleActionDisplay.value = l
            ruleAction.value = RuleDeviceDataDetail(device_list = it)
        }
    }

}