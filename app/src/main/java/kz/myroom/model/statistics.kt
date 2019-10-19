package kz.myroom.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
data class StatisticsInfo(
    @SerializedName("number") val numberOfExcellentPerfomances: Int?,
    @SerializedName("field_type") val field_type: String?
) : Parcelable