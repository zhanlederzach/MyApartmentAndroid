package kz.myroom.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Keep
@Parcelize
data class BedroomInfo(
    @SerializedName("id") val id: String,
    @SerializedName("beedrooms") val beedrooms: Int,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    var dictance: Int?,
    var date: String?
): Serializable, Parcelable

@Keep
@Parcelize
data class RestarauntResponse(
    @SerializedName("total_entries") val totalEntries: Int,
    @SerializedName("per_page") val perPage: Int,
    @SerializedName("current_page") val currentPage: Int,
    @SerializedName("restaurants") val restaurants: List<Restaraunt>
): Serializable, Parcelable

@Parcelize
data class Restaraunt(
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("address") val address: String?,
    @SerializedName("city") val city: String?,
    @SerializedName("state") val state: String?,
    @SerializedName("area") val area: String?,
    @SerializedName("postal_code") val postalCode: String?,
    @SerializedName("country") val country: String?,
    @SerializedName("phone") val phone: String?,
    @SerializedName("lat") val lat: Double,
    @SerializedName("lng") val lng: Double,
    @SerializedName("price") val price: Int?,
    @SerializedName("reserve_url") val reserveUrl: String?,
    @SerializedName("mobile_reserve_url") val mobileReserveUrl: String?,
    @SerializedName("image_url") val imageUrl: String?,
    var dictance: Int? = 0
): Serializable, Parcelable

@Parcelize
data class ErrorResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String
) : Parcelable
