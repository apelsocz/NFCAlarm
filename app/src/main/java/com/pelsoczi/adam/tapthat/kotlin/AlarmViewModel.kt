package com.pelsoczi.adam.tapthat.kotlin

import android.annotation.SuppressLint
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.content.Context
import android.util.Log
import com.pelsoczi.adam.tapthat.data.PreferencesManager
import com.pelsoczi.adam.tapthat.util.Data
import com.pelsoczi.data.Alarm
import com.pelsoczi.data.AlarmRepository
import java.util.*

class AlarmViewModel(application: Application) : AndroidViewModel(application) {

    val NAME = "AlarmViewModel"

    private val alarmRepository: AlarmRepository
    private var alarmLiveData: LiveData<MutableList<Alarm>>

    private var scheduled = Alarm.EMPTY
    private var selected = Alarm.EMPTY

    init {
        Log.wtf(NAME, "init{$NAME}")

        alarmRepository = AlarmRepository(application.applicationContext)
        alarmLiveData = alarmRepository.getAlarmsList()

        alarmLiveData.observeForever { alarms ->
            if (alarms != null && alarms.size > 0) {
                Log.v(NAME, "$NAME observed ${alarmLiveData.value?.size} alarms")
                updateScheduled(alarms)
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

    fun toggleAlarm(alarm: Alarm) {
        val toggledAlarm = alarm
        toggledAlarm.active = !alarm.active

        updateAlarm(toggledAlarm)
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

    private fun updateScheduled(alarms: MutableList<Alarm>?) {
        var idNext = Alarm.EMPTY.id

        if (alarms != null) {
            alarms.forEach { alarm: Alarm ->
                if (!alarm.active) alarms.remove(alarm)
            }
            if (alarms.isNotEmpty()) {
                var calNext = Calendar.getInstance()
                var calNow = Calendar.getInstance()
                var calModel = Calendar.getInstance()

                calNext.timeInMillis = 0L
                calNow.timeInMillis = System.currentTimeMillis()
                calModel.timeInMillis = System.currentTimeMillis()

                var dateNext: Date = calNext.time
                var dateNow: Date = calNow.time
                var dateModel: Date

                alarms.forEach { activeAlarm: Alarm ->
                    // reset model Calendar
                    calModel.set(Calendar.DAY_OF_YEAR, calNow.get(Calendar.DAY_OF_YEAR))
                    calModel.set(Calendar.HOUR_OF_DAY, 0)
                    calModel.set(Calendar.MINUTE, 0)
                    calModel.set(Calendar.SECOND, 5)

                    // recompute Calendar internal fields
                    dateModel = calModel.time

                    // initialize Calendar and Date to model values
                    val hourOfDay = activeAlarm.hour
                    val minute = activeAlarm.minute
                    calModel.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calModel.set(Calendar.MINUTE, minute)
                    dateModel = calModel.time

                    if (activeAlarm.once) {
                        if (dateModel.before(dateNow)) {
                            calModel.add(Calendar.DAY_OF_YEAR, 1)
                            dateModel = calModel.time
                        }
                    }
                    else {
                        // amount of days to increment
                        var i = 0
                        var iterate = true

                        do {
                            // refresh model calendar to today and increment by i
                            calModel.set(Calendar.DAY_OF_YEAR, calNow.get(Calendar.DAY_OF_YEAR))
                            calModel.add(Calendar.DAY_OF_YEAR, i)
                            dateModel = calModel.time

                            if (Data.isDayActive(calModel.get(Calendar.DAY_OF_WEEK), activeAlarm)) {
                                if (dateModel.after(dateNow)) {
                                    // loop until first active model date after now
                                    iterate = false
                                }
                            }
                            i++
                        }
                        while (iterate)
                    }

                    // assign dateNext
                    if (dateNext.time == 0L) {
                        // assign knowing that dateModel is more ideal
                        dateNext = dateModel
                        idNext = activeAlarm.id
                    }
                    else {
                        // assign, if dateModel is after dateNow && before current assignment
                        dateNext = if (dateModel.after(dateNow) && dateModel.before(dateNext))
                            dateModel
                        else
                            dateNext

                        idNext = if (dateNext.compareTo(dateModel) == 0) activeAlarm.id else idNext
                    }
                }
//                setScheduled(idNext, dateNext.time)
            }
            else {
                // alarms is empty - write Alarm.EMPTY for next scheduled
//                setScheduled(Alarm.EMPTY.id, 0L)
            }
        }
    }

    @SuppressLint("ApplySharedPref")
    private fun setScheduled(idOfNext: Long, timeInMillis: Long) {
        val context = (getApplication<Application>() as Application).applicationContext

        val result = context.getSharedPreferences(PreferencesManager.PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .putLong(PreferencesManager.KEY_VALUE_ID, idOfNext)
                .putLong(PreferencesManager.KEY_VALUE_MILLIS, timeInMillis)
                .commit()

        scheduled = alarmRepository.getAlarmById(idOfNext)
        // todo scheduling retrieve of next scheduled alarm
    }


}