package kz.myroom.ui.history

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import io.reactivex.Single
import kz.myroom.App
import kz.myroom.di.applySchedulersSingle
import kz.myroom.model.BedroomInfo
import kz.myroom.model.Restaraunt
import kz.myroom.repositories.IApartmentRepository
import kz.myroom.repositories.IRestarauntRepository
import kz.myroom.utils.AppConstants
import kz.myroom.utils.base.BaseViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HistoryViewModel(
    private val restarauntRepository: IRestarauntRepository
): BaseViewModel() {

    val liveData by lazy {
        MutableLiveData<ResultData>()
    }

    fun getBookedRestaraunts(){
        disposables.add(
            restarauntRepository.getBookedRestaurants()
                .compose(applySchedulersSingle())
                .doOnSubscribe { liveData.value = ResultData.ShowLoading }
                .doFinally { liveData.value = ResultData.HideLoading }
                .subscribe({ result ->
                    Log.d("HistoryViewModel", result.size.toString());
                    liveData.value = ResultData.Result(result)
                } , { error ->
                    liveData.value = ResultData.Error(error.message)
                })
        )
    }

    fun bookRestaurant(restoraunt: Restaraunt) {
        restarauntRepository.bookRestaurant(restoraunt)
    }

    sealed class ResultData {
        object HideLoading: ResultData()
        object ShowLoading: ResultData()
        object ResultBooked: ResultData()
        data class Result(val info: List<Restaraunt>): ResultData()
        data class Error(val message: String?): ResultData()
        object LoadMoreFinished: ResultData()
        data class LoadMoreResult(val bedRoomInfo: List<BedroomInfo>): ResultData()
    }
}
