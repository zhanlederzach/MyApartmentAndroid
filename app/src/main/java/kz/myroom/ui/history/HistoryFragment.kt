package kz.myroom.ui.history

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Layout
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import kz.myroom.R
import kz.myroom.model.Restaraunt
import kz.myroom.utils.extensions.initRecyclerView
import org.koin.android.ext.android.inject
import java.util.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class HistoryFragment : Fragment() {

    private lateinit var bedroomsRV: RecyclerView
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var colorDrawableBackground: ColorDrawable
    private lateinit var deleteIcon: Drawable
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var emptyView: LinearLayout

    private val viewModel: HistoryViewModel by inject()
    private var bedroomList: List<Restaraunt>? = null

//    private val emptyView by lazy {
//        layoutInflater.inflate(R.layout.view_empty_list, null)
//    }

    private val beedroomAdapter by lazy {
        HistoryAdapterResto()
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
        initMyAdapter()
        setData()
    }

    private fun bindViews(view: View) = with(view) {
        bedroomsRV = view.findViewById(R.id.myRestoraunts)
        swipeRefresh = view.findViewById(R.id.swipeRefresh)
        emptyView = view.findViewById(R.id.emptyView)

        swipeRefresh.setOnRefreshListener {
            beedroomAdapter.replaceData(ArrayList())
            viewModel.getBookedRestaraunts()
        }
    }

    private fun initMyAdapter() {
        activity?.initRecyclerView(bedroomsRV)
        bedroomsRV.adapter = beedroomAdapter
        viewManager = LinearLayoutManager(context)
        colorDrawableBackground = ColorDrawable(Color.parseColor("#ff0000"))
        deleteIcon = context?.let { ContextCompat.getDrawable(it, R.drawable.ic_delete) }!!

        bedroomsRV.apply {
            setHasFixedSize(true)
            adapter = beedroomAdapter
            layoutManager = viewManager
            addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
        }

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, viewHolder2: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDirection: Int) {
                Log.d("HistoryFragment", "onSwiped: ");
                viewModel.deleteItem(beedroomAdapter.getElemtByPosition(viewHolder.adapterPosition))
                (beedroomAdapter as HistoryAdapterResto).removeItem(viewHolder.adapterPosition, viewHolder)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val iconMarginVertical = (viewHolder.itemView.height - deleteIcon.intrinsicHeight) / 2

                if (dX > 0) {
                    colorDrawableBackground.setBounds(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                    deleteIcon.setBounds(itemView.left + iconMarginVertical, itemView.top + iconMarginVertical,
                        itemView.left + iconMarginVertical + deleteIcon.intrinsicWidth, itemView.bottom - iconMarginVertical)
                } else {
                    colorDrawableBackground.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                    deleteIcon.setBounds(itemView.right - iconMarginVertical - deleteIcon.intrinsicWidth, itemView.top + iconMarginVertical,
                        itemView.right - iconMarginVertical, itemView.bottom - iconMarginVertical)
                    deleteIcon.level = 0
                }

                colorDrawableBackground.draw(c)

                c.save()

                if (dX > 0)
                    c.clipRect(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                else
                    c.clipRect(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)

                deleteIcon.draw(c)

                c.restore()

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(bedroomsRV)
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
                        emptyView.visibility = View.VISIBLE
                        beedroomAdapter.replaceData(ArrayList())
                    } else {
                        emptyView.visibility = View.GONE
                        bedroomList = result.info
                        beedroomAdapter.replaceData(result.info as MutableList<Restaraunt>)
                    }
                    viewModel.getBookedRestaurantsWithInterval()
                }
                is HistoryViewModel.ResultData.ResultDiffCallback -> {
                    if (result.diffCallback.isNullOrEmpty()) {
                        emptyView.visibility = View.VISIBLE
                        beedroomAdapter.replaceData(ArrayList())
                    } else {
                        emptyView.visibility = View.GONE
                        bedroomList = result.diffCallback
                        beedroomAdapter.replaceData(result.diffCallback as MutableList<Restaraunt>)
                    }
                }
                is HistoryViewModel.ResultData.Error -> {
                    Toast.makeText(activity, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }


}
