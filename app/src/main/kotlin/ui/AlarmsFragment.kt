package ui


import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearLayoutManager.VERTICAL
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.ViewSwitcher
import com.pelsoczi.adam.tapthat.R
import com.pelsoczi.adam.tapthat.model.AlarmModel
import com.pelsoczi.adam.tapthat.ui.Adapter
import kotlinx.android.synthetic.main.activity_application.*
import kotlinx.android.synthetic.main.alarms_fragment.*


class AlarmsFragment : Fragment(), LifecycleOwner {

    private val registry = LifecycleRegistry(this)
    override fun getLifecycle() = registry

    companion object {
        val NAME = AlarmsFragment::class.java.simpleName
        fun newInstance() = AlarmsFragment()
    }

    private val factory = ViewSwitcher.ViewFactory {
        val textView = TextView(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            textView.setTextAppearance(R.style.TextAppearance_AppCompat_Medium)
        textView
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return LayoutInflater.from(container as? Context)
                .inflate(R.layout.alarms_fragment, container)
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

        update()

        with(alarm_container_recycler) {
            setHasFixedSize(true)
            adapter = Adapter(context, emptyList<AlarmModel>())
            layoutManager = LinearLayoutManager(activity, VERTICAL, false)
            itemAnimator = DefaultItemAnimator()
        }
    }

    override fun onResume() {
        super.onResume()
        floating_action_btn.setOnClickListener { view ->
            (activity as AlarmsActivity).onAlarmClick(Alarm.EMPTY)
        }
    }
}