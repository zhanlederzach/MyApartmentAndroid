package kz.myroom.utils

import kz.myroom.R

object AppConstants {

    const val BEDROOM = "BEDROOM"
    const val PROFILE_STATISCTIC = "PROFILE_STATISCTIC"
    const val DEFAULT_PAGE = 0
    const val PAGE_LIMIT = 10

    val rootDestinations = setOf(R.id.home_dest, R.id.news_dest, R.id.profile_dest)

    fun meterDistanceBetweenPoints(lat_a: Double, lng_a: Double, lat_b: Double, lng_b: Double): Double {
        val pk = (180f / Math.PI).toFloat()

        val a1 = lat_a / pk
        val a2 = lng_a / pk
        val b1 = lat_b / pk
        val b2 = lng_b / pk

        val t1 = Math.cos(a1.toDouble()) * Math.cos(a2.toDouble()) * Math.cos(b1.toDouble()) * Math.cos(b2.toDouble())
        val t2 = Math.cos(a1.toDouble()) * Math.sin(a2.toDouble()) * Math.cos(b1.toDouble()) * Math.sin(b2.toDouble())
        val t3 = Math.sin(a1.toDouble()) * Math.sin(b1.toDouble())
        val tt = Math.acos(t1 + t2 + t3)

        return 6366000 * tt
    }

}