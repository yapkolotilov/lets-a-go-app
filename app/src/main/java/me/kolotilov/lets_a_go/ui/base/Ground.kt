package me.kolotilov.lets_a_go.ui.base

import android.view.View
import android.widget.TextView
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.models.Route
import me.kolotilov.lets_a_go.ui.context
import me.kolotilov.lets_a_go.ui.name

class GroundFactory : Recycler.Factory<Route.Ground> {

    override fun getType(item: Route.Ground): Int = R.layout.ground_type

    override fun getViewHolder(
        type: Int,
        itemView: View,
        delegate: Recycler.Delegate<Route.Ground>
    ): Recycler.ViewHolder<Route.Ground> {
        return GroundViewHolder(itemView, delegate)
    }
}

class GroundViewHolder(itemView: View, delegate: Recycler.Delegate<Route.Ground>) :
    Recycler.ViewHolder<Route.Ground>(itemView, delegate) {

    private val textView: TextView = itemView as TextView

    override fun bind(item: Route.Ground, selected: Boolean) {
        textView.setBackgroundResource(if (selected) R.drawable.bg_selected_item else R.drawable.bg_select_item)
        val text = item.name(context)
        textView.text = text
    }
}