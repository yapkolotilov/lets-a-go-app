package me.kolotilov.lets_a_go.ui.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.kolotilov.lets_a_go.ui.context
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.di

class Recycler {

    open class Adapter<T>(
        protected val factory: Factory<T>,
        protected val delegate: Delegate<T> = object : Delegate<T> {}
    ) : RecyclerView.Adapter<ViewHolder<T>>() {

        var enabled: Boolean = true

        var items: List<T> = emptyList()
            set(value) {
                field = value
                notifyDataSetChanged()
            }

        var selectedItems: Set<T> = emptySet()
            set(value) {
                field = value
                notifyDataSetChanged()
            }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<T> {
            val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
            return factory.getViewHolder(viewType, view, delegate)
        }

        override fun onBindViewHolder(holder: ViewHolder<T>, position: Int) {
            val item = items[position]
            holder.itemView.setOnClickListener {
                if (!enabled)
                    return@setOnClickListener
                delegate.onClick(item)
                notifyDataSetChanged()
            }
            holder.bind(item, selectedItems.contains(item))
        }

        override fun getItemCount(): Int = items.size

        override fun getItemViewType(position: Int): Int {
            return factory.getType(items[position])
        }
    }

    class SelectAdapter<T>(
        factory: Factory<T>,
        delegate: Delegate<T> = object : Delegate<T> {},
    ) : Adapter<T>(factory, delegate) {

        var selectedItem: T? = null
            set(value) {
                if (field == value)
                    return
                field = value
                onItemSelected(selectedItem)
                notifyDataSetChanged()
            }
        private var onItemSelected: ((T?) -> Unit) = {}

        fun setOnItemSelectedListener(listener: (T?) -> Unit) {
            onItemSelected = listener
        }

        override fun onBindViewHolder(holder: ViewHolder<T>, position: Int) {
            val item = items[position]
            holder.itemView.setOnClickListener {
                if (!enabled)
                    return@setOnClickListener
                selectedItem = if (selectedItem == item) null else item
                onItemSelected(selectedItem)
                notifyDataSetChanged()
            }
            holder.bind(item, item == selectedItem)
        }
    }

    abstract class ViewHolder<T>(itemView: View, protected val delegate: Delegate<T>) :
        RecyclerView.ViewHolder(itemView), DIAware {

        override val di: DI by di { context }

        open fun bind(item: T, selected: Boolean) {}
    }

    interface Factory<T> {

        fun getType(item: T): Int

        fun getViewHolder(type: Int, itemView: View, delegate: Delegate<T>): Recycler.ViewHolder<T>
    }

    class SimpleFactory<T>(
        private val type: Int,
        private val viewHolderFactory: (View, Delegate<T>) -> ViewHolder<T>
    ) : Factory<T> {

        override fun getType(item: T): Int = type

        override fun getViewHolder(
            type: Int,
            itemView: View,
            delegate: Delegate<T>
        ): ViewHolder<T> = viewHolderFactory(itemView, delegate)
    }

    interface Delegate<T> {

        fun onClick(item: T) = Unit
    }
}