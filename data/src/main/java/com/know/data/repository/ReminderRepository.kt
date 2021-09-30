package com.know.data.repository

import com.google.android.gms.maps.model.LatLng
import com.know.data.common.Result
import com.know.domain.GeoLocation
import com.know.domain.LocationDirection
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {
    fun getLocationByAddress(address:String) : Flow<Result<GeoLocation>>
    fun getDirection(origin: LatLng, dest: LatLng) :  Flow<Result<LocationDirection>>
}