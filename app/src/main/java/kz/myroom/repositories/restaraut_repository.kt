package kz.myroom.repositories

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Single
import kz.myroom.api.RestarauntApi
import kz.myroom.model.ErrorResponse
import kz.myroom.model.Restaraunt
import org.apache.commons.io.FileUtils
import retrofit2.HttpException

interface IRestarauntRepository {
    fun getRestoraunts(): Single<List<Restaraunt>>
    fun bookRestaurant(restaraunt: Restaraunt)
    fun getBookedRestaurants(): Single<List<Restaraunt>>
    fun deleteBookedRestaurants()
}

private const val RESTARAUNT_BOOKED = "resto_booked"

class RestarauntRepositoryImpl (
    private val localStorage: ILocalRepository,
    private val gson: Gson,
    private val api: RestarauntApi
): IRestarauntRepository {

    private val restoTypeToken = object : TypeToken<List<Restaraunt>>() { }.type

    override fun getRestoraunts(): Single<List<Restaraunt>> {
        return api.getRestoraunts(city = "Chicago")
            .map {
                it.restaurants
            }
            .onErrorResumeNext { error ->
                when (error) {
                    is HttpException -> {
                        val errorBody = error.response()?.errorBody()?.string()
                        val response = gson.fromJson<ErrorResponse>(errorBody, ErrorResponse::class.java)
                        Single.error(Throwable(response.message))
                    } else -> {
                        Single.error(Throwable(error.message))
                    }
                }
            }
    }

    override fun bookRestaurant(restaraunt: Restaraunt) {
        Log.d("ResoRepo", "bookRestaurant: ");
        val file = localStorage.createFileInDir(fileName = RESTARAUNT_BOOKED)
        FileUtils.writeStringToFile(file, gson.toJson(restaraunt), "UTF-8")
    }

    override fun getBookedRestaurants(): Single<List<Restaraunt>> {
        return Single.fromCallable {
            val file = localStorage.createFileInDir(fileName = RESTARAUNT_BOOKED)
            val json = FileUtils.readFileToString(file, "UTF-8")
            if (json.isNullOrEmpty()) {
                ArrayList()
            } else {
                gson.fromJson<List<Restaraunt>>(json, restoTypeToken)
            }
        }
        .onErrorResumeNext { error ->
            when (error) {
                is HttpException -> {
                    val errorBody = error.response()?.errorBody()?.string()
                    val response = gson.fromJson<ErrorResponse>(errorBody, ErrorResponse::class.java)
                    Single.error(Throwable(response.message))
                } else -> {
                    Log.d("ResoRepoEr", error.message);
                    Single.error(Throwable(error.message))
                }
            }
        }
    }

    override fun deleteBookedRestaurants() {
        FileUtils.forceDelete(localStorage.createFileInDir(fileName = RESTARAUNT_BOOKED))
    }
}