package me.kolotilov.lets_a_go.presentation.map

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.GridLayout
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.presentation.Tags
import me.kolotilov.lets_a_go.ui.*
import me.kolotilov.lets_a_go.ui.base.Grid
import me.kolotilov.lets_a_go.ui.base.InfoView
import me.kolotilov.lets_a_go.ui.base.KeyValueFactory
import me.kolotilov.lets_a_go.ui.base.toKeyValueModel
import org.joda.time.DateTime
import org.joda.time.Duration
import org.joda.time.format.DateTimeFormatter
import org.kodein.di.instance

class EntryStatsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : InfoView<EntryStatsView.Data>(context, attrs, R.layout.view_route_stats) {

    data class Data(
        val distance: Double,
        val duration: Duration,
        val speed: Double,
        val kiloCaloriesBurnt: Int?,
        val altitudeDelta: Double,
        val date: DateTime?,
    )

    override val cardView: View = findViewById(R.id.card_view)
    override val loadingLayout: View = findViewById(R.id.loading_layout)
    override val loadedLayout: View = findViewById(R.id.loaded_layout)
    override val emptyLayout: View = findViewById(R.id.empty_layout)
    private val statsGrid: GridLayout = findViewById(R.id.stats_grid)

    private val statsAdapter = Grid.ListAdapter(statsGrid, KeyValueFactory())
    private val dateFormatter: DateTimeFormatter by instance(Tags.ENTRY_DATE)

    fun setData(
        distance: Double,
        duration: Duration,
        speed: Double,
        kiloCaloriesBurnt: Int?,
        altitudeDelta: Double,
        date: DateTime?,
    ) {
        setData(Data(distance, duration, speed, kiloCaloriesBurnt, altitudeDelta, date))
    }

    override fun fillData(data: Data) {
        statsAdapter.items = listOf(
            context.getString(R.string.date_hint) to data.date?.let { dateFormatter.print(it.millis) },
            getString(R.string.distance_hint) to data.distance.distance(context),
            getString(R.string.duration_hint) to data.duration.duration(),
            getString(R.string.speed_hint) to data.speed.speed(context),
            getString(R.string.kilocalories_burnt_hint) to (data.kiloCaloriesBurnt?.kilocalories(
                context
            ) ?: context.getString(R.string.fill_basic_info)),
            getString(R.string.altitude_delta_hint) to data.altitudeDelta.distance(context),

        ).filter { it.second != null }.map { it.toKeyValueModel() }
    }
}