package com.pelsoczi.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import android.util.Log
import com.pelsoczi.data.AlarmDatabase.Companion.DATABASE_VERSION


@Database(entities = arrayOf(Alarm::class), version = DATABASE_VERSION)
abstract class AlarmDatabase : RoomDatabase() {

    companion object {
        const val NAME = "AlarmDatabase"
        const val DATABASE_NAME = "alarms-db"
        const val DATABASE_VERSION = 2

        fun instance(context: Context): AlarmDatabase = Room.databaseBuilder(
                context.applicationContext, AlarmDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build()
                .also { Log.w(NAME, "instance() returned [$DATABASE_NAME|v.$DATABASE_VERSION]") }
    }

    init {
        Log.wtf(NAME, "init{$NAME}")
    }

    abstract fun alarmDao(): AlarmDao
}