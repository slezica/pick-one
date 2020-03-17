package io.slezica.pickone.component

import android.content.Context
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.children
import io.slezica.pickone.R
import io.slezica.pickone.arch.log
import io.slezica.pickone.arch.startAnimation
import io.slezica.pickone.model.Indicator


class PickerIndicatorLayout(context: Context, attrs: AttributeSet?): FrameLayout(context, attrs) {

    private val COLORS = listOf(
        R.color.indicator_1,
        R.color.indicator_2,
        R.color.indicator_3,
        R.color.indicator_4,
        R.color.indicator_5,
        R.color.indicator_6,
        R.color.indicator_7
    )

    private var selectedIndicatorView: View? = null

    fun addOrUpdate(indicator: Indicator) {
        var indicatorView = findIndicatorView(indicator.pointer.id)

        if (indicatorView == null) {
            log("Add", indicator.pointer.id)
            indicatorView = createIndicatorView(indicator)
            addView(indicatorView)

        } else {
            log("Update", indicator.pointer.id)
        }

        indicatorView.layoutParams = LayoutParams(indicator.size, indicator.size).also {
            it.leftMargin = (indicator.pointer.x - indicator.size / 2).toInt()
            it.topMargin = (indicator.pointer.y - indicator.size / 2).toInt()
        }
    }

    fun remove(indicatorId: Int) {
        findIndicatorView(indicatorId)?.let(this::removeIndicatorView)
    }

    fun select(indicatorId: Int) {
        for (child in children) {
            if (child.id == indicatorId) {
                selectIndicatorView(child)
            } else {
                removeIndicatorView(child)
            }
        }
    }

    fun unselect() {
        for (child in children) {
            if (child == selectedIndicatorView) {
                unselectIndicatorView(child)
            } else {
                removeIndicatorView(child)
            }
        }
    }

    private fun removeIndicatorView(view: View) {
        log("Remove", view.id, "start")

        view.startAnimation(R.anim.a_indicator_removed, onEnd = {
            log("Remove", view.id, "end")

            view.clearAnimation()
            removeView(view)
        })
    }

    private fun selectIndicatorView(view: View) {
        log("Select", view.id)
        selectedIndicatorView = view

        setIndicatorViewDrawable(view, R.drawable.ic_indicator_selected)
        view.startAnimation(R.anim.a_indicator_selected)
    }

    private fun unselectIndicatorView(view: View) {
        log("Unselect", view.id, "start")

        view.startAnimation(R.anim.a_indicator_unselected, onEnd = {
            log("Unselect", view.id, "end")
            selectedIndicatorView = null

            view.clearAnimation()
            removeView(view)
        })
    }

    private fun createIndicatorView(indicator: Indicator) = ImageView(context).also {
        it.id = indicator.pointer.id
        log("Create", it.id)

        setIndicatorViewDrawable(it, R.drawable.ic_indicator)
        it.startAnimation(R.anim.a_indicator_added)
    }

    private fun setIndicatorViewDrawable(view: View, @DrawableRes resId: Int) {
        val color = ContextCompat.getColor(context, COLORS[view.id % COLORS.size])

        val originalDrawable = context.getDrawable(resId)!!
        val tintedDrawable = DrawableCompat.wrap(originalDrawable.mutate())

        DrawableCompat.setTint(tintedDrawable, color)
        DrawableCompat.setTintMode(tintedDrawable, PorterDuff.Mode.SRC_IN)

        (view as ImageView).setImageDrawable(tintedDrawable)

        if (selectedIndicatorView != null) {
            view.visibility = GONE
        }
    }

    private fun findIndicatorView(indicatorId: Int): View? =
        findViewById<ImageView>(indicatorId)
}