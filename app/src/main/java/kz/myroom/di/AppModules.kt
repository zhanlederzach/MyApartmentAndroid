package kz.myroom.di

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.reactivex.Single
import io.reactivex.SingleSource
import io.reactivex.SingleTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kz.myroom.repositories.ApartmentRepositoryImpl
import kz.myroom.repositories.IApartmentRepository
import kz.myroom.repositories.ILocalRepository
import kz.myroom.repositories.LocalStorageImpl
import kz.myroom.ui.home.HomeViewModel
import kz.myroom.ui.profile.ProfileViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val networkModule = module {
    single { createGson() }
    single { createSharedPreference(androidContext()) }

    single { LocalStorageImpl(get()) as ILocalRepository }
    single { ApartmentRepositoryImpl(get(), get()) as IApartmentRepository }
}

val viewModelModule = module (override = true) {
    viewModel { ProfileViewModel() }
    viewModel { HomeViewModel(get()) }
}

fun <T> applySchedulersSingle(): SingleTransformer<T, T> {
    return object : SingleTransformer<T, T> {
        override fun apply(upstream: Single<T>): SingleSource<T> {
            return upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        }
    }
}

fun createGson() : Gson = GsonBuilder()
    .setLenient()
    .create()

fun createSharedPreference(context: Context): SharedPreferences {
    val sharedPreferences = context.getSharedPreferences("localDB", Context.MODE_PRIVATE)
    return sharedPreferences
}

val appModules = listOf(networkModule, viewModelModule)

