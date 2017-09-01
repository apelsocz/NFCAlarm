package com.pelsoczi.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

@Database(entities = arrayOf(Alarm::class), version = 1)
abstract class AlarmDatabase : RoomDatabase() {

    companion object {
        val DATABASE_NAME = "alarms-db"
    }

    abstract fun alarmDao(): AlarmDao
}