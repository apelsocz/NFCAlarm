package com.pelsoczi.adam.tapthat.kotlin

import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
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
    private val sharedPreferences = getApplication<Application>().applicationContext
            .getSharedPreferences(PreferencesManager.PREF_NAME, Context.MODE_PRIVATE)

    private var alarmLiveData: LiveData<MutableList<Alarm>>
    private var scheduled = MutableLiveData<Alarm>()

    private var selected = Alarm.EMPTY

    init {
        Log.wtf(NAME, "init{$NAME}")

        alarmRepository = AlarmRepository(application.applicationContext)

        alarmLiveData = alarmRepository.getAlarmsList()
        alarmLiveData.observeForever { alarms ->
            Log.v(NAME, "livedata alarm list observed:")
            if (alarms != null) {
                Log.v(NAME, "$NAME observed ${alarms.size} Alarms")
                alarms.forEach { println("Alarm: ${it.id}, ${it.hour}:${it.minute}") }
                updateScheduled(alarms)
            }
        }
    }

    fun getAlarms() = alarmLiveData

    fun getScheduled() = scheduled

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

    private fun updateScheduled(alarms: MutableList<Alarm>?) {
        println("| updateScheduled()")

        var nextScheduled = Alarm.EMPTY
        var millis = 0L

        val listOfActiveAlarms = alarms?.filter { alarm -> alarm.active }

        if (listOfActiveAlarms != null && listOfActiveAlarms.isNotEmpty()) {
            println("| ${listOfActiveAlarms.size} active alarms")

            var calNext = Calendar.getInstance()
            var calNow = Calendar.getInstance()
            var calModel = Calendar.getInstance()

            calNext.timeInMillis = 0L
            calNow.timeInMillis = System.currentTimeMillis()
            calModel.timeInMillis = System.currentTimeMillis()

            var dateNext: Date = calNext.time
            var dateNow: Date = calNow.time
            var dateModel: Date

            println("| begin for each..")

            listOfActiveAlarms.forEach {
                // reset model Calendar
                calModel.set(Calendar.DAY_OF_YEAR, calNow.get(Calendar.DAY_OF_YEAR))
                calModel.set(Calendar.HOUR_OF_DAY, 0)
                calModel.set(Calendar.MINUTE, 0)
                calModel.set(Calendar.SECOND, 5)

                // recompute Calendar internal fields
                dateModel = calModel.time

                // initialize Calendar and Date to model values
                val hourOfDay = it.hour
                val minute = it.minute
                calModel.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calModel.set(Calendar.MINUTE, minute)
                dateModel = calModel.time

                println("| ready for ${it.toString()}")

                if (it.once) {
                    if (dateModel.before(dateNow)) {
                        calModel.add(Calendar.DAY_OF_YEAR, 1)
                        dateModel = calModel.time
                    }
                } else {
                    // amount of days to increment
                    var i = 0
                    var iterate = true
                    do {
                        // refresh model calendar to today and increment by i
                        calModel.set(Calendar.DAY_OF_YEAR, calNow.get(Calendar.DAY_OF_YEAR))
                        calModel.add(Calendar.DAY_OF_YEAR, i)
                        dateModel = calModel.time

                        if (Data.isDayActive(calModel.get(Calendar.DAY_OF_WEEK), it)) {
                            if (dateModel.after(dateNow)) {
                                // loop until first active model date after now
                                iterate = false
                            }
                        }
                        i++
                    } while (iterate)
                }

                // assign dateNext
                if (dateNext.time == 0L) {
                    // assign knowing that dateModel is more ideal
                    dateNext = dateModel
                    nextScheduled = it
                } else {
                    // assign, if dateModel is after dateNow && before current assignment
                    dateNext = if (dateModel.after(dateNow) && dateModel.before(dateNext))
                        dateModel
                    else
                        dateNext

                    nextScheduled = if (dateNext.compareTo(dateModel) == 0) it else nextScheduled
                }
            }
            millis = dateNext.time
        }
        else {
            println("| nothing to update")
        }

        sharedPreferences.edit()
                .putLong(PreferencesManager.KEY_VALUE_ID, nextScheduled.id)
                .putLong(PreferencesManager.KEY_VALUE_MILLIS, millis)
                .commit()

        scheduled.postValue(nextScheduled)

        println("| updateScheduled() = ${nextScheduled.toString()} at $millis")
    }

    fun getScheduledMillis(): Long {
        return sharedPreferences.getLong(PreferencesManager.KEY_VALUE_MILLIS, 0L)
    }

    fun select(alarm: Alarm) {
        if (alarm == Alarm.EMPTY) {
            selected = Alarm.EMPTY
        } else {
            val index = alarmLiveData.value?.indexOf(alarm)
            if (index != null && index != -1) {
                selected = alarmLiveData.value?.get(index) ?: Alarm.EMPTY
            }
        }
    }

    fun getSelected() = selected

    fun resetSelected() {
        selected = Alarm.EMPTY
    }
}