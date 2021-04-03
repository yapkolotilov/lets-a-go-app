package me.kolotilov.lets_a_go.ui.details.user

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.ui.base.Grid

class BaseDetailsFactory : Grid.Factory {

    override fun holder(
        item: Grid.ViewModel,
        inflater: LayoutInflater,
        parent: ViewGroup
    ): Grid.ViewHolder<*> {
        val keyView = inflater.inflate(R.layout.base_details_key_item, parent, false) as TextView
        val valueView = inflater.inflate(R.layout.base_details_value_item, parent, false) as TextView
        return BaseDetailsViewHolder(keyView, valueView)
    }
}