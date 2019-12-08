package kz.myroom.api

import io.reactivex.Single
import kz.myroom.model.RestarauntResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface RestarauntApi {

    @GET("restaurants")
    fun getRestoraunts(
        @Query("city") city: String,
        @Query("current_page") currentPage: Int
    ): Single<RestarauntResponse>

    @GET("restaurants")
    fun findRestoraunt(
        @Query("city") city: String,
        @Query("name") name: String?
    ): Single<RestarauntResponse>
}