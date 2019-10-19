package kz.myroom.repositories

import android.content.SharedPreferences

interface ILocalRepository {
    var isRegistered: Boolean
}

class LocalStorageImpl(
    private val sharedPreference: SharedPreferences
): ILocalRepository {

    companion object {
        const val IS_REGISTERED = "is_registered"
    }

    override var isRegistered: Boolean
        get() = sharedPreference.getBoolean(IS_REGISTERED, false)
        set(value) {
            sharedPreference.edit().putBoolean(IS_REGISTERED, value).apply()
        }

}