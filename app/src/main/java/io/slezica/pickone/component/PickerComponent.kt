package io.slezica.pickone.component

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import io.slezica.androidexperiments.components.Component
import io.slezica.pickone.R
import io.slezica.pickone.arch.dp
import io.slezica.pickone.arch.log
import io.slezica.pickone.databinding.PickerBinding
import io.slezica.pickone.model.Indicator
import io.slezica.pickone.model.Pointer
import java.util.*
import kotlin.collections.mutableMapOf
import kotlin.collections.set


class PickerComponent: Component<PickerBinding>(), PickerTouchOverlay.Listener {

    companion object {
        private const val SUBMIT_HOLD_MS = 1500L
        private const val SUBMIT_VIBRATE_MS = 150L

        private val INDICATOR_SIZE = 1024.dp

        private val INDICATOR_COLORS = intArrayOf(
            R.color.indicator_1,
            R.color.indicator_2,
            R.color.indicator_3,
            R.color.indicator_4,
            R.color.indicator_5,
            R.color.indicator_6,
            R.color.indicator_7
        )
    }

    val handler = Handler()
    var winner: Pointer? = null
    val pointers = mutableMapOf<Int, Pointer>()

    override fun createView(inflater: LayoutInflater) =
        PickerBinding.inflate(inflater)

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView() {
        super.onCreateView()

        ui.touchOverlay.listener = this

        pointers.clear()
        winner = null
    }

    override fun onPointerDown(pointer: Pointer) {
        pointers[pointer.id] = pointer

        if (winner == null) {
            log("addOrUpdate", pointer.id)
            ui.indicatorLayout.addOrUpdate(Indicator(pointer, colorFor(pointer), INDICATOR_SIZE))
        }

        onPointerCountChange()
    }

    override fun onPointerMove(pointer: Pointer) {
        if (pointers.contains(pointer.id)) {
            pointers[pointer.id] = pointer
        }

        ui.indicatorLayout.addOrUpdate(Indicator(pointer, colorFor(pointer), INDICATOR_SIZE))
    }

    override fun onPointerUp(pointer: Pointer) {
        pointers.remove(pointer.id)

        if (winner == null) {
            ui.indicatorLayout.remove(pointer.id)
        }

        onPointerCountChange()
    }

    private fun onPointerCountChange() {
        handler.removeCallbacks(submitResult)

        if (pointers.isEmpty()) {
            winner = null
            ui.indicatorLayout.unselect()

        } else if (winner == null) {
            handler.postDelayed(submitResult, SUBMIT_HOLD_MS)
        }
    }

    val submitResult = Runnable {
        winner = pointers[Random().nextInt(pointers.size)]
        pointers.clear()

        vibrate()
        ui.indicatorLayout.select(winner!!.id)
    }

    fun colorFor(pointer: Pointer) =
        ContextCompat.getColor(context, INDICATOR_COLORS[pointer.id % INDICATOR_COLORS.size])

    fun vibrate() {
        val v = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = VibrationEffect
                .createOneShot(SUBMIT_VIBRATE_MS, VibrationEffect.DEFAULT_AMPLITUDE)

            v.vibrate(effect)

        } else {
            v.vibrate(SUBMIT_VIBRATE_MS)
        }
    }
}