import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LifecycleRegistryOwner
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.pelsoczi.adam.tapthat.R
import kotlinx.android.synthetic.main.activity_application.*
import ui.AlarmsFragment

/**
 * The primary activity which controls the user's interactions.
 */
class AlarmsActivity : AppCompatActivity(), LifecycleRegistryOwner {

    private val registry = LifecycleRegistry(this)
    override fun getLifecycle() = registry

    val NAME = AlarmsActivity::getLocalClassName

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        setContentView(R.layout.activity_application)
        setSupportActionBar(app_bar as Toolbar)

        savedInstanceState ?: init()
    }

    private fun init() {
        supportFragmentManager.beginTransaction()
                .add(fragmentContainer.id, AlarmsFragment.newInstance(), AlarmsFragment.NAME)
                .commitAllowingStateLoss()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // animate the fab back into view
        floating_action_btn.show()
    }
}