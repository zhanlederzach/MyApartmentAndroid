package kz.myroom.ui.home

import android.widget.Button
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kz.myroom.R
import kz.myroom.model.Restaraunt
import kz.myroom.utils.extensions.setTextAsync
import org.joda.time.DateTimeComparator


class BeedroomAdapter: BaseQuickAdapter<Restaraunt, BaseViewHolder>(R.layout.item_bedroom) {

    override fun convert(helper: BaseViewHolder, item: Restaraunt?) {
        helper.apply {
            getView<Button>(R.id.btnBook).setTransformationMethod(null)
            setTextAsync(R.id.tvTitleResto, item?.name)
            setTextAsync(R.id.tvDescription, "LoremLoremLoremLoremLoremLoremLoremLoremLoremLoremLoremLorem")
            setTextAsync(R.id.tvDate, "10.12.2019")
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