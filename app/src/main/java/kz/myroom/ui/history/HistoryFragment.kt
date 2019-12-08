package kz.myroom.ui.history

import android.location.LocationManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import kz.myroom.R
import kz.myroom.model.Restaraunt
import kz.myroom.ui.home.BeedroomAdapter
import kz.myroom.ui.home.HomeViewModel
import kz.myroom.utils.AppConstants
import kz.myroom.utils.extensions.initRecyclerView
import org.koin.android.ext.android.inject
import java.util.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class HistoryFragment : Fragment() {

    private lateinit var bedroomsRV: RecyclerView

    private val viewModel: HomeViewModel by inject()
    private var locationManager: LocationManager? = null
    private var bedroomList: List<Restaraunt>? = null
    private lateinit var swipeRefresh: SwipeRefreshLayout

    private val beedroomAdapter by lazy {
        HistoryAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews(view)
        setAdapter()
        setData()
    }

    private fun bindViews(view: View) {
        bedroomsRV = view.findViewById(R.id.myRestoraunts)
        swipeRefresh = view.findViewById(R.id.swipeRefresh)

        swipeRefresh.setOnRefreshListener {
            beedroomAdapter.setNewData(ArrayList())
            viewModel.getBookedRestaraunts()
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

    private fun setData() {
        viewModel.getBookedRestaraunts()
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


}
