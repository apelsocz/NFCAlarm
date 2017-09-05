package com.pelsoczi.adam.tapthat.kotlin

import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.util.Log
import com.pelsoczi.data.Alarm
import com.pelsoczi.data.AlarmRepository

class AlarmViewModel(application: Application) : AndroidViewModel(application) {

    val NAME = "AlarmViewModel"

    private val alarmRepository: AlarmRepository
    private var alarmLiveData: LiveData<MutableList<Alarm>>

    private var selected = Alarm.EMPTY

    init {
        Log.wtf(NAME, "init{$NAME}")

        alarmRepository = AlarmRepository(application.applicationContext)
        alarmLiveData = alarmRepository.getAlarmsList()

        alarmLiveData.observeForever { alarms ->
            if (alarms != null && alarms.size > 0) {
                Log.v(NAME, "alarmLiveData.observe { ${alarmLiveData.value?.size} }")

//                updateNext()
            }
        }
    }

    fun getAlarms() = alarmLiveData

    fun updateAlarm(alarm: Alarm) {
        if (alarm != Alarm.EMPTY) {
            alarmRepository.insertAlarms(mutableListOf(alarm))
        }
        resetSelected()
    }

    fun deleteAlarm(alarm: Alarm) {
        if (alarm != Alarm.EMPTY) {
            Log.d(NAME, "Delete Alarm ${alarm.id}")
            alarmRepository.deleteAlarms(mutableListOf(alarm))
        }
        resetSelected()
    }

    fun select(alarm: Alarm) {
        if (alarm == Alarm.EMPTY) {
            selected = Alarm.EMPTY
        }
        else {
            val index = alarmLiveData.value?.indexOf(alarm)
            if (index != null && index != -1) {
                selected = alarmLiveData.value?.get(index) ?: Alarm.EMPTY
            }
        }
    }

    fun getSelected() = selected

    fun resetSelected() { selected = Alarm.EMPTY }
}