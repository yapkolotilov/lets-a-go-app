package me.kolotilov.lets_a_go.network

import io.reactivex.Completable
import io.reactivex.Single
import me.kolotilov.lets_a_go.network.input.*
import me.kolotilov.lets_a_go.network.output.*
import retrofit2.http.*

private const val AUTH = "auth"
private const val DETAILS = "details"
private const val MAP = "map"

interface LetsAGoApi {

    @POST("/$AUTH/register")
    fun register(@Body loginDto: LoginDto): Completable

    @POST("/$AUTH/login")
    fun login(@Body loginDto: LoginDto): Single<TokenDto>

    @POST("/$DETAILS")
    fun getDetails(@Body location: DetailsDto): Single<UserDetailsDto>

    @POST("/$DETAILS/edit")
    fun editDetails(@Body editDetailsDto: EditDetailsDto): Single<UserDetailsDto>

    @POST("/$DETAILS/change_password")
    fun changePassword(@Body changePasswordDto: ChangePasswordDto): Single<UserDetailsDto>

    @GET("/$DETAILS/illnesses")
    fun getIllnesses(): Single<List<String>>

    @GET("/$DETAILS/symptoms")
    fun getSymptoms(): Single<List<String>>

    @DELETE("/$MAP/entries/{id}")
    fun deleteEntry(@Path("id") id: Int): Completable

    @POST("/$MAP/getroutes")
    fun getRoutes(
        @Query("filter") filter: Boolean,
        @Body coordinatesDto: CoordinatesDto
    ): Single<List<RouteLineDto>>

    @POST("/$MAP/routes")
    fun createRoute(@Body route: CreateRouteDto): Single<RouteDetailsDto>

    @GET("/$MAP/routes/{id}")
    fun getRoute(@Path("id") id: Int): Single<RouteDetailsDto>

    @POST("/$MAP/routes/{id}")
    fun editRoute(@Path("id") id: Int, @Body route: EditRouteDto): Single<RouteDetailsDto>

    @DELETE("/$MAP/routes/{id}")
    fun deleteRoute(@Path("id") id: Int): Completable

    @POST("$MAP/entries/preview")
    fun entryPreview(@Body entry: CreateEntryPreviewDto): Single<EntryPreviewDto>

    @POST("$MAP/routes/{id}/startEntry")
    fun startEntry(@Path("id") routeId: Int, @Body location: CreatePointDto): Single<StartEntryDto>

    @POST("/$MAP/routes/{id}/entries")
    fun createEntry(@Path("id") id: Int, @Body createEntryDto: CreateEntryDto): Single<RouteDetailsDto>

    @POST("/$MAP/routes/search")
    fun searchRoutes(@Body query: SearchRoutesDto): Single<List<RouteItemDto>>

    @POST("/$MAP/routes/preview")
    fun routePreview(@Body points: CreateRoutePreviewDto): Single<RoutePreviewDto>

    @GET("/$MAP/entries/{id}")
    fun getEntry(@Path("id") id: Int): Single<EntryDetailsDto>

    @GET("/$MAP/routes/{id}/map")
    fun getRouteOnMap(@Path("id") id: Int): Single<RouteLineDto>
}