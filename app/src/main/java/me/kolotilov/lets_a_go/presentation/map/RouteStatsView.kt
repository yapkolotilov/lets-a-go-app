package me.kolotilov.lets_a_go.presentation.map

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.GridLayout
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.ui.*
import me.kolotilov.lets_a_go.ui.base.Grid
import me.kolotilov.lets_a_go.ui.base.InfoView
import me.kolotilov.lets_a_go.ui.base.KeyValueFactory
import me.kolotilov.lets_a_go.ui.base.toKeyValueModel
import org.joda.time.Duration

class RouteStatsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : InfoView<RouteStatsView.Data>(context, attrs, R.layout.view_route_stats) {

    data class Data(
        val distance: Double,
        val duration: Duration,
        val speed: Double,
        val kiloCaloriesBurnt: Int?,
        val altitudeDelta: Double,
    )

    override val cardView: View = findViewById(R.id.card_view)
    override val loadingLayout: View = findViewById(R.id.loading_layout)
    override val loadedLayout: View = findViewById(R.id.loaded_layout)
    override val emptyLayout: View = findViewById(R.id.empty_layout)
    private val statsGrid: GridLayout = findViewById(R.id.stats_grid)

    private val statsAdapter = Grid.ListAdapter(statsGrid, KeyValueFactory())

    fun setData(
        distance: Double,
        duration: Duration,
        speed: Double,
        kiloCaloriesBurnt: Int?,
        altitudeDelta: Double,
    ) {
        setData(Data(distance, duration, speed, kiloCaloriesBurnt, altitudeDelta))
    }

    override fun fillData(data: Data) {
        statsAdapter.items = listOf(
            "" to null,
            getString(R.string.distance_hint) to data.distance.distance(context),
            getString(R.string.duration_hint) to data.duration.duration(context),
            getString(R.string.speed_hint) to data.speed.speed(context),
            getString(R.string.kilocalories_burnt_hint) to data.kiloCaloriesBurnt?.kilocalories(
                context
            ),
            getString(R.string.altitude_delta_hint) to data.altitudeDelta.distance(context)
        ).filter { it.second != null }.map { it.toKeyValueModel() }
    }
}