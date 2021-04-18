package me.kolotilov.lets_a_go.ui.details

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.models.RouteItem
import me.kolotilov.lets_a_go.ui.base.Recycler
import me.kolotilov.lets_a_go.ui.base.RouteFactory
import me.kolotilov.lets_a_go.ui.getString

class RoutesView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private companion object {

        const val MAX_ROUTES = 3
    }

    init {
        inflate(context, R.layout.view_routes, this)
    }

    private val showAllButton: TextView = findViewById(R.id.show_all_text_view)
    private val routesRecycler: RecyclerView = findViewById(R.id.routes_recycler)

    private var expanded: Boolean = false
    private var routesCache: List<RouteItem> = emptyList()
    private var onRouteItemListener: (RouteItem) -> Unit = {}
    private val routesAdapter: Recycler.Adapter<RouteItem> = Recycler.Adapter(RouteFactory(), object : Recycler.Delegate<RouteItem> {

        override fun onClick(item: RouteItem) {
            onRouteItemListener(item)
        }
    })

    init {
        routesRecycler.adapter = routesAdapter
        showAllButton.setOnClickListener { switchExpand(!expanded) }
    }

    fun setOnRouteClickListener(listener: (RouteItem) -> Unit) {
        onRouteItemListener = listener
    }

    fun setItems(routes: List<RouteItem>) {
        val count = routes.size
        showAllButton.isVisible = count > MAX_ROUTES
        routesCache = routes
        routesAdapter.items = routes.take(MAX_ROUTES)
        switchExpand(true)
    }

    private fun switchExpand(expanded: Boolean) {
        this.expanded = expanded
        if (!expanded) {
            routesAdapter.items = routesCache
            showAllButton.text = getString(R.string.collapse_button)
        } else {
            routesAdapter.items = routesCache.take(MAX_ROUTES)
            val count = routesCache.size
            showAllButton.text = context.getString(R.string.show_all, count)
        }
    }
}