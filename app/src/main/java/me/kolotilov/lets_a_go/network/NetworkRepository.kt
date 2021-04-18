package me.kolotilov.lets_a_go.network

import io.reactivex.Completable
import io.reactivex.Single
import me.kolotilov.lets_a_go.models.*
import me.kolotilov.lets_a_go.network.input.*
import me.kolotilov.lets_a_go.network.output.*
import okhttp3.ResponseBody
import org.joda.time.DateTime
import retrofit2.Converter
import retrofit2.HttpException
import retrofit2.Retrofit

interface NetworkRepository {

    fun register(email: String, password: String): Completable

    fun login(email: String, password: String): Completable

    fun getDetails(location: Point? = null): Single<UserDetails>

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

    fun createRoute(
        name: String?,
        type: Route.Type?,
        difficulty: Int,
        ground: Route.Ground?,
        isPublic: Boolean,
        points: List<Point>
    ): Single<RouteDetails>

    fun getRoute(id: Int): Single<RouteDetails>

    fun editRoute(
        id: Int,
        name: String?,
        difficulty: Int?,
        type: Route.Type?,
        ground: Route.Ground?
    ): Single<RouteDetails>

    fun deleteRoute(id: Int): Completable

    fun getAllRoutes(filter: Boolean): Single<List<RoutePoint>>

    fun startEntry(routeId: Int, location: Point): Single<StartEntry>

    fun createEntry(routeId: Int, points: List<Point>): Single<RouteDetails>

    fun searchRoutes(name: String?, filter: Filter?, location: Point?): Single<List<RouteItem>>

    fun routePreview(points: List<Point>): Single<RoutePreview>

    fun entryPreview(routeId: Int, points: List<Point>): Single<EntryPreview>

    fun getEntry(id: Int): Single<EntryDetails>

    fun getRouteOnMap(id: Int): Single<RouteLine>
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

    override fun getDetails(location: Point?): Single<UserDetails> {
        return api.getDetails(DetailsDto(location?.toCreatePointDto()))
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
        difficulty: Int,
        ground: Route.Ground?,
        isPublic: Boolean,
        points: List<Point>
    ): Single<RouteDetails> {
        val createRoute = CreateRouteDto(
            name = name,
            difficulty = difficulty,
            type = type,
            ground = ground,
            isPublic = isPublic,
            points = points.map { it.toCreatePointDto() }
        )
        return api
            .createRoute(createRoute)
            .map { it.toRouteDetails() }
            .parseError()
    }

    override fun getRoute(id: Int): Single<RouteDetails> {
        return api
            .getRoute(id).map { it.toRouteDetails() }
            .parseError()
    }

    override fun editRoute(
        id: Int,
        name: String?,
        difficulty: Int?,
        type: Route.Type?,
        ground: Route.Ground?
    ): Single<RouteDetails> {
        return api.editRoute(
            id, EditRouteDto(
                name = name,
                difficulty = difficulty,
                type = type,
                ground = ground
            )
        ).map { it.toRouteDetails() }
            .parseError()
    }

    override fun deleteRoute(id: Int): Completable {
        return api.deleteRoute(id)
            .parseError()
    }

    override fun getAllRoutes(filter: Boolean): Single<List<RoutePoint>> {
        return api.getRoutes(filter)
            .map { routes -> routes.map { it.toRoutePoint() } }
            .parseError()
    }

    override fun startEntry(routeId: Int, location: Point): Single<StartEntry> {
        return api.startEntry(routeId, location.toCreatePointDto())
            .map { it.toStartEntry() }
            .parseError()
    }

    override fun createEntry(routeId: Int, points: List<Point>): Single<RouteDetails> {
        return api.createEntry(routeId, CreateEntryDto(points.map { it.toCreatePointDto() }))
            .map { it.toRouteDetails() }
            .parseError()
    }

    override fun searchRoutes(name: String?, filter: Filter?, location: Point?): Single<List<RouteItem>> {
        val queryDto = SearchRoutesDto(
            name = name,
            filter = filter?.toFilterDto(),
            userLocation = location?.toCreatePointDto()
        )
        return api.searchRoutes(queryDto)
            .map { routes -> routes.map { it.toRouteItem() } }
            .parseError()
    }

    override fun routePreview(points: List<Point>): Single<RoutePreview> {
        return api.routePreview(CreateRoutePreviewDto(points.map { it.toCreatePointDto() }))
            .map { it.toRoutePreview() }
            .parseError()
    }

    override fun entryPreview(routeId: Int, points: List<Point>): Single<EntryPreview> {
        return api.entryPreview(
            CreateEntryPreviewDto(
                routeId,
                points.map { it.toCreatePointDto() })
        )
            .map { it.toEntryPreview() }
            .parseError()
    }

    override fun getEntry(id: Int): Single<EntryDetails> {
        return api.getEntry(id)
            .map { it.toEntryDetails() }
            .parseError()
    }

    override fun getRouteOnMap(id: Int): Single<RouteLine> {
        return api.getRouteOnMap(id)
            .map { it.toRouteLine() }
            .parseError()
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