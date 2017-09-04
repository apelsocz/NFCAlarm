package com.pelsoczi.adam.tapthat.kotlin.ui

import android.app.FragmentManager
import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LifecycleRegistryOwner
import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import com.pelsoczi.adam.tapthat.R
import com.pelsoczi.adam.tapthat.kotlin.Application
import com.pelsoczi.data.Alarm
import kotlinx.android.synthetic.main.activity_application.*

/**
 * The primary activity which controls the user's interactions.
 */
class AlarmActivity : AppCompatActivity(), LifecycleRegistryOwner {

    private val registry = LifecycleRegistry(this)
    override fun getLifecycle() = registry

    val NAME = AlarmActivity::getLocalClassName.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(NAME, "onCreate, savedInstanceState = $savedInstanceState")

        setContentView(R.layout.activity_application)
        setSupportActionBar(app_bar as Toolbar)

        savedInstanceState ?: init()
    }

    private fun init() {
        supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, AlarmsFragment.newInstance(), AlarmsFragment.NAME)
                .commitAllowingStateLoss()
    }

    fun onAlarmClick(alarm: Alarm) {
        Log.d(NAME, alarm.id.toString())

        (application as Application).getViewModel().select(alarm)

        floating_action_btn.hide()
        supportFragmentManager.popBackStackImmediate(EditFragment.NAME,
                FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.fragmentContainer, EditFragment(), EditFragment.NAME)
                .addToBackStack(EditFragment.NAME)
                .commit()
    }

    fun onActiveToggle(alarm: Alarm) {
        val alarmFragment: AlarmsFragment = supportFragmentManager
                .findFragmentByTag(AlarmsFragment.NAME) as AlarmsFragment
        alarmFragment.toggleAlarm(alarm)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // animate the fab back into view
        floating_action_btn.show()
    }
}