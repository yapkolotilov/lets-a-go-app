package me.kolotilov.lets_a_go.presentation.map

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import me.kolotilov.lets_a_go.models.*
import me.kolotilov.lets_a_go.network.Repository
import me.kolotilov.lets_a_go.presentation.BaseViewModel
import me.kolotilov.lets_a_go.presentation.Params
import me.kolotilov.lets_a_go.presentation.Screens
import me.kolotilov.lets_a_go.ui.base.setResultListener
import me.kolotilov.lets_a_go.ui.toEditRouteParams
import ru.terrakok.cicerone.Router

class MapViewModel(
    private val repository: Repository,
    private val router: Router,
    private val params: Params
) : BaseViewModel() {

    val routes: Observable<List<Route>> get() = routesSubject
    private val routesSubject = BehaviorSubject.create<List<Route>>()

    val startEntry: Observable<Unit> get() = startEntrySubject
    private val startEntrySubject = PublishSubject.create<Unit>()

    val errorDialog: Observable<ErrorCode> get() = errorDialogSubject
    private val errorDialogSubject: Subject<ErrorCode> = BehaviorSubject.create()

    val clearEntry: Observable<Unit> get() = clearEntrySubject
    private val clearEntrySubject: Subject<Unit> = PublishSubject.create()

    override fun attach() {
        loadRoutes()
    }

    fun openRoutePreview(recordedPoints: MutableList<Point>) {
        repository.routePreview(recordedPoints)
            .load()
            .doOnSuccess {
                router.setResultListener<Unit>(EditRouteViewModel.TAG) {
                    loadRoutes()
                    clearEntrySubject.onNext(Unit)
                }
                router.navigateTo(Screens.editRoute(it.toEditRouteParams(recordedPoints)))
            }
            .doOnError {
                if (it is ServiceException)
                    errorDialogSubject.onNext(it.code)
            }
            .emptySubscribe()
            .autoDispose()
    }

    fun openEditEntryBottomSheet(route: Route?, recordedPoints: MutableList<Point>, callback: () -> Unit) {
        params.editEntry.route = route
        params.editEntry.entry = Entry(recordedPoints, -1)
        params.editRoute.callback = {
            loadRoutes()
            callback()
        }
        router.navigateTo(Screens.editEntry())
    }

    fun openRouteDetailsBottomSheet(route: Route) {
        params.routeDetails.id = route.id
        params.routeDetails.callback = {
            loadRoutes()
            if (it)
                startEntrySubject.onNext(Unit)
        }
        router.navigateTo(Screens.routeDetails())
    }

    private fun loadRoutes() {
        repository.getAllRoutes(false) // TODO: Убрать заглушку
            .load()
            .doOnSuccess {
                routesSubject.onNext(it)
            }
            .emptySubscribe()
            .autoDispose()
    }

    fun openUserDetails() {
        router.navigateTo(Screens.userDetails())
    }
}