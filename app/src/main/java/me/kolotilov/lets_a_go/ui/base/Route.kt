package me.kolotilov.lets_a_go.ui.base

import android.view.View
import android.widget.TextView
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.models.RouteItem
import me.kolotilov.lets_a_go.ui.context
import me.kolotilov.lets_a_go.ui.distance
import me.kolotilov.lets_a_go.ui.duration
import me.kolotilov.lets_a_go.ui.name

class RouteFactory : Recycler.Factory<RouteItem> {

    override fun getType(item: RouteItem): Int = R.layout.route_item

    override fun getViewHolder(
        type: Int,
        itemView: View,
        delegate: Recycler.Delegate<RouteItem>
    ): Recycler.ViewHolder<RouteItem> {
        return RouteViewHolder(itemView, delegate)
    }
}

class RouteViewHolder(itemView: View, delegate: Recycler.Delegate<RouteItem>) :
    Recycler.ViewHolder<RouteItem>(itemView, delegate) {

    private val nameTextView: TextView = itemView.findViewById(R.id.name_text_view)
    private val distanceAndDurationTextView: TextView =
        itemView.findViewById(R.id.distance_and_duration_text_view)
    private val typeAndGroundTextView: TextView =
        itemView.findViewById(R.id.type_and_ground_text_view)
    private val distanceToRouteTextView: TextView =
        itemView.findViewById(R.id.distance_to_route_text_view)

    override fun bind(item: RouteItem, selected: Boolean) {
        nameTextView.text = item.name ?: context.getString(R.string.route_title)
        distanceAndDurationTextView.text =
            listOf(item.distance.distance(context), item.duration.duration()).joinToString()
        typeAndGroundTextView.text =
            listOfNotNull(item.type?.name(context), item.ground?.name(context)).joinToString()
        distanceToRouteTextView.text = item.distanceToRoute?.let {
            context.getString(R.string.distance_to_route, it.distance(context))
        }
    }
}