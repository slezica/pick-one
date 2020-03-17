package io.slezica.pickone.component

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import io.slezica.pickone.model.Pointer

class PickerTouchOverlay(context: Context, attrs: AttributeSet?): FrameLayout(context, attrs) {

    interface Listener {
        fun onPointerDown(pointer: Pointer)
        fun onPointerUp(pointer: Pointer)
        fun onPointerMove(pointer: Pointer)
    }

    var listener: Listener? = null

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val pointerIndex = event.actionIndex
        val pointerId = event.getPointerId(pointerIndex)

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_POINTER_DOWN -> {
                val pointer = Pointer(
                    id = pointerId,
                    x = event.getX(pointerIndex),
                    y = event.getY(pointerIndex)
                )

                listener?.onPointerDown(pointer)
            }

            MotionEvent.ACTION_MOVE -> {
                for (i in 0 until event.pointerCount) {
                    val pointer = Pointer(
                        id = event.getPointerId(i),
                        x = event.getX(i),
                        y = event.getY(i)
                    )

                    listener?.onPointerMove(pointer)
                }
            }

            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_POINTER_UP,
            MotionEvent.ACTION_CANCEL -> {
                val pointer = Pointer(
                    id = pointerId,
                    x = event.getX(pointerIndex),
                    y = event.getY(pointerIndex)
                )

                listener?.onPointerUp(pointer)
            }
        }

        invalidate()
        return true
    }
}