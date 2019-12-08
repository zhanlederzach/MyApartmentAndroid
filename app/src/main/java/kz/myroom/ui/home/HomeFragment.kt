package kz.myroom.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Criteria
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import kz.myroom.R
import kz.myroom.model.BedroomInfo
import kz.myroom.model.Restaraunt
import kz.myroom.utils.AppConstants
import kz.myroom.utils.custom_views.CustomDialog
import kz.myroom.utils.extensions.initRecyclerView
import kz.myroom.utils.extensions.isPermissionGranted
import kz.myroom.utils.getUserLocationWithPermissionCheck
import org.koin.android.ext.android.inject
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnShowRationale
import permissions.dispatcher.PermissionRequest
import java.util.ArrayList

/**
 * done by Zhanel
 */
class HomeFragment : Fragment() {

    private lateinit var bedroomsRV: RecyclerView

    private val viewModel: HomeViewModel by inject()
    private var locationManager: LocationManager? = null
    private var bedroomList: List<Restaraunt>? = null
    private lateinit var swipeRefresh: SwipeRefreshLayout

    private val beedroomAdapter by lazy {
        BeedroomAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews(view)
        setAdapter()
        setData()
    }

    private fun bindViews(view: View) {
        bedroomsRV = view.findViewById(R.id.bedroomList)
        swipeRefresh = view.findViewById(R.id.swipeRefresh)

        swipeRefresh.setOnRefreshListener {
            beedroomAdapter.setNewData(ArrayList())
            viewModel.getRestaraunts()
        }
    }

    private fun setAdapter() {
        beedroomAdapter.apply {
            setOnItemClickListener { adapter, view, position ->
                val bundle = Bundle()
                val bedroom = getItem(position)
                bundle.putParcelable(AppConstants.BEDROOM, bedroom)
                view.findNavController().navigate(R.id.actionBedroomDetail, bundle)
            }
//            setEnableLoadMore(true)
//            setOnLoadMoreListener(object : BaseQuickAdapter.RequestLoadMoreListener {
//                @TargetApi(Build.VERSION_CODES.O)
//                override fun onLoadMoreRequested() {
//                    viewModel.getRestaraunts()
//                }
//            }, bedroomsRV)
        }
        activity?.initRecyclerView(bedroomsRV)
        bedroomsRV.adapter = beedroomAdapter
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setData() {
        viewModel.getRestaraunts()
        viewModel.liveData.observe(this, Observer { result ->
            when(result) {
                is HomeViewModel.ResultData.HideLoading -> {
                    swipeRefresh.isRefreshing = false
                }
                is HomeViewModel.ResultData.ShowLoading -> {
                    swipeRefresh.isRefreshing = true
                }
                is HomeViewModel.ResultData.LoadMoreFinished -> {
                    beedroomAdapter.apply {
                        loadMoreComplete()
                        loadMoreEnd(true)
                    }
                }
                is HomeViewModel.ResultData.Result -> {
//                    if (isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        bedroomList = result.info
                        beedroomAdapter.replaceData(result.info as MutableList<Restaraunt>)
//                        getDistance()
//                    }else {
//                        getUserLocationWithPermissionCheck()
//                    }
                }
                is HomeViewModel.ResultData.Error -> {
                    Toast.makeText(activity, "Some error occurred", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    @SuppressLint("MissingPermission")
    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    fun getDistance() {
        locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var userLocation = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if (userLocation == null) {
            val criteria = Criteria()
            criteria.accuracy = Criteria.ACCURACY_COARSE
            val provider = locationManager?.getBestProvider(criteria, true)
            userLocation = locationManager!!.getLastKnownLocation(provider)
        }
        if(userLocation!=null){
            for (bedroom in bedroomList!!) {
                if (assertStudioLocationCorrect(bedroom)) {
                    bedroom.dictance = AppConstants.meterDistanceBetweenPoints(
                        userLocation.latitude, userLocation.longitude,
                        bedroom.lat, bedroom.lng
                    ).toInt()
                }
            }
            beedroomAdapter.setSortedData(bedroomList as MutableList<Restaraunt>)
        }
    }

    @OnShowRationale(Manifest.permission.ACCESS_FINE_LOCATION)
    fun showLocationRequest(request: PermissionRequest) {
        if (activity != null) {
            val dialog = CustomDialog(activity, R.style.PauseDialog)
            dialog.apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                setContentView(R.layout.dialog_location_request)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                activity?.findViewById<Button>(R.id.btnCancelLocationRequest)?.setOnClickListener {
                    request.cancel()
                    cancel()
                }
                activity?.findViewById<Button>(R.id.btnApproveLocation)?.setOnClickListener {
                    request.proceed()
                    cancel()
                }
                show()
            }
        }
    }

    private fun assertStudioLocationCorrect(bedroom: Restaraunt?): Boolean {
        return bedroom?.lat != null || bedroom?.lng != null
    }

    data class PreferenceForBedroom(
        var numberOfBeds: Int? = null,
        var startDate: String? = null,
        var endDate: String? = null
    )
}
