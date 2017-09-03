package com.pelsoczi.data

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class AlarmRepository(context: Context) {

    @Volatile var alarmDatabase = AlarmDatabase.instance(context as Application)

    fun loadAlarmsList(): LiveData<List<Alarm>> {
        val mutableLiveData = MutableLiveData<List<Alarm>>()
        alarmDatabase.alarmDao().loadAlarmsList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { alarmList ->
                    mutableLiveData.value = alarmList
                }
        return mutableLiveData
    }

    /** should return the amount updated */
    fun updateAlarms(alarms: List<Alarm>) {
        alarmDatabase.alarmDao().updateAlarms(alarms.toMutableList())
    }

    /** should return true false */
    fun deleteAlarms(alarms: List<Alarm>) {
        alarmDatabase.alarmDao().deleteAlarms(alarms.toMutableList())
    }
}