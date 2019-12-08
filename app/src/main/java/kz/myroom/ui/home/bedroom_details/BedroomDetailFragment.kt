package kz.myroom.ui.home.bedroom_details


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.item_bedroom.*

import kz.myroom.R
import kz.myroom.model.BedroomInfo
import kz.myroom.model.Restaraunt
import kz.myroom.ui.MainActivity
import kz.myroom.ui.history.HistoryViewModel
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
    private lateinit var imageView: ImageView

    private val viewModel: HistoryViewModel by inject()
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
        imageView = view.findViewById(R.id.imageView)

        imageView.setOnClickListener{
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            val shareBody = "Application Link : https://play.google.com/store/apps/details?id=${context?.getPackageName()}"
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "App link")
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody)
            startActivity(Intent.createChooser(sharingIntent, "Share App Link Via :"))
        }

        btnBook.setOnClickListener {
            bedroom?.let {
                viewModel.bookRestaurant(it)
                viewModel.getBookedRestaraunts()
                Toast.makeText(activity, "${bedroom?.name} successfully booked", Toast.LENGTH_SHORT).show()
                view?.findNavController()?.navigate(R.id.actionBookBedroom)
            }
        }
    }

    private fun setData() {
        tvDistance.setText("200 m")
        tvNameBedroom.setText(bedroom?.name)
        tvDescription.setText("Это элегантное и по-своему простое заведение " +
                "с атмосферой Средиземного моря, на территории Esentai City. " +
                "Оно подходит  как для большой компании, так и для посиделок вдвоем. " +
                "Здесь всегда пахнет оливками и свежими фруктами, на кухне томятся морепродукты, " +
                "а гостям непременно подается вино. Здесь чувствуется та легкость, " +
                "которая приходят во время отпуска у моря.  ")
        tvDate.setText("12.09.2019")
        tvNumberOfRooms.setText(3.toString())
    }

}
