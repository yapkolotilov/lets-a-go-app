package me.kolotilov.lets_a_go.presentation

import me.kolotilov.lets_a_go.models.Point

class Params {

    class EditRoute {

        var points = emptyList<Point>()
        var id: Int? = null

        fun clear() {

            points = emptyList()
            id = null
        }
    }

    class RouteDetails {
        var id: Int? = null

        fun clear() {
            id = null
        }
    }

    var editRoute = EditRoute()
    val routeDetails = RouteDetails()

    fun clear() {
        editRoute.clear()
        routeDetails.clear()
    }
}