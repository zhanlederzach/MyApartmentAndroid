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