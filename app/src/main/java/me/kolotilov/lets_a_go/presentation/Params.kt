package me.kolotilov.lets_a_go.presentation

import me.kolotilov.lets_a_go.models.Entry
import me.kolotilov.lets_a_go.models.Point
import me.kolotilov.lets_a_go.models.Route

class Params {

    class EditRoute {

        var callback = {}
        var points = emptyList<Point>()
        var id: Int? = null

        fun clear() {
            callback = {}
            points = emptyList()
            id = null
        }
    }

    class RouteDetails {

        var id: Int? = null
        var callback: (Boolean) -> Unit = {}

        fun clear() {
            id = null
            callback = {}
        }
    }

    class EditEntry {

        var route: Route? = null
        var entry = Entry(emptyList(), -1)
        var callback = {}

        fun clear() {
            route = null
            callback = {}
            entry = Entry(emptyList(), -1)
        }
    }

    class EntryDetails {

        var entry = Entry(emptyList(), -1)
        var route: Route? = null

        fun clear() {
            entry = Entry(emptyList(), -1)
            route = null
        }
    }

    val editRoute = EditRoute()
    val routeDetails = RouteDetails()
    val editEntry = EditEntry()
    val entryDetails = EntryDetails()

    fun clear() {
        editRoute.clear()
        routeDetails.clear()
        editEntry.clear()
        entryDetails.clear()
    }
}