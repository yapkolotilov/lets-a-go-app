package me.kolotilov.lets_a_go.ui.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.recyclerview.widget.RecyclerView


/**
 * Утилитный класс для работы с [GridLayout]
 */
class Grid {

    /**
     * Адаптер для работы с GridLayout. Аналог [RecyclerView.Adapter].
     * Нужен там, где необходимо выравнивание элементов списка по вертикали.
     */
    abstract class Adapter(private val gridLayout: GridLayout) {

        /**
         * Список элементов.
         */
        var items: List<ViewModel> = listOf()
            set(value) {
                val previousValue = field
                field = value
                if (previousValue != value)
                    notifyDataSetChanged()
            }

        /**
         * Аналог onCreateViewHolder из адаптера для ресайклера. Аналог [RecyclerView.Adapter.onCreateViewHolder].
         */
        abstract fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder<ViewModel>

        /**
         * Перерисовывает GridLayout. Аналог [RecyclerView.Adapter.notifyDataSetChanged]
         */
        fun notifyDataSetChanged() {
            val gridLayout = gridLayout
            gridLayout.removeAllViews()
            for (i in items.indices) {
                val holder = onCreateViewHolder(gridLayout, i)
                for (view in holder.itemViews)
                    gridLayout.addView(view)
                holder.bind(items[i])
            }
        }
    }

    interface ViewModel

    /**
     * Вьюхолдер для GridLayout. Группирует сразу несколько View. Аналог [RecyclerView.ViewHolder].
     *
     * @param itemViews Вьюхи.
     */
    abstract class ViewHolder<T>(
        internal val itemViews: List<View>
    ) {

        constructor(vararg itemView: View) : this(itemView.toList())

        /**
         * Наполняет значениями вьюхи.
         *
         * @param item Элемент списка.
         */
        abstract fun bind(element: T)
    }

    /**
     * Вьюхолдер, который ничего не делает.
     */
    class StaticViewHolder(view: View) : ViewHolder<ViewModel>(listOf(view)) {

        override fun bind(element: ViewModel) = Unit
    }

    interface Factory {

        fun holder(
            item: ViewModel,
            inflater: LayoutInflater,
            parent: ViewGroup
        ): ViewHolder<*>
    }

    class ListAdapter(
        gridLayout: GridLayout,
        private val factory: Factory
    ) : Adapter(gridLayout) {

        override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder<ViewModel> {
            return factory.holder(items[position], LayoutInflater.from(parent.context), parent) as ViewHolder<ViewModel>
        }
    }
}

