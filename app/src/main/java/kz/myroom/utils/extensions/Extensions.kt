package kz.myroom.utils.extensions

import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

fun Fragment?.isFragmentAddedAndVisible(): Boolean {
    return this != null && this.isAdded && this.isVisible
}

fun Fragment.isPermissionGranted(permission: String)
        = (ContextCompat.checkSelfPermission(activity!!, permission) == PackageManager.PERMISSION_GRANTED)