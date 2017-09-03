import android.app.Application

class Application : Application() {

    companion object {
        var isRinging = false
        var isSnoozing = false
    }

    override fun onCreate() {
        super.onCreate()
    }
}