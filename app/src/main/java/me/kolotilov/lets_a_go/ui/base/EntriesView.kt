package me.kolotilov.lets_a_go.ui.base

import android.content.Context
import android.util.AttributeSet
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.models.RouteEntry
import me.kolotilov.lets_a_go.ui.distance
import me.kolotilov.lets_a_go.ui.getString
import me.kolotilov.lets_a_go.ui.kilocalories

class EntriesView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private companion object {

        const val MAX_ENTRIES = 3
    }

    init {
        inflate(context, R.layout.view_entries, this)
    }

    private val statsGrid: GridLayout = findViewById(R.id.stats_grid)
    private val showAllTextView: TextView = findViewById(R.id.show_all_text_view)
    private val entriesRecycler: RecyclerView = findViewById(R.id.entries_recycler)

    private val statsAdapter = Grid.ListAdapter(statsGrid, KeyValueFactory())
    private val entriesAdapter =
        Recycler.Adapter(EntryFactory(), object : Recycler.Delegate<RouteEntry> {

            override fun onClick(item: RouteEntry) {
                onEntryClickListener(item)
            }
        })

    private var onEntryClickListener: (RouteEntry) -> Unit = {}
    private var entriesCache: List<RouteEntry> = emptyList()
    private var expanded: Boolean = false

    init {
        entriesRecycler.adapter = entriesAdapter

        showAllTextView.setOnClickListener {
            switchExpand(!expanded)
        }
    }

    fun setOnEntryClickListener(listener: (RouteEntry) -> Unit) {
        onEntryClickListener = listener
    }

    fun setItems(
        totalDistance: Double,
        totalKilocaloriesBurnt: Int?,
        entries: List<RouteEntry>
    ) {
        statsAdapter.items = listOfNotNull(
            entries.size.takeIf { it > 0 }?.let {
                KeyValueModel(
                    context.getString(R.string.entries_count_hint),
                    it.toString()
                )
            },
            KeyValueModel(getString(R.string.distance_hint), totalDistance.distance(context)),
            totalKilocaloriesBurnt?.let {
                KeyValueModel(
                    getString(R.string.kilocalories_burnt_hint),
                    it.kilocalories(context)
                )
            }
        )
        val count = entries.size
        switchExpand(true)
        showAllTextView.isVisible = count > MAX_ENTRIES
        entriesCache = entries
        entriesAdapter.items = entries.take(MAX_ENTRIES)
    }

    private fun switchExpand(expanded: Boolean) {
        this.expanded = expanded
        if (!expanded) {
            entriesAdapter.items = entriesCache
            showAllTextView.text = getString(R.string.collapse_button)
        } else {
            entriesAdapter.items = entriesCache.take(MAX_ENTRIES)
            val count = entriesCache.size
            showAllTextView.text = context.getString(R.string.show_all, count)
        }
    }
}