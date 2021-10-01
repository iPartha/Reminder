package com.know.reminder.di.module


import com.google.gson.GsonBuilder
import com.know.data.services.ApiService
import com.know.reminder.BuildConfig
import dagger.Module
import dagger.Provides
import okhttp3.*

import retrofit2.converter.gson.GsonConverterFactory

import javax.inject.Singleton
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.io.IOException


@Module
class NetworkModule {

    @Provides
    @Singleton
    fun providesOkHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)

        client.addInterceptor(object : Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): Response {
                val original: Request = chain.request()
                val originalHttpUrl: HttpUrl = original.url
                val url: HttpUrl = originalHttpUrl.newBuilder()
                    .addQueryParameter("key", BuildConfig.MAPS_API_KEY)
                    .build()

                // Request customization: add request headers
                val requestBuilder: Request.Builder = original.newBuilder()
                    .url(url)
                val request: Request = requestBuilder.build()
                return chain.proceed(request)
            }
        })

        return client.build()
    }

    @Provides
    @Singleton
    fun providesRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit {
        val gsonBuilder = GsonBuilder()
        return Retrofit.Builder().baseUrl(BuildConfig.API_BASE_URL)
            .addConverterFactory((GsonConverterFactory.create(gsonBuilder.create())))
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun providesApiService(retrofit: Retrofit) : ApiService {
        return  retrofit.create(ApiService::class.java)
    }
}