package me.kolotilov.lets_a_go.ui.base

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import me.kolotilov.lets_a_go.R

class KeyValueModel(
    val key: String,
    val value: String
) : Grid.ViewModel

fun Pair<String, String?>.toKeyValueModel() = KeyValueModel(
    key = first,
    value = second!!
)

class KeyValueFactory : Grid.Factory {

    override fun holder(
        item: Grid.ViewModel,
        inflater: LayoutInflater,
        parent: ViewGroup
    ): Grid.ViewHolder<*> {
        val key = inflater.inflate(R.layout.key_item, parent, false) as TextView
        val value = inflater.inflate(R.layout.value_item, parent, false) as TextView
        return KeyValueViewHolder(key, value)
    }
}

class KeyValueViewHolder(
    private val keyView: TextView,
    private val valueView: TextView
) : Grid.ViewHolder<KeyValueModel>(listOf(keyView, valueView)) {

    override fun bind(element: KeyValueModel) {
        keyView.text = element.key
        valueView.text = element.value
    }
}