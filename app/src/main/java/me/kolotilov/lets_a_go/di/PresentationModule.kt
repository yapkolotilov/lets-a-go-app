package me.kolotilov.lets_a_go.di

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.GsonBuilder
import me.kolotilov.lets_a_go.App
import me.kolotilov.lets_a_go.network.*
import me.kolotilov.lets_a_go.presentation.base.NotificationService
import me.kolotilov.lets_a_go.presentation.base.PermissionService
import me.kolotilov.lets_a_go.presentation.base.getNotificationService
import me.kolotilov.lets_a_go.presentation.base.getPermissionService
import me.kolotilov.lets_a_go.presentation.details.UserDetailsContainer
import okhttp3.OkHttpClient
import okhttp3.Request
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import ru.terrakok.cicerone.Router
import java.io.IOException
import java.util.concurrent.TimeUnit

fun presentationModule() = DI.Module("Presentation") {
    bind<Context>() with singleton { instance<App>() }
    bind<SharedPreferences>() with singleton {
        instance<App>().getSharedPreferences(
            "LETS_A_GO",
            Context.MODE_PRIVATE
        )
    }
    bind<ResourceProvider>() with singleton { ResourceProviderImpl(instance()) }
    bind<LocalRepository>() with singleton { LocalRepositoryImpl(instance()) }
    bind<Router>() with singleton { instance<App>().getRouter() }
    bind<OkHttpClient>() with singleton { provideOkHttpClient(instance()) }
    bind<Retrofit>() with singleton { provideRetrofit(instance()) }
    bind<LetsAGoApi>() with singleton { instance<Retrofit>().create() }
    bind<NetworkRepository>() with singleton {
        NetworkRepositoryImpl(
            instance(),
            instance(),
            instance()
        )
    }
    bind<Repository>() with singleton { RepositoryImpl(instance(), instance()) }
    bind<UserDetailsContainer>() with singleton { UserDetailsContainer() }
    bind<PermissionService>() with singleton { getPermissionService(instance()) }
    bind<NotificationService>() with singleton { getNotificationService(instance(), instance()) }
}

private fun provideOkHttpClient(sharedPreferences: SharedPreferences): OkHttpClient {
    val timeout = 1_000L
    return OkHttpClient.Builder()
        .addInterceptor { chain ->
            val token = sharedPreferences.getString("TOKEN", "")!!
            val request = chain.request().newBuilder()
                .apply {
                    if (token.isNotEmpty())
                        addHeader("Authorization", "Bearer $token")
                }
                .build()
            Log.d("REQUEST", "$request : ${bodyToString(request)}")
            val response = chain.proceed(request)
            Log.d("RESPONSE", response.toString())
            response
        }
        .callTimeout(timeout, TimeUnit.SECONDS)
        .connectTimeout(timeout, TimeUnit.SECONDS)
        .readTimeout(timeout, TimeUnit.SECONDS)
        .writeTimeout(timeout, TimeUnit.SECONDS)
        .build()
}

private fun provideRetrofit(client: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .baseUrl("https://lets-a-go.herokuapp.com/")
        .client(client)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ssz")
                    .create()
            )
        )
        .build()
}

private fun bodyToString(request: Request): String? {
    return try {
        val copy: Request = request.newBuilder().build()
        val buffer = okio.Buffer()
        copy.body()?.writeTo(buffer)
        buffer.readUtf8()
    } catch (e: IOException) {
        "did not work"
    }
}