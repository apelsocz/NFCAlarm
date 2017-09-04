package com.pelsoczi.adam.tapthat.kotlin

import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.util.Log
import com.pelsoczi.data.Alarm
import com.pelsoczi.data.AlarmRepository


class AlarmViewModel(application: Application) : AndroidViewModel(application) {

    val NAME = AlarmViewModel::class.java.simpleName ?: "AlarmViewModel"

    private var alarmRepository = AlarmRepository(application.applicationContext)

    private var alarmLiveData: LiveData<List<Alarm>>
    private var selected = Alarm.EMPTY

    init {
        alarmLiveData = alarmRepository.loadAlarmsList()
        alarmLiveData.observeForever { alarmList: List<Alarm>? ->
            alarmList?.let {
                val total = it.size
                if (total > 0) {
                    alarmRepository.updateAlarms(it)
                    // schedule service for next alarm intent delivery
                    Log.d(NAME, "Updated $total LiveData")
                }
            }
        }
    }

    fun alarmsLiveData() = alarmLiveData

    fun select(alarm: Alarm) {
        val index = alarmLiveData.value?.indexOf(alarm)
        if (index != null && index != -1) {
            selected = alarmLiveData.value?.get(index) ?: Alarm.EMPTY
        }
    }

    fun getSelected() = selected

    fun updateAlarms(alarms: List<Alarm>) = alarmRepository.updateAlarms(alarms)
}