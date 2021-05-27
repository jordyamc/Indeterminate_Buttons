package knf.tools.indeterminatebuttons

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.StateSet
import android.util.TypedValue
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat


object Utils {
    fun applyAlpha(color: Int, alpha: Float): Int {
        return Color.argb(
            Math.round(Color.alpha(color) * alpha),
            Color.red(color),
            Color.green(color),
            Color.blue(color)
        )
    }

    fun tintDrawable(view: View, @DrawableRes drawable: Int): Drawable {
        require(view is IndeterminateCheckable) { "view must implement IndeterminateCheckable" }
        val colorStateList = createIndetermColorStateList(view.context)
        val d: Drawable = DrawableCompat.wrap(ContextCompat.getDrawable(view.context, drawable)!!)
        DrawableCompat.setTintList(d, colorStateList)
        return d
    }

    private fun createIndetermColorStateList(context: Context): ColorStateList {
        val states = arrayOf(
            intArrayOf(-android.R.attr.state_enabled),
            intArrayOf(R.attr.state_indeterminate),
            intArrayOf(android.R.attr.state_checked),
            StateSet.WILD_CARD
        )
        val normal = resolveColor(context, R.attr.colorControlNormal, Color.DKGRAY)
        val activated = resolveColor(context, R.attr.colorControlActivated, Color.CYAN)
        val disabledAlpha = resolveFloat(context, android.R.attr.disabledAlpha, 0.25f)
        val colors = intArrayOf(
            applyAlpha(normal, disabledAlpha),
            normal,
            activated,
            normal
        )
        return ColorStateList(states, colors)
    }

    private fun resolveColor(context: Context, @AttrRes attr: Int, defaultValue: Int): Int {
        val a = context.theme.obtainStyledAttributes(intArrayOf(attr))
        return try {
            a.getColor(0, defaultValue)
        } finally {
            a.recycle()
        }
    }

    private fun resolveFloat(context: Context, @AttrRes attr: Int, defaultValue: Float): Float {
        val `val` = TypedValue()
        return if (context.theme.resolveAttribute(attr, `val`, true)) {
            `val`.float
        } else {
            defaultValue
        }
    }
}