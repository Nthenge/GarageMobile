package com.mobile.garaje.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "http://192.168.137.1:8083/"

    val WS_BASE_URL: String = BASE_URL.replaceFirst("http", "ws") + "ws"
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val authenticatedOkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val authenticatedRetrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(authenticatedOkHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authApi: AuthApiService = retrofit.create(AuthApiService::class.java)

    val garageApi: GarageApiService             = authenticatedRetrofit.create(GarageApiService::class.java)
    val mechanicApi: MechanicApiService         = authenticatedRetrofit.create(MechanicApiService::class.java)
    val carOwnerApi: CarOwnerApiService         = authenticatedRetrofit.create(CarOwnerApiService::class.java)
    val commandCentreApi: CommandCentreApiService = authenticatedRetrofit.create(CommandCentreApiService::class.java)
    val servicesApi: ServicesApiService = authenticatedRetrofit.create(ServicesApiService::class.java)
    val garageHomeApi: GarageHomeApiService = authenticatedRetrofit.create(GarageHomeApiService::class.java)
    val garageBookingsApi: GarageBookingsApiService = authenticatedRetrofit.create(GarageBookingsApiService::class.java)
    val carOwnerHomeApi: CarOwnerHomeApiService = authenticatedRetrofit.create(CarOwnerHomeApiService::class.java)
    val carOwnerServiceApi: CarOwnerServiceApiService = authenticatedRetrofit.create(CarOwnerServiceApiService::class.java)
    val supportApi: SupportApiService = authenticatedRetrofit.create(SupportApiService::class.java)

}