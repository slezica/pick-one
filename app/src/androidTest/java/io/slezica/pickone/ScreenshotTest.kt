package io.slezica.pickone

import android.graphics.Bitmap
import android.os.SystemClock
import android.view.InputDevice
import android.view.MotionEvent
import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileOutputStream

/**
 * Not a pass/fail test: a screenshot harness. Launches the app, presses three
 * fingers at once on the touch overlay (a real multi-pointer MotionEvent, since
 * a production build blocks /dev/input injection), lets the indicators settle,
 * and writes a PNG to the app's external files dir for `adb pull`.
 *
 * Run:  connectedDebugAndroidTest, then pull three-fingers.png.
 */
@RunWith(AndroidJUnit4::class)
class ScreenshotTest {

    // Distributed touch points, as fractions of the overlay size.
    private val points = listOf(
        0.28f to 0.34f,
        0.72f to 0.55f,
        0.42f to 0.76f
    )

    @Test
    fun captureThreeFingers() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()

        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            SystemClock.sleep(800) // let the fragment/view settle

            lateinit var overlay: View
            scenario.onActivity { activity ->
                overlay = activity.findViewById(R.id.touch_overlay)
                dispatchThreeFingerDown(overlay)
            }

            SystemClock.sleep(700) // add-animation finishes (<200ms) but stay < 1.5s hold

            val bitmap: Bitmap = instrumentation.uiAutomation.takeScreenshot()
            val out = File(
                instrumentation.targetContext.getExternalFilesDir(null),
                "three-fingers.png"
            )
            FileOutputStream(out).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        }
    }

    private fun dispatchThreeFingerDown(overlay: View) {
        val w = overlay.width
        val h = overlay.height
        val xs = points.map { it.first * w }
        val ys = points.map { it.second * h }

        val down = SystemClock.uptimeMillis()

        fun props(count: Int) = Array(count) { i ->
            MotionEvent.PointerProperties().apply {
                id = i
                toolType = MotionEvent.TOOL_TYPE_FINGER
            }
        }

        fun coords(count: Int) = Array(count) { i ->
            MotionEvent.PointerCoords().apply {
                x = xs[i]; y = ys[i]; pressure = 1f; size = 1f
            }
        }

        fun send(action: Int, count: Int) {
            val event = MotionEvent.obtain(
                down, SystemClock.uptimeMillis(), action,
                count, props(count), coords(count),
                0, 0, 1f, 1f, 0, 0,
                InputDevice.SOURCE_TOUCHSCREEN, 0
            )
            overlay.dispatchTouchEvent(event)
            event.recycle()
        }

        val ptrDown = MotionEvent.ACTION_POINTER_DOWN
        val shift = MotionEvent.ACTION_POINTER_INDEX_SHIFT

        send(MotionEvent.ACTION_DOWN, 1)
        send(ptrDown or (1 shl shift), 2)
        send(ptrDown or (2 shl shift), 3)
    }
}
