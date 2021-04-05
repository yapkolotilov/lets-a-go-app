package me.kolotilov.lets_a_go.network

import io.reactivex.Completable
import io.reactivex.Single
import me.kolotilov.lets_a_go.network.input.*
import me.kolotilov.lets_a_go.network.output.EntryDto
import me.kolotilov.lets_a_go.network.output.RouteDetailsDto
import me.kolotilov.lets_a_go.network.output.TokenDto
import me.kolotilov.lets_a_go.network.output.UserDetailsDto
import retrofit2.http.*

private const val AUTH = "auth"
private const val DETAILS = "details"
private const val MAP = "map"

interface LetsAGoApi {

    @POST("/$AUTH/register")
    fun register(@Body loginDto: LoginDto): Completable

    @POST("/$AUTH/login")
    fun login(@Body loginDto: LoginDto): Single<TokenDto>

    @GET("/$DETAILS")
    fun getDetails(): Single<UserDetailsDto>

    @POST("/$DETAILS")
    fun editDetails(@Body editDetailsDto: EditDetailsDto): Single<UserDetailsDto>

    @POST("/$DETAILS/change_password")
    fun changePassword(@Body changePasswordDto: ChangePasswordDto): Single<UserDetailsDto>

    @GET("/$DETAILS/illnesses")
    fun getIllnesses(): Single<List<String>>

    @GET("/$DETAILS/symptoms")
    fun getSymptoms(): Single<List<String>>

    @GET("/$MAP/entries")
    fun getEntries(): Single<List<EntryDto>>

    @DELETE("/$MAP/entries/{id}")
    fun deleteEntry(@Path("id") id: Int): Completable

    @GET("/$MAP/routes")
    fun getRoutes(@Query("filter") filter: Boolean): Single<List<RouteDetailsDto>>

    @POST("/$MAP/routes")
    fun createRoute(@Body route: CreateRouteDto): Single<RouteDetailsDto>

    @GET("/$MAP/routes/{id}")
    fun getRoute(@Path("id") id: Int): Single<RouteDetailsDto>

    @POST("/$MAP/routes/{id}")
    fun editRoute(@Path("id") id: Int, @Body route: EditRouteDto): Single<RouteDetailsDto>

    @DELETE("/$MAP/routes/{id}")
    fun deleteRoute(@Path("id") id: Int): Completable

    @POST("/$MAP/routes/{id}/entries")
    fun createEntry(@Path("id") id: Int, @Body createEntryDto: CreateEntryDto): Single<RouteDetailsDto>

    @POST("/$MAP/routes/search")
    fun searchRoutes(@Query("name") name: String?, filter: FilterDto?): Single<List<RouteDetailsDto>>
}