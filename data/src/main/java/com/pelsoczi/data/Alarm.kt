package com.pelsoczi.data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.util.Log


@Entity(tableName = "alarms")
data class Alarm @Ignore constructor(
        @PrimaryKey var id: Long,
        var hour: Int,
        var minute: Int,
        var active: Boolean,
        var once: Boolean,
        var sunday: Boolean,
        var monday: Boolean,
        var tuesday: Boolean,
        var wednesday: Boolean,
        var thursday: Boolean,
        var friday: Boolean,
        var saturday: Boolean) {

    companion object {
        val NAME = "Alarm"
        val EMPTY = Alarm(0L)
    }

    constructor(id: Long) : this(id, hour = 0, minute = 0, active = false, once = true, sunday = false,
            monday = false, tuesday = false, wednesday = false, thursday = false, friday = false,
            saturday = false)

    init {
        Log.wtf(NAME, "init{${toString()}}")
    }

    override fun toString(): String {
        return "<Alarm> id=${id}, time=$hour:$minute"
    }
}