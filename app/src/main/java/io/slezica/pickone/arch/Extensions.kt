package io.slezica.pickone.arch

import android.content.Context
import android.content.res.Resources
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import androidx.annotation.AnimRes
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import io.slezica.pickone.R


fun View.startAnimation(@AnimRes animationId: Int,
                        onStart: () -> Unit = {},
                        onRepeat: () -> Unit = {},
                        onEnd: () -> Unit = {}) {

    val anim = AnimationUtils.loadAnimation(context, animationId)
    val handler = Handler()

    val delayToAvoidCrashes = 100L // not 100% sure, has to do with removing a view on animation end

    anim.setAnimationListener(object: AnimationListener {
        override fun onAnimationStart(animation: Animation?) {
             handler.postDelayed(onStart, delayToAvoidCrashes)
        }

        override fun onAnimationEnd(animation: Animation?) {
            handler.postDelayed(onEnd, delayToAvoidCrashes)
        }
        override fun onAnimationRepeat(animation: Animation?) {
            handler.postDelayed(onRepeat, delayToAvoidCrashes)
        }
    })

    startAnimation(anim)
}


val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()


fun Context.getTintedDrawable(@DrawableRes drawableRes: Int, @ColorRes colorRes: Int): Drawable {
    val color = ContextCompat.getColor(this, colorRes)

    val originalImg = getDrawable(R.drawable.ic_indicator)!!
    val tintedImg = DrawableCompat.wrap(originalImg.mutate())

    DrawableCompat.setTint(tintedImg, color)
    DrawableCompat.setTintMode(tintedImg, PorterDuff.Mode.SRC_IN)

    return tintedImg
}


fun Any.log(vararg parts: Any) =
    Log.d(javaClass.simpleName, parts.joinToString(" "))