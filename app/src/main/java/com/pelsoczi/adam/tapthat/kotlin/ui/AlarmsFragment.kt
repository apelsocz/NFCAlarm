package com.pelsoczi.adam.tapthat.kotlin.ui

import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LifecycleRegistryOwner
import android.arch.lifecycle.LiveData
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearLayoutManager.VERTICAL
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.ViewSwitcher
import com.pelsoczi.adam.tapthat.R
import com.pelsoczi.adam.tapthat.kotlin.Application
import com.pelsoczi.adam.tapthat.ui.Adapter
import com.pelsoczi.data.Alarm
import kotlinx.android.synthetic.main.activity_application.*
import kotlinx.android.synthetic.main.alarms_fragment.*


class AlarmsFragment : Fragment(), LifecycleRegistryOwner {
    companion object {
        val NAME = AlarmsFragment::class.java.simpleName
        fun newInstance() = AlarmsFragment()
    }
    init {
        println("init{${AlarmsFragment}")
    }

    private lateinit var alarmsLiveData: LiveData<List<Alarm>>
    private lateinit var adapter: Adapter

    private val factory = ViewSwitcher.ViewFactory {
        val textView = TextView(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            textView.setTextAppearance(R.style.TextAppearance_AppCompat_Medium)
        textView
    }
    private val registry = LifecycleRegistry(this)

    override fun getLifecycle() = registry

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return LayoutInflater.from(container?.context).inflate(R.layout.alarms_fragment,
                container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        with(ac_next_date) {
            setFactory(factory)
            setInAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in))
            setOutAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_out))
        }
        with(ac_next_time) {
            setFactory(factory)
            setInAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in))
            setOutAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_out))
        }

        adapter = Adapter(context, listOf(Alarm.EMPTY))
        with(alarm_container_recycler) {
            setHasFixedSize(true)
            adapter = this@AlarmsFragment.adapter
            layoutManager = LinearLayoutManager(activity, VERTICAL, false)
            itemAnimator = DefaultItemAnimator()
        }

        alarmsLiveData = (activity.application as Application).getViewModel().alarmsLiveData()
        alarmsLiveData.observeForever { alarms: List<Alarm>? ->
            if (alarms != null && alarms.isNotEmpty()) {
                alarm_container_recycler.swapAdapter(Adapter(context, alarms),
                        false)
                this@AlarmsFragment.adapter = alarm_container_recycler.adapter as Adapter
                updateNext()
            }
        }

        activity.floating_action_btn.setOnClickListener { view ->
            println("$NAME: floating_action_btn onClick")
            (activity as AlarmActivity).onAlarmClick(Alarm.EMPTY)
        }
    }

    override fun onResume() {
        super.onResume()

    }

    private fun updateNext() {
        Log.i(NAME, "updateNext()")
    }

    public fun toggleAlarm(alarm: Alarm) {
        // viewmodel toggle isactive and recalculate next
    }
}