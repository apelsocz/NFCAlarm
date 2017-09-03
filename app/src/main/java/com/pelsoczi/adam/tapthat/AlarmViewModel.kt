package com.pelsoczi.adam.tapthat

import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.util.Log
import com.pelsoczi.data.Alarm
import com.pelsoczi.data.AlarmRepository


class AlarmViewModel(application: MyApplication) : AndroidViewModel(application) {

    val NAME = AlarmViewModel::class.java.simpleName ?: "AlarmViewModel"

    private var alarmRepository = AlarmRepository(application.applicationContext)

    private lateinit var liveAlarmData: LiveData<List<Alarm>>
    fun getliveAlarmData() = liveAlarmData

    init {
        liveAlarmData = alarmRepository.loadAlarmsList()
        liveAlarmData.observeForever { alarmList: List<Alarm>? ->
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

    fun populateRoom(alarmList: List<Alarm>) {
        alarmRepository.updateAlarms(alarmList)
    }

    fun alarmsListLiveData(): LiveData<List<Alarm>>? {
        return liveAlarmData
    }

    fun updateAlarms(alarms: List<Alarm>) {
        alarmRepository.updateAlarms(alarms.toMutableList())
    }

    fun deleteAlarms(alarms: List<Alarm>) {
        alarmRepository.deleteAlarms(alarms.toMutableList())
    }
}