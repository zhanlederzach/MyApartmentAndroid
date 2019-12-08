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
import java.lang.Exception

interface IRestarauntRepository {
    fun getRestoraunts(currentPage: Int): Single<List<Restaraunt>>
    fun findRestoraunt(name: String?): Single<List<Restaraunt>>
    fun bookRestaurant(restaraunt: Restaraunt)
    fun getBookedRestaurants(): Single<List<Restaraunt>>
    fun deleteBookedRestaurants()
    fun getLocalResto(): Single<List<Restaraunt>>
}

private const val RESTARAUNT_BOOKED = "resto_booked"
private const val RESTARAUNTS = "resto"

class RestarauntRepositoryImpl (
    private val localStorage: ILocalRepository,
    private val gson: Gson,
    private val api: RestarauntApi
): IRestarauntRepository {

    private val restoTypeToken = object : TypeToken<List<Restaraunt>>() { }.type

    override fun getRestoraunts(currentPage: Int): Single<List<Restaraunt>> {
        return api.getRestoraunts(city = "Chicago", currentPage = currentPage)
            .map {
                if (currentPage == 0) {
                    saveResto(it.restaurants)
                }
                it.restaurants
            }
            .onErrorResumeNext { error ->
                when (error) {
                    is NoConnectionException -> Single.error(NoConnectionException(error.message))
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

    private fun saveResto(restaurants: List<Restaraunt>) {
        val file = localStorage.createFileInDir(fileName = RESTARAUNTS)
        FileUtils.writeStringToFile(file, gson.toJson(restaurants), Charsets.UTF_8)
    }

    override fun getLocalResto(): Single<List<Restaraunt>> {
        return Single.fromCallable {
            val file = localStorage.createFileInDir(fileName = RESTARAUNTS)
            val json = FileUtils.readFileToString(file, Charsets.UTF_8)
            if (json.isNullOrEmpty()) {
                ArrayList()
            } else {
                gson.fromJson<List<Restaraunt>>(json, restoTypeToken)
            }
        }
    }

    override fun findRestoraunt(name: String?): Single<List<Restaraunt>> {
        return api.findRestoraunt(city = "Chicago", name = name)
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
        val file = localStorage.createFileInDir(fileName = RESTARAUNT_BOOKED)
        val json = FileUtils.readFileToString(file, Charsets.UTF_8)
        if (json.isNullOrEmpty()) {
            Log.d("ResoRepoBook1", "empty");
            val restaraunts = arrayListOf(restaraunt).toList()
            FileUtils.writeStringToFile(file, gson.toJson(restaraunts), Charsets.UTF_8)
        } else {
            Log.d("ResoRepoBook2", json);
            val restaraunts = gson.fromJson<List<Restaraunt>>(json, restoTypeToken)
            (restaraunts as MutableList<Restaraunt>).add(restaraunt)
            Log.d("ResoRepoBook3", restaraunts.size.toString());
            FileUtils.writeStringToFile(file, gson.toJson(restaraunts), Charsets.UTF_8)
        }
    }

    override fun getBookedRestaurants(): Single<List<Restaraunt>> {
        return Single.fromCallable {
            val file = localStorage.createFileInDir(fileName = RESTARAUNT_BOOKED)
            val json = FileUtils.readFileToString(file, "UTF-8")
            Log.d("ResoRepoFile", json);
            if (json.isNullOrEmpty()) {
                Log.d("ResoRepoEr3", "empty");
                arrayListOf<Restaraunt>().toList()
            } else {
                Log.d("ResoRepoEr3", "not empty");
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

class NoConnectionException(override val message: String) : Throwable()