package kz.myroom.repositories

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Single
import kz.myroom.model.BedroomInfo
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

interface IApartmentRepository {
    fun getBedrooms(offset: Int): Single<List<BedroomInfo>>
    fun getBookedBedrooms(): Single<List<BedroomInfo>>
    fun bookBedroom(idOfBedroom: Int): String
}

class ApartmentRepositoryImpl (
    private val localStorage: ILocalRepository,
    private val gson: Gson
): IApartmentRepository {

    @RequiresApi(Build.VERSION_CODES.O)
    var listOfbedRooms: List<BedroomInfo> = generateBedroomList()
    var listOfBookedRooms: List<BedroomInfo> = arrayListOf()

    @RequiresApi(Build.VERSION_CODES.O)
    private fun generateBedroomList(): List<BedroomInfo> {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm")
        val formatted = current.format(formatter)

        val description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
                "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris " +
                "nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in " +
                "reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. "
        return arrayListOf(
            BedroomInfo("0x1", 1, "Noemia apt", description,59.329440, 18.066045, null, formatted),
            BedroomInfo("0x2", 1, "Thalia apt", description,59.329440, 18.066045, null, formatted),
            BedroomInfo("0x3", 1, "Ellie apt", description, 59.329440, 18.066045, null, formatted),
            BedroomInfo("0x4", 1, "Katherine apt", description,59.329440, 18.066045, null, formatted),
            BedroomInfo("0x5", 2, "Arpine apt", description,59.329440, 18.066045, null, formatted),
            BedroomInfo("0x6", 2, "Georgia apt", description,59.329440, 18.066045, null, formatted),
            BedroomInfo("0x7", 2, "Nantia apt", description,59.329440, 18.066045, null, formatted),
            BedroomInfo("0x8", 3, "Suzan apt", description,59.377871, 17.976483, null, formatted),
            BedroomInfo("0x9", 3, "Patrick apt", description,59.329440, 18.066045, null, formatted),
            BedroomInfo("0x10", 3, "Mark apt", description,59.329440, 18.066045, null, formatted)
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getBedrooms(offset: Int): Single<List<BedroomInfo>> {
        if(offset == 90)    return Single.fromCallable() { arrayListOf<BedroomInfo>() }
        return Single.fromCallable() {
            for(i in 0..10) {
                (listOfbedRooms as MutableList<BedroomInfo>)
                    .addAll((generateBedroomList() as MutableList<BedroomInfo>))
            }
            listOfbedRooms
        }
    }

    override fun getBookedBedrooms(): Single<List<BedroomInfo>> {
        return Single.fromCallable() { listOfBookedRooms }
    }

    override fun bookBedroom(idOfBedroom: Int): String {
        var cnt = 0
        for(item in listOfbedRooms){
//            if(item.id == idOfBedroom){
//                val bedroom = item
//                (listOfBookedRooms as MutableList<BedroomInfo>).add(bedroom)
//                (listOfbedRooms as MutableList<BedroomInfo>).removeAt(cnt)
//                return item.id
//            }
//            ++cnt
        }
        return "Error"
    }

}