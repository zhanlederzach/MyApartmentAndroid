package kz.myroom.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kz.myroom.R
import kz.myroom.model.Restaraunt

class HistoryAdapterResto(private val dataset: MutableList<Restaraunt> = mutableListOf()) : RecyclerView.Adapter<HistoryAdapterResto.MainViewHolder>() {

    private var removedPosition: Int = 0
    private var removedItem: Restaraunt? = null

    class MainViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val title: TextView = v.findViewById(R.id.tvTitle)
        val time: TextView = v.findViewById(R.id.tvTime)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): HistoryAdapterResto.MainViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_booked_resto, viewGroup, false)
        return MainViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: MainViewHolder, position: Int) {
        viewHolder.title.text = dataset[position].name
        viewHolder.time.text = "17:30PM"
    }

    fun removeItem(position: Int, viewHolder: RecyclerView.ViewHolder) {
        removedItem = dataset[position]
        removedPosition = position

        dataset.removeAt(position)
        notifyItemRemoved(position)

        Snackbar.make(viewHolder.itemView, "$removedItem removed", Snackbar.LENGTH_LONG).setAction("UNDO") {
            dataset.add(removedPosition, removedItem!!)
            notifyItemInserted(removedPosition)

        }.show()
    }

    override fun getItemCount() = dataset.size

    fun replaceData(data: List<Restaraunt>) {
        dataset.clear()
        dataset.addAll(data)
        notifyDataSetChanged()
    }

    fun getElemtByPosition(adapterPosition: Int): Restaraunt {
        return dataset.get(adapterPosition)
    }
}