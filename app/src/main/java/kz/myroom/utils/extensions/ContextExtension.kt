package kz.myroom.utils.extensions

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable

fun Context.initRecyclerView(
    recyclerView: RecyclerView,
    orientation: Int = RecyclerView.VERTICAL,
    stackFromEnd: Boolean = false
) = with(recyclerView) {
    val linearLayoutManager = LinearLayoutManager(this@initRecyclerView, orientation, false)
    linearLayoutManager.stackFromEnd = stackFromEnd
    linearLayoutManager.reverseLayout = stackFromEnd
    layoutManager = linearLayoutManager
    setHasFixedSize(true)
    setItemViewCacheSize(20)
}

fun Context.circularProgress(): CircularProgressDrawable {
    return CircularProgressDrawable(this).apply {
        strokeWidth = 3f
        centerRadius = 15f
        start()
    }
}

