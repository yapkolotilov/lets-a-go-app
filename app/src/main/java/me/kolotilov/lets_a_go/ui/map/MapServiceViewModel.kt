package me.kolotilov.lets_a_go.ui.map

import me.kolotilov.lets_a_go.models.Point
import me.kolotilov.lets_a_go.presentation.BaseViewModel
import me.kolotilov.lets_a_go.presentation.Constants
import me.kolotilov.lets_a_go.presentation.base.NotificationService

class MapServiceViewModel(
    private val notificationService: NotificationService
) : BaseViewModel() {

    enum class Type {

        ROUTING,
        ENTRYING
    }

    private var routeId: Int = 0
    private var routeName: String? = null
    private var routePoints: List<Point> = emptyList()
    private var type: Type = Type.ROUTING
    private var recordedPoints: MutableList<Point> = mutableListOf()

    fun proceedRecordingData(data: RecordingData) {
        when(data) {
            is RecordingData.Routing -> {
                type = Type.ROUTING
                this.recordedPoints = data.points.toMutableList()
            }
            is RecordingData.Entrying -> {
                type = Type.ENTRYING
                this.routeId = data.routeId
                this.routeName = data.routeName
                this.routePoints = data.routePoints
                this.recordedPoints = data.points.toMutableList()
            }
        }
    }

    fun getRecordingData(): RecordingData {
        return when(type) {
            Type.ROUTING -> {
                RecordingData.Routing(
                    points = recordedPoints
                )
            }
            Type.ENTRYING -> {
                RecordingData.Entrying(
                    routeId = routeId,
                    routeName = routeName,
                    routePoints = routePoints,
                    points = recordedPoints
                )
            }
        }
    }

    fun onLocationUpdate(location: Point) {
        recordedPoints.add(location)
        if (type == Type.ENTRYING) {
            val distance = routePoints.minOf { it distance location }
            if (distance > Constants.MIN_DISTANCE_TO_ROUTE)
                notificationService.showStickToRouteNotification()
            else
                notificationService.hideStickToRouteNotification()
        }
    }
}