package kz.myroom.ui.history

import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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

    private val viewModel: HistoryViewModel by inject()
    private var bedroomList: List<Restaraunt>? = null
    private lateinit var swipeRefresh: SwipeRefreshLayout

    private val emptyView by lazy {
        layoutInflater.inflate(R.layout.view_empty_list, null)
    }

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

    private fun bindViews(view: View) = with(view) {
        bedroomsRV = view.findViewById(R.id.myRestoraunts)
        swipeRefresh = view.findViewById(R.id.swipeRefresh)

        swipeRefresh.setOnRefreshListener {
            beedroomAdapter.setNewData(ArrayList())
            viewModel.getBookedRestaraunts()
        }
        emptyView.findViewById<Button>(R.id.btnRefresh).setOnClickListener {
            viewModel.getBookedRestaraunts()
        }
    }

    private fun setAdapter() {
        beedroomAdapter.apply {
//            setOnItemClickListener { adapter, view, position ->
//                val bundle = Bundle()
//                val bedroom = getItem(position)
//                bundle.putParcelable(AppConstants.BEDROOM, bedroom)
//            }
        }
        activity?.initRecyclerView(bedroomsRV)
        bedroomsRV.adapter = beedroomAdapter
    }

    private fun setData() {
        viewModel.getBookedRestaraunts()
        viewModel.liveData.observe(this, Observer { result ->
            when(result) {
                is HistoryViewModel.ResultData.HideLoading -> {
                    swipeRefresh.isRefreshing = false
                }
                is HistoryViewModel.ResultData.ShowLoading -> {
                    swipeRefresh.isRefreshing = true
                }
                is HistoryViewModel.ResultData.Result -> {
                    Log.d("HistoryFragmentSiz", result.info.size.toString());
                    if (result.info.isNullOrEmpty()) {
                        beedroomAdapter.replaceData(ArrayList())
                    } else {
                        bedroomList = result.info
                        beedroomAdapter.replaceData(result.info as MutableList<Restaraunt>)
                    }
                }
                is HistoryViewModel.ResultData.Error -> {
                    Toast.makeText(activity, "Some error occurred", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }


}
