package kz.myroom.ui.home

import android.content.Context
import android.widget.Button
import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kz.myroom.R
import kz.myroom.model.Restaraunt
import kz.myroom.utils.extensions.circularProgress
import kz.myroom.utils.extensions.setTextAsync
import org.joda.time.DateTimeComparator

interface OnBookClickListener {
    fun bookClickListener(restaraunt: Restaraunt)
}

interface OnShareClickListener {
    fun shareClickListener(restaraunt: Restaraunt)
}

class BeedroomAdapter(
    private val onBookClickListener: OnBookClickListener?,
    private val onShareClickListener: OnShareClickListener?
): BaseQuickAdapter<Restaraunt, BaseViewHolder>(R.layout.item_bedroom) {

    override fun convert(helper: BaseViewHolder, item: Restaraunt?) {
        helper.apply {
            getView<Button>(R.id.btnBook).setTransformationMethod(null)
            setTextAsync(R.id.tvTitleResto, item?.name)
            setTextAsync(R.id.tvDescription, "LoremLoremLoremLoremLoremLoremLoremLoremLoremLoremLoremLorem" +
                    "LoremLoremLoremLoremLoremLoremLoremLoremLoremLoremLoremLorem")
            setTextAsync(R.id.tvDate, "10.12.2019")

            Glide.with(itemView)
                .load(item?.imageUrl)
                .placeholder(itemView.context.circularProgress())
                .into(getView(R.id.imageViewMain))

            getView<ImageView>(R.id.imageView).setOnClickListener {
                item?.let { it -> onShareClickListener?.shareClickListener(it) }
            }

            getView<Button>(R.id.btnBook).setOnClickListener {
                item?.let { it -> onBookClickListener?.bookClickListener(it) }
            }
        }
    }

    override fun replaceData(data: MutableCollection<out Restaraunt>) {
        mData.clear()
        mData.addAll(data)
        notifyDataSetChanged()
    }


    fun setSortedData(data: MutableList<Restaraunt>) {
//        if(data.size > 0){
//            val listOfBerooms = data.sortedWith(compareBy({ it.dictance })) as MutableList<BedroomInfo>
//            replaceData(listOfBerooms)
//        } else {
//            replaceData(arrayListOf())
//        }
    }
/*
    fun setFilteredBedrooms(preferenceForBedroom: HomeFragment.PreferenceForBedroom) {
        var beedroomsRange = arrayListOf<BedroomInfo>()

        if(preferenceForBedroom.numberOfBeds != null) {
            beedroomsRange = data.filter { it.beedrooms ==  preferenceForBedroom.numberOfBeds } as ArrayList<BedroomInfo>
        }

        if(preferenceForBedroom.startDate != null && preferenceForBedroom.endDate != null) {
            val dateTimeComparator = DateTimeComparator.getDateOnlyInstance()
            beedroomsRange = data.filter {
                dateTimeComparator.compare(it.date, preferenceForBedroom.startDate) >= 0
                && dateTimeComparator.compare(it.date, preferenceForBedroom.startDate) <= 0
            } as ArrayList<BedroomInfo>
        }

        setSortedData(beedroomsRange as MutableList<BedroomInfo>)
    }
    */

}
