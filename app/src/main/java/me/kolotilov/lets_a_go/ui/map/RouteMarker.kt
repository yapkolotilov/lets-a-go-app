package me.kolotilov.lets_a_go.ui.map

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.models.RoutePoint
import me.kolotilov.lets_a_go.ui.bitmapDescriptorFromVector
import me.kolotilov.lets_a_go.ui.getColorCompat
import me.kolotilov.lets_a_go.ui.mapIcon
import me.kolotilov.lets_a_go.ui.toLatLng

data class RouteMarker(
    val route: RoutePoint
) : ClusterItem {

    override fun getPosition(): LatLng {
        return route.startPoint.toLatLng()
    }

    override fun getTitle(): String? = null

    override fun getSnippet(): String? = null
}

class RouteRenderer(
    private val context: Context,
    map: GoogleMap,
    clusterManager: ClusterManager<RouteMarker>
) :
    DefaultClusterRenderer<RouteMarker>(context, map, clusterManager) {

    override fun shouldRenderAsCluster(cluster: Cluster<RouteMarker>): Boolean {
        return cluster.size > 1
    }

    override fun onBeforeClusterItemRendered(item: RouteMarker, markerOptions: MarkerOptions) {
        markerOptions.icon(context.bitmapDescriptorFromVector(item.route.type.mapIcon()))
    }

    override fun getColor(clusterSize: Int): Int {
        return context.getColorCompat(R.color.blue_primary)
    }
}