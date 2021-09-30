package com.know.data.services

import com.know.data.common.*
import com.know.domain.GeoLocation
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiService {

    @GET("/maps/api/geocode/json")
    suspend fun getLocationByAddress(@Query(ADDRESS) address : String,  @Query(SENSOR)sensor : Boolean=true) : GeoLocation

    @GET("maps/api/directions/json")
    suspend fun getDirection(@Query(ORIGIN) origin : String,
                             @Query(DESTINATION)destination : String,
                             @Query(MODE)mode : String="driving",
                             @Query(SENSOR)sensor : Boolean=false) : ResponseBody

}