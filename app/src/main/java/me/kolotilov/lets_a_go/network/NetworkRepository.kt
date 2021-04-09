package me.kolotilov.lets_a_go.network

import io.reactivex.Completable
import io.reactivex.Single
import me.kolotilov.lets_a_go.models.*
import me.kolotilov.lets_a_go.network.input.*
import me.kolotilov.lets_a_go.network.output.toEntry
import me.kolotilov.lets_a_go.network.output.toRoute
import me.kolotilov.lets_a_go.network.output.toRoutePreview
import me.kolotilov.lets_a_go.network.output.toUserDetails
import okhttp3.ResponseBody
import org.joda.time.DateTime
import retrofit2.Converter
import retrofit2.HttpException
import retrofit2.Retrofit

interface NetworkRepository {

    fun register(email: String, password: String): Completable

    fun login(email: String, password: String): Completable

    fun getDetails(): Single<UserDetails>

    fun editDetails(
        name: String? = null,
        birthDate: DateTime? = null,
        height: Int? = null,
        weight: Int? = null,
        illnesses: List<String>? = null,
        symptoms: List<String>? = null,
        filter: Filter? = null,
        updateFilter: Boolean = false
    ): Single<UserDetails>

    fun changePassword(password: String): Single<UserDetails>

    fun getIllnessesAndSymptoms(): Single<IllnessesAndSymptoms>

    fun createRoute(name: String?, type: Route.Type?, ground: Route.Ground?, points: List<Point>): Single<Route>

    fun getRoute(id: Int): Single<Route>

    fun editRoute(id: Int, route: Route): Single<Route>

    fun deleteRoute(id: Int): Completable

    fun getAllRoutes(filter: Boolean): Single<List<Route>>

    fun getEntries(): Single<List<Entry>>

    fun createEntry(route: Route, entry: Entry): Single<Route>

    fun searchRoutes(query: String?, filter: Filter?): Single<List<Route>>

    fun routePreview(points: List<Point>): Single<RoutePreview>
}

class NetworkRepositoryImpl(
    private val api: LetsAGoApi,
    private val localRepository: LocalRepository,
    private val retrofit: Retrofit
) : NetworkRepository {

    override fun register(email: String, password: String): Completable {
        return api.register(LoginDto(username = email, password = password))
            .parseError()
    }

    override fun login(email: String, password: String): Completable {
        return api.login(LoginDto(username = email, password = password))
            .doOnSuccess {
                localRepository.token = it.token
            }
            .ignoreElement()
            .parseError()
    }

    override fun getDetails(): Single<UserDetails> {
        return api.getDetails()
            .map { it.toUserDetails() }
            .parseError()
    }

    override fun editDetails(
        name: String?,
        birthDate: DateTime?,
        height: Int?,
        weight: Int?,
        illnesses: List<String>?,
        symptoms: List<String>?,
        filter: Filter?,
        updateFilter: Boolean
    ): Single<UserDetails> {
        return api.editDetails(
            EditDetailsDto(
                name,
                birthDate?.toDate(),
                height,
                weight,
                illnesses,
                symptoms,
                filter?.toFilterDto(),
                updateFilter
            )
        ).map { it.toUserDetails() }
            .parseError()
    }

    override fun changePassword(password: String): Single<UserDetails> {
        return api.changePassword(ChangePasswordDto(password)).map { it.toUserDetails() }
            .parseError()
    }

    override fun getIllnessesAndSymptoms(): Single<IllnessesAndSymptoms> {
        return Single.zip(
            api.getIllnesses(),
            api.getSymptoms()
        ) { t1, t2 -> IllnessesAndSymptoms(t1, t2) }
            .parseError()
    }

    override fun createRoute(
        name: String?,
        type: Route.Type?,
        ground: Route.Ground?,
        points: List<Point>
    ): Single<Route> {
        val createRoute = CreateRouteDto(
            name = name,
            difficulty = null,
            type = type,
            ground = ground,
            points = points.map { it.toCreatePointDto() }
        )
        return api.createRoute(createRoute).map { it.toRoute() }
    }

    override fun getRoute(id: Int): Single<Route> {
        return api.getRoute(id).map { it.toRoute() }
    }

    override fun editRoute(id: Int, route: Route): Single<Route> {
        return api.editRoute(id, route.toEditRouteDto()).map { it.toRoute() }
    }

    override fun deleteRoute(id: Int): Completable {
        return api.deleteRoute(id)
    }

    override fun getAllRoutes(filter: Boolean): Single<List<Route>> {
        return api.getRoutes(filter).map { routes -> routes.map { it.toRoute() } }
    }

    override fun getEntries(): Single<List<Entry>> {
        return api.getEntries().map { entries -> entries.map { it.toEntry() } }
    }

    override fun createEntry(route: Route, entry: Entry): Single<Route> {
        return api.createEntry(route.id, entry.toCreateEntryDto()).map { it.toRoute()} }

    override fun searchRoutes(query: String?, filter: Filter?): Single<List<Route>> {
        return api.searchRoutes(query, filter?.toFilterDto()).map { routes -> routes.map { it.toRoute() } }
    }

    override fun routePreview(points: List<Point>): Single<RoutePreview> {
        return api.routePreview(CreateRoutePreviewDto(points.map { it.toCreatePointDto() }))
            .map { it.toRoutePreview() }
    }

    private fun parseError(throwable: Throwable): Throwable {
        val converter: Converter<ResponseBody, ErrorDto> =
            retrofit.responseBodyConverter(ErrorDto::class.java, emptyArray())

        if (throwable is HttpException) runCatching {
            val error = throwable.response()?.errorBody()?.let { converter.convert(it) }
            if (error != null)
                return error.toServiceException()
        }
        if (throwable is retrofit2.adapter.rxjava2.HttpException) runCatching {
            val error = throwable.response()?.errorBody()?.let { converter.convert(it) }
            if (error != null)
                return error.toServiceException()
        }
        return throwable
    }

    private fun Completable.parseError(): Completable {
        return onErrorResumeNext { Completable.error(parseError(it)) }
    }

    private fun <T> Single<T>.parseError(): Single<T> {
        return onErrorResumeNext { Single.error(parseError(it)) }
    }
}