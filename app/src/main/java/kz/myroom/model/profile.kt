package kz.myroom.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class ProfileInfo(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("statisctics") val statisctics: StatisticsInfo
): Serializable