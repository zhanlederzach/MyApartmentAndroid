package kz.myroom.ui.home.bedroom_details


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.findNavController

import kz.myroom.R
import kz.myroom.model.BedroomInfo
import kz.myroom.model.Restaraunt
import kz.myroom.ui.home.HomeViewModel
import kz.myroom.utils.AppConstants
import org.koin.android.ext.android.inject

/**
 * done by Zhanel
 */
class BedroomDetailFragment : Fragment() {

    private lateinit var tvDistance: TextView
    private lateinit var tvNameBedroom: TextView
    private lateinit var tvNumberOfRooms: TextView
    private lateinit var tvDate: TextView
    private lateinit var btnBook: Button
    private lateinit var tvDescription: TextView

    private val viewModel: HomeViewModel by inject()
    private var bedroom: Restaraunt? = null

    override fun setArguments(args: Bundle?) {
        super.setArguments(args)
        if(args?.containsKey(AppConstants.BEDROOM) == true) {
            bedroom = args.getParcelable(AppConstants.BEDROOM)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bedroom_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.deleteBookedRestoraunts()
        bindViews(view)
        setData()
    }

    private fun bindViews(view: View) {
        tvDistance = view.findViewById(R.id.tvDistance)
        tvNameBedroom = view.findViewById(R.id.tvNameBedroom)
        tvNumberOfRooms = view.findViewById(R.id.tvNumberOfRooms)
        tvDescription = view.findViewById(R.id.tvDescription)
        tvDate = view.findViewById(R.id.tvDate)
        btnBook = view.findViewById(R.id.btnBook)

        btnBook.setOnClickListener {
            bedroom?.let {
                viewModel.bookRestaurant(it)
                Toast.makeText(activity, "${bedroom?.name} successfully booked", Toast.LENGTH_SHORT).show()
                view?.findNavController()?.navigate(R.id.actionBookBedroom)
            }
        }
    }

    private fun setData() {
        tvDistance.setText("200 m")
        tvNameBedroom.setText(bedroom?.name)
        tvDescription.setText("Very good apartemt")
        tvDate.setText("12.09.2019")
        tvNumberOfRooms.setText(3.toString())

        viewModel.liveData.observe(this, Observer { result ->
            when(result) {
                is HomeViewModel.ResultData.Result -> {
                    Toast.makeText(activity, "${bedroom?.name} successfully booked", Toast.LENGTH_SHORT).show()
                    view?.findNavController()?.navigate(R.id.actionBookBedroom)
                }
            }
        })
    }

}
