package me.kolotilov.lets_a_go.ui.base

import android.view.View
import android.widget.TextView
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.models.RouteEntry
import me.kolotilov.lets_a_go.ui.context
import me.kolotilov.lets_a_go.ui.duration
import me.kolotilov.lets_a_go.ui.getDrawableCompat
import org.joda.time.format.DateTimeFormat

class EntryFactory : Recycler.Factory<RouteEntry> {

    override fun getType(item: RouteEntry): Int {
        return R.layout.entry_detail_item
    }

    override fun getViewHolder(
        type: Int,
        itemView: View,
        delegate: Recycler.Delegate<RouteEntry>
    ): Recycler.ViewHolder<RouteEntry> {
        return EntryDetailsViewHolder(itemView, delegate)
    }
}

class EntryDetailsViewHolder(itemView: View, delegate: Recycler.Delegate<RouteEntry>) :
    Recycler.ViewHolder<RouteEntry>(itemView, delegate) {

    private val dateTextView = itemView.findViewById<TextView>(R.id.date_text_view)
    private val durationTextView = itemView.findViewById<TextView>(R.id.duration_text_view)
    private val dateFormat = DateTimeFormat.forPattern("dd MMMM HH:mm")

    override fun bind(item: RouteEntry, selected: Boolean) {
        dateTextView.text = dateFormat.print(item.date.millis)
        durationTextView.text = item.duration.duration()
        val background = if (item.passed)
            context.getDrawableCompat(R.drawable.entry_passed_item)
        else
            context.getDrawableCompat(R.drawable.entry_not_passed_item)
        itemView.background = background
    }
}