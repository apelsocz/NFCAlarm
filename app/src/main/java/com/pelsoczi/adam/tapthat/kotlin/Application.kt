package com.pelsoczi.adam.tapthat.kotlin

import android.app.Application

class Application : Application() {

    private lateinit var alarmViewModel: AlarmViewModel

    companion object {
        var isRinging = false
        var isSnoozing = false
    }

    override fun onCreate() {
        super.onCreate()
        alarmViewModel = AlarmViewModel(this)
    }

    fun getViewModel() = alarmViewModel
}