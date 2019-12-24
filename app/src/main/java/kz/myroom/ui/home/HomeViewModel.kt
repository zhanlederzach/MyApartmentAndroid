package kz.myroom.ui.home

import androidx.lifecycle.MutableLiveData
import kz.myroom.di.applySchedulersSingle
import kz.myroom.model.Restaraunt
import kz.myroom.repositories.IRestarauntRepository
import kz.myroom.repositories.NoConnectionException
import kz.myroom.utils.Constants
import kz.myroom.utils.base.BaseViewModel

class HomeViewModel(
    private val restarauntRepository: IRestarauntRepository
): BaseViewModel() {

    val liveData by lazy {
        MutableLiveData<ResultData>()
    }

    init {
        getFromLocal()
    }

    private var currentPage = Constants.DEFAULT_PAGE

    fun getRestaraunts(isRefresh: Boolean = false){
        if (isRefresh) {
            currentPage = Constants.DEFAULT_PAGE
        }
        disposables.add(
            restarauntRepository.getRestoraunts(currentPage = currentPage)
                .compose(applySchedulersSingle())
                .doOnSubscribe {
                    if (currentPage == Constants.DEFAULT_PAGE) {
                        liveData.value = ResultData.ShowLoading
                    }
                }
                .doFinally { liveData.value = ResultData.HideLoading }
                .subscribe({ result ->
                    if (currentPage == Constants.DEFAULT_PAGE) {
                        liveData.value = ResultData.Result(result)
                    } else {
                        liveData.value = ResultData.LoadMoreResult(result)
                        liveData.value = ResultData.LoadMoreFinished
                    }
                    currentPage++
                } , { error ->
                    if (error is NoConnectionException) {
                        currentPage = Constants.DEFAULT_PAGE
                        getFromLocal()
                    }
                    liveData.value = ResultData.Error(error.message ?: "")
                    liveData.value = ResultData.LoadMoreFinished
                })
        )
    }

    private fun getFromLocal() {
        disposables.add(
            restarauntRepository.getLocalResto()
                .compose(applySchedulersSingle())
                .subscribe(
                    { liveData.value = ResultData.Result(it) },
                    { }
                )
        )
    }

    fun findResoraunt(name: String?){
        disposables.add(
            restarauntRepository.findRestoraunt(name = name)
                .compose(applySchedulersSingle())
                .subscribe({ result ->
                    liveData.postValue(ResultData.Result(result))
                } , { error ->
                    liveData.postValue(ResultData.Error(error.message))
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
        data class LoadMoreResult(val info: List<Restaraunt>): ResultData()
    }
}