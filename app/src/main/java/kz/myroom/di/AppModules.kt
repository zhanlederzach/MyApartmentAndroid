package kz.myroom.di

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kz.myroom.api.RestarauntApi
import kz.myroom.repositories.*
import kz.myroom.ui.history.HistoryViewModel
import kz.myroom.ui.home.HomeViewModel
import kz.myroom.ui.profile.ProfileViewModel
import kz.myroom.utils.StethoUtils
import okhttp3.OkHttpClient
import okhttp3.Protocol
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.reactivestreams.Publisher
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLException

val networkModule = module {
    single { createGson() }
    single { createSharedPreference(androidContext()) }
    single { createHttpClient() }
    single { createApiService(get(), get()) }

    single { LocalStorageImpl(get(), get()) as ILocalRepository }
    single { ApartmentRepositoryImpl(get(), get()) as IApartmentRepository }
    single { RestarauntRepositoryImpl(get(), get(), get()) as IRestarauntRepository }
}

val viewModelModule = module (override = true) {
    viewModel { ProfileViewModel() }
    viewModel { HomeViewModel(get()) }
    viewModel { HistoryViewModel(get()) }
}

fun <T> applySchedulersSingle(): SingleTransformer<T, T> {
    return object : SingleTransformer<T, T> {
        override fun apply(upstream: Single<T>): SingleSource<T> {
            return upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        }
    }
}

fun <T> applySchedulersFlowable(): FlowableTransformer<T, T> {
    return object : FlowableTransformer<T, T> {
        override fun apply(upstream: Flowable<T>): Publisher<T> {
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

fun createHttpClient(): OkHttpClient {
    val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60L, TimeUnit.SECONDS)
        .readTimeout(60L, TimeUnit.SECONDS)
        .protocols(Collections.singletonList(Protocol.HTTP_1_1))
        .cache(null)
        .addNetworkInterceptor(StethoUtils.createInterceptor())

    return okHttpClient.build()
}

fun createApiService(okHttpClient: OkHttpClient, gson: Gson): RestarauntApi {
    return Retrofit.Builder()
        .baseUrl("http://opentable.herokuapp.com/api/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(RestarauntApi::class.java)
}

//fun <T> Single<T>.mapNetworkErrors(resourceUtils: AppResourceUtils, gson: Gson): Single<T> =
//    this.onErrorResumeNext { error ->
//        error.printStackTrace()
//        when (error) {
//            is NoConnectionException -> Single.error(NoConnectionException(error.message))
//            is SocketTimeoutException, is ConnectException, is UnknownHostException, is SSLException -> {
//                Single.error(NoConnectionException(resourceUtils.getString(R.string.server_connection_error_text)))
//            }
//            is HttpException -> {
//                val errorBody = error.response()?.errorBody()?.string()
//                val response = gson.fromJson<ErrorResponse>(errorBody, ErrorResponse::class.java)
//                Single.error(Throwable(response.message))
//            }
//            else -> Single.error(Throwable(error.message))
//        }
//    }

val appModules = listOf(networkModule, viewModelModule)

