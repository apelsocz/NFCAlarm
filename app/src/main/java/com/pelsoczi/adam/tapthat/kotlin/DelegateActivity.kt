package com.pelsoczi.adam.tapthat.kotlin
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.pelsoczi.adam.tapthat.MyApplication
import com.pelsoczi.adam.tapthat.RingingActivity
import com.pelsoczi.adam.tapthat.app.MediaService
import com.pelsoczi.adam.tapthat.kotlin.ui.AlarmActivity

class DelegateActivity : AppCompatActivity() {

    val NAME = DelegateActivity::getLocalClassName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isRinging = Application.isRinging
//        val isRinging = MyApplication.getInstance().isRinging
        val isSnoozed = Application.isSnoozing
//        val isSnoozed = MyApplication.getInstance().isSnoozing

        // determine which activity should be started
        if (isRinging || isSnoozed) {
            if (isSnoozed) {
                // play media
                val media = Intent(applicationContext, MediaService::class.java)
                media.action = MediaService.ACTION_PLAY
                applicationContext.startService(media)

                // todo the app was launched while snoozed, modify the global state
                MyApplication.getInstance().setSnoozing(false)
                MyApplication.getInstance().doScheduling(false)
            }
            // present the user with ringing alarm
            startActivity( Intent(applicationContext, RingingActivity::class.java) )
        } else {
            // present the user the primary activity
            startActivity( Intent(applicationContext, AlarmActivity::class.java) )
        }
        // done
        finish()
    }
}