package com.pelsoczi.data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey


@Entity(tableName = "alarms")
data class Alarm @Ignore constructor(
        @PrimaryKey var id: Long,
        var hour: Int,
        var minute: Int,
        var next: Long,
        var active: Boolean,
        var repeats: Boolean,
        var sunday: Boolean,
        var monday: Boolean,
        var tuesday: Boolean,
        var wednesday: Boolean,
        var thursday: Boolean,
        var friday: Boolean,
        var saturday: Boolean) {

    companion object {
        val NAME = "Alarm"
        val EMPTY = Alarm()
    }

    constructor() : this(System.currentTimeMillis(), 0, 0, 0L, false, false,
            //sun  mon    tue    wed    thur   fri    sat
            false, false, false, false, false, false, false)

    init {
        println("$NAME: $id")
    }
}