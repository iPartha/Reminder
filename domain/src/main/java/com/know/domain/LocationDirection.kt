package com.know.domain

data class LocationDirection(
    val directions: MutableList<List<LocationLatLng>>,
    val endLocation: LocationLatLng?,
    val travelTimeInSecs : Long)