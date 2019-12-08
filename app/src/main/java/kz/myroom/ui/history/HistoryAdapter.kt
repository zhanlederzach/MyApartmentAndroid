package kz.myroom.ui.history

import android.widget.Button
import android.widget.LinearLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kz.myroom.R
import kz.myroom.model.Restaraunt
import kz.myroom.utils.extensions.setTextAsync
import org.joda.time.DateTimeComparator

class HistoryAdapter(): BaseQuickAdapter<Restaraunt, BaseViewHolder>(R.layout.item_booked_resto) {

    override fun convert(helper: BaseViewHolder, item: Restaraunt?) {
        helper.apply {
            setTextAsync(R.id.tvTitle, item?.name)
            setTextAsync(R.id.tvTime, "17:30 PM")
        }
    }

    override fun replaceData(data: MutableCollection<out Restaraunt>) {
        mData.clear()
        mData.addAll(data)
        notifyDataSetChanged()
    }
}