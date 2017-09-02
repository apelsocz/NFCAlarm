package com.pelsoczi.data

import android.arch.persistence.room.*
import io.reactivex.Flowable


@Dao
interface AlarmDao {

    @Query("SELECT * FROM alarms")
    fun loadAlarmsList(): Flowable<MutableList<Alarm>>

    @Query("SELECT * FROM alarms WHERE id = :id LIMIT 1")
    fun loadAlarm(id: Int): Flowable<Alarm>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAlarms(alarms: MutableList<Alarm>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateAlarms(alarms: MutableList<Alarm>) {
    }

    @Delete
    fun deleteAlarms(alarms: MutableList<Alarm>)
}