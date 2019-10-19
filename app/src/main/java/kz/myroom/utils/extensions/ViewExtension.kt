package kz.myroom.utils.extensions

import android.text.Spannable
import android.util.DisplayMetrics
import android.view.View
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat
import com.chad.library.adapter.base.BaseViewHolder

fun View.dpToPixel(dp: Float): Float =
    dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)

fun View.dpToPixel(dp: Int): Int =
    (dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()

fun View.dpToPixelInt(dp: Float): Int =
    (dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()

fun TextView.setTextAsync(text: CharSequence?) {
    (this as AppCompatTextView).apply {
        val params = TextViewCompat.getTextMetricsParams(this)
        val precomputedText  = PrecomputedTextCompat.create(text ?: "", params)
        setTextFuture(PrecomputedTextCompat.getTextFuture(precomputedText, params, null))
    }
}

fun TextView.setTextAsync(@StringRes strId: Int) {
    (this as AppCompatTextView).apply {
        val params = TextViewCompat.getTextMetricsParams(this)
        val precomputedText  = PrecomputedTextCompat.create(resources.getString(strId), params)
        setTextFuture(PrecomputedTextCompat.getTextFuture(precomputedText, params, null))
    }
}

fun BaseViewHolder.setTextAsync(@IdRes viewId: Int, value: CharSequence?): BaseViewHolder {
    val view = getView<TextView>(viewId)
    view.setTextAsync(value)
    return this
}

fun BaseViewHolder.setTextAsync(@IdRes viewId: Int, @StringRes strId: Int): BaseViewHolder {
    val view = getView<TextView>(viewId)
    view.setTextAsync(strId)
    return this
}