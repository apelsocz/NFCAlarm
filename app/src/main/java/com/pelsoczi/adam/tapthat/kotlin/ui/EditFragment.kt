package com.pelsoczi.adam.tapthat.kotlin.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import android.widget.TimePicker
import com.pelsoczi.adam.tapthat.R
import com.pelsoczi.adam.tapthat.kotlin.Application
import com.pelsoczi.data.Alarm
import kotlinx.android.synthetic.main.edit.*
import java.util.*


class EditFragment : Fragment(), View.OnClickListener {
    companion object {
        val NAME = AlarmsFragment::class.java.simpleName
    }

    init {
        println("init{${EditFragment}}")
    }

    private lateinit var alarm: Alarm
    private var cachedId: Long? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return LayoutInflater.from(container?.context).inflate(R.layout.edit, container,
                false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        edit_picker.descendantFocusability = TimePicker.FOCUS_BLOCK_DESCENDANTS
        edit_active.setOnClickListener(this)
        edit_repeat_once.setOnClickListener(this)
        edit_repeat_sun.setOnClickListener(this)
        edit_repeat_mon.setOnClickListener(this)
        edit_repeat_tue.setOnClickListener(this)
        edit_repeat_wed.setOnClickListener(this)
        edit_repeat_thu.setOnClickListener(this)
        edit_repeat_fri.setOnClickListener(this)
        edit_repeat_sat.setOnClickListener(this)

        if (savedInstanceState == null) {
            alarm = (activity.application as Application).getViewModel().getSelected()
            if (alarm == Alarm.EMPTY) {
                cachedId = System.currentTimeMillis()
            }
            else {
                cachedId = alarm.id
            }
        }
        else {
            cachedId = savedInstanceState.getLong("id")
        }
        
        updateUi()
    }
    
    private fun updateUi() {
        if (alarm != Alarm.EMPTY) {
            edit_active.setChecked(alarm.active)
            edit_picker.setHour(alarm.hour)
            edit_picker.setMinute(alarm.minute)
            edit_repeat_once.setChecked(alarm.once)
            edit_repeat_sun.setChecked(alarm.sunday)
            edit_repeat_mon.setChecked(alarm.monday)
            edit_repeat_tue.setChecked(alarm.tuesday)
            edit_repeat_wed.setChecked(alarm.wednesday)
            edit_repeat_thu.setChecked(alarm.thursday)
            edit_repeat_fri.setChecked(alarm.friday)
            edit_repeat_sat.setChecked(alarm.saturday)
        } else {
            edit_active.isChecked = false
            edit_picker.setHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
            edit_picker.setMinute(Calendar.getInstance().get(Calendar.MINUTE))
            edit_repeat_once.isChecked = true
            edit_repeat_sun.isChecked = false
            edit_repeat_mon.isChecked = false
            edit_repeat_tue.isChecked = false
            edit_repeat_wed.isChecked = false
            edit_repeat_thu.isChecked = false
            edit_repeat_fri.isChecked = false
            edit_repeat_sat.isChecked = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit, menu)

        // hide settings menu item
        val settings = menu.findItem(R.id.action_settings)
        if (settings != null) {
            settings.isVisible = false
        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val model = toAlarmModel()
        
        when(item.itemId) {
            R.id.save -> {
                (activity as AlarmActivity).onEditUpdate(model)
            }
            R.id.delete -> {
                (activity as AlarmActivity).onEditDelete(alarm)
            }
        }

        return true
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        // todo implement this without saved instance state - using viewModel
        alarm = toAlarmModel()
        outState?.putLong("id", cachedId ?: 0L)
        super.onSaveInstanceState(outState)
    }
    
    private fun toAlarmModel() = Alarm(cachedId ?: 0,
            edit_picker.hour,
            edit_picker.minute,
            edit_active.isChecked,
            edit_repeat_once.isChecked,
            edit_repeat_sun.isChecked,
            edit_repeat_mon.isChecked,
            edit_repeat_tue.isChecked,
            edit_repeat_wed.isChecked,
            edit_repeat_thu.isChecked,
            edit_repeat_fri.isChecked,
            edit_repeat_sat.isChecked
    )

    override fun onClick(view: View) {
        if (view == edit_repeat_once) {
            if (edit_repeat_once.isChecked) {
                edit_repeat_sun.isChecked = false
                edit_repeat_mon.isChecked = false
                edit_repeat_tue.isChecked = false
                edit_repeat_wed.isChecked = false
                edit_repeat_thu.isChecked = false
                edit_repeat_fri.isChecked = false
                edit_repeat_sat.isChecked = false
            } else {
                edit_repeat_once.isChecked = true
            }
        } else if (view == edit_repeat_sun || view == edit_repeat_mon || view == edit_repeat_tue ||
                view == edit_repeat_wed || view == edit_repeat_thu || view == edit_repeat_fri ||
                view == edit_repeat_sat) {
            if (edit_repeat_once.isChecked) {
                edit_repeat_once.isChecked = false
            } else if (!edit_repeat_sun.isChecked && !edit_repeat_mon.isChecked && 
                    !edit_repeat_tue.isChecked && !edit_repeat_wed.isChecked && 
                    !edit_repeat_thu.isChecked && !edit_repeat_fri.isChecked &&
                    !edit_repeat_sat.isChecked) {
                edit_repeat_once.isChecked = true
            }
        }
    }

}