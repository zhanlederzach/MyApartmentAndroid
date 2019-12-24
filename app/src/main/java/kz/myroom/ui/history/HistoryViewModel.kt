package kz.myroom.ui.history

import androidx.lifecycle.MutableLiveData
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kz.myroom.di.applySchedulersFlowable
import kz.myroom.di.applySchedulersSingle
import kz.myroom.model.BedroomInfo
import kz.myroom.model.Restaraunt
import kz.myroom.repositories.IRestarauntRepository
import kz.myroom.utils.base.BaseViewModel
import java.util.concurrent.TimeUnit

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
                    liveData.value = ResultData.Result(result)
                } , { error ->
                    liveData.value = ResultData.Error(error.message)
                })
        )
    }

    fun getBookedRestaurantsWithInterval() {
        val disposable =
            Observable.interval(0, 2, TimeUnit.SECONDS)
                .flatMap {
                    return@flatMap restarauntRepository.getBookedRestaurants().toObservable()
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result -> liveData.value = ResultData.ResultDiffCallback(result) },
                    { error ->
                        liveData.value = ResultData.Error(error.message ?: "")
                    }
                )
        disposables.addAll(disposable)
    }

    fun bookRestaurant(restoraunt: Restaraunt) {
        restarauntRepository.bookRestaurant(restoraunt)
    }

    fun deleteItem(restoraunt: Restaraunt) {
        restarauntRepository.deleteRestoraunt(restoraunt)
    }

    sealed class ResultData {
        object HideLoading: ResultData()
        object ShowLoading: ResultData()
        object ResultBooked: ResultData()
        data class ResultDiffCallback(val diffCallback: List<Restaraunt>): ResultData()
        data class Result(val info: List<Restaraunt>): ResultData()
        data class Error(val message: String?): ResultData()
        object LoadMoreFinished: ResultData()
        data class LoadMoreResult(val bedRoomInfo: List<BedroomInfo>): ResultData()
    }
}
