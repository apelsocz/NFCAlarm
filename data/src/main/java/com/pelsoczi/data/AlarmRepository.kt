package com.pelsoczi.data

import android.app.Application
import android.arch.lifecycle.LiveData
import android.content.Context
import android.util.Log

class AlarmRepository(context: Context) {

    val NAME = "AlarmRepository"

    private val database = AlarmDatabase.instance(context as Application)

    init {
        Log.wtf(NAME, "init{$NAME} : $context")
    }

    fun getAlarmsList(): LiveData<MutableList<Alarm>> {
        val list = database.alarmDao().loadAlarmsList()
        Log.v(NAME, ".getAlarmsList() | return size = ${list.value?.size}")
        return list
    }

    fun insertAlarms(alarms: MutableList<Alarm>) {
        AppExecutors.DISK().execute {
            database.alarmDao().insertAlarms(alarms)
        }
    }

    fun updateAlarms(alarms: MutableList<Alarm>) {
        AppExecutors.DISK().execute {
            database.alarmDao().insertAlarms(alarms)
        }
    }

    fun deleteAlarms(alarms: MutableList<Alarm>) {
        AppExecutors.DISK().execute {
            Log.d(NAME, "Delete ${alarms.toString()}")
            database.alarmDao().deleteAlarms(alarms)
        }
    }
}