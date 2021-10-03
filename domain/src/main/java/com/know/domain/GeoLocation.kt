package com.know.domain

import java.io.Serializable

data class GeoLocation(val results : List<Result>?, val status : String)

data class Result(
    val address_components: List<Address>,
    val formatted_address: String,
    val geometry: Geometry,
    val place_id: String,
    val types: Array<String>) {

}


data class Geometry(
    val bounds : Direction,
    val location : LocationLatLng,
    val location_type: String,
    val viewport: Direction
)

data class Direction(val northeast:LocationLatLng, val southwest:LocationLatLng)

data class LocationLatLng(val lat:Double, val lng:Double) : Serializable {
    override fun toString(): String {
        return "$lat,$lng"
    }
}



data class Address(
    private val long_name:String,
    private val short_name:String,
    private val types: Array<String>
)
