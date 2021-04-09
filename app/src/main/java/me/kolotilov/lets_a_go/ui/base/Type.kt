package me.kolotilov.lets_a_go.ui.base

import android.view.View
import android.widget.ImageView
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.models.Route
import me.kolotilov.lets_a_go.ui.icon

class TypeFactory : Recycler.Factory<Route.Type> {

    override fun getType(item: Route.Type): Int = R.layout.route_type

    override fun getViewHolder(
        type: Int,
        itemView: View,
        delegate: Recycler.Delegate<Route.Type>
    ): Recycler.ViewHolder<Route.Type> {
        return TypeViewHolder(itemView, delegate)
    }
}

class TypeViewHolder(itemView: View, delegate: Recycler.Delegate<Route.Type>) :
    Recycler.ViewHolder<Route.Type>(itemView, delegate) {

    private val iconView: ImageView = itemView as ImageView

    override fun bind(item: Route.Type, selected: Boolean) {
        iconView.setBackgroundResource(if (selected) R.drawable.bg_selected_item else R.drawable.bg_select_item)
        val icon = item.icon()
        iconView.setImageResource(icon)
    }
}