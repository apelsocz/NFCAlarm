package com.pelsoczi.data

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*


@Dao
interface AlarmDao {

    @Query("SELECT * FROM alarms")
    fun loadAlarmsList(): LiveData<MutableList<Alarm>>

    @Query("SELECT * FROM alarms WHERE id = :id LIMIT 1")
    fun loadAlarm(id: Long): LiveData<Alarm>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAlarms(alarms: MutableList<Alarm>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateAlarms(alarms: MutableList<Alarm>): Int

    @Delete
    fun deleteAlarms(alarms: MutableList<Alarm>)
}