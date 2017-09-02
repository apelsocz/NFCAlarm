package com.pelsoczi.adam.tapthat

import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.pelsoczi.data.Alarm
import com.pelsoczi.data.AlarmRepository


class AlarmViewModel(application: MyApplication) : AndroidViewModel(application) {

    private var alarmRepository = AlarmRepository(application.applicationContext)

    private var liveAlarmData: LiveData<List<Alarm>>? = null

    fun alarmsListLiveData(): LiveData<List<Alarm>>? {
        if (liveAlarmData == null) {
            liveAlarmData = alarmRepository.loadAlarmsList()
        }
        return liveAlarmData
    }
}