package kz.myroom.utils

import android.content.Context
import android.os.StrictMode
import android.webkit.WebView
import com.facebook.stetho.Stetho

object StethoUtils {

    fun install(context: Context) {
        Stetho.initializeWithDefaults(context)
        WebView.setWebContentsDebuggingEnabled(true)
//        strictMode()
    }

    fun createInterceptor(): okhttp3.Interceptor = com.facebook.stetho.okhttp3.StethoInterceptor()

    private fun strictMode() {
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
            .detectDiskReads()
            .detectDiskWrites()
            .detectNetwork()
            .penaltyLog()
            .build())

        StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
            .detectLeakedSqlLiteObjects()
            .detectLeakedClosableObjects()
            .penaltyLog()
            .build())
    }
}