package com.pelsoczi.adam.tapthat.kotlin.ui

import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LifecycleRegistryOwner
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.pelsoczi.adam.tapthat.R
import com.pelsoczi.data.Alarm
import kotlinx.android.synthetic.main.activity_application.*

/**
 * The primary activity which controls the user's interactions.
 */
class AlarmActivity : AppCompatActivity(), LifecycleRegistryOwner {

    private val registry = LifecycleRegistry(this)
    override fun getLifecycle() = registry

    val NAME = AlarmActivity::getLocalClassName

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

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
        floating_action_btn.hide()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // animate the fab back into view
        floating_action_btn.show()
    }
}