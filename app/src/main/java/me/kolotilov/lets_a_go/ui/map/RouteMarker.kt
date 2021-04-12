package me.kolotilov.lets_a_go.ui.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import me.kolotilov.lets_a_go.R
import me.kolotilov.lets_a_go.models.RoutePoint
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
        markerOptions.icon(bitmapDescriptorFromVector(context, item.route.type.mapIcon()))
    }

    override fun getColor(clusterSize: Int): Int {
        return context.getColorCompat(R.color.blue_primary)
    }

    @Suppress("SameParameterValue")
    private fun bitmapDescriptorFromVector(
        context: Context,
        @DrawableRes vectorDrawableResourceId: Int
    ): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId)
        val bitmap = Bitmap.createBitmap(
            vectorDrawable!!.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable.draw(canvas)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}