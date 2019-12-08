package kz.myroom

import android.app.Application
import kz.myroom.di.appModules
import kz.myroom.utils.StethoUtils
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(appModules)
        }
        StethoUtils.install(this)
    }

}