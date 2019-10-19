package kz.myroom.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import io.reactivex.Single
import kz.myroom.App
import kz.myroom.di.applySchedulersSingle
import kz.myroom.model.BedroomInfo
import kz.myroom.repositories.IApartmentRepository
import kz.myroom.utils.AppConstants
import kz.myroom.utils.base.BaseViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HomeViewModel(
    private val bedroomRepository: IApartmentRepository
): BaseViewModel() {

    val liveData by lazy {
        MutableLiveData<BedroomData>()
    }

    private var currentPage = AppConstants.DEFAULT_PAGE

    @RequiresApi(Build.VERSION_CODES.O)
    fun getBedroomInfo(){
        disposables.add(
            bedroomRepository.getBedrooms(offset = currentPage * AppConstants.PAGE_LIMIT)
                .compose(applySchedulersSingle())
                .doOnSubscribe{ liveData.value = BedroomData.ShowLoading }
                .doFinally { liveData.value = BedroomData.HideLoading }
                .subscribe(
                    { result ->
                        if (currentPage == AppConstants.DEFAULT_PAGE) {
                            liveData.value = BedroomData.Result(result)
                        }
                        else {
                            liveData.value = BedroomData.LoadMoreResult(result)
                            liveData.value = BedroomData.LoadMoreFinished
                        }
                        liveData.value = BedroomData.Result(result)
                        currentPage++
                    },
                    { error ->
                        liveData.value = BedroomData.Error(error.message)
                        liveData.value = BedroomData.LoadMoreFinished
                    }
                )
        )
    }

    fun bookBedRoom(id: String) {
        bedroomRepository.bookBedroom(idOfBedroom = id)
        liveData.value = BedroomData.ResultBooked
    }

    sealed class BedroomData {
        object HideLoading: BedroomData()
        object ShowLoading: BedroomData()
        object ResultBooked: BedroomData()
        data class Result(val bedRoomInfo: List<BedroomInfo>): BedroomData()
        data class Error(val message: String?): BedroomData()
        object LoadMoreFinished: BedroomData()
        data class LoadMoreResult(val bedRoomInfo: List<BedroomInfo>): BedroomData()
    }
}