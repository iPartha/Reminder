package com.know.data.repository

import com.google.android.gms.maps.model.LatLng
import com.know.data.common.CallErrors
import com.know.data.common.Result
import com.know.data.common.applyCommonSideEffects
import com.know.data.common.asString
import com.know.data.services.ApiService
import com.know.domain.LocationDirection
import com.know.domain.LocationLatLng
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.net.URLEncoder
import org.json.JSONObject
import org.json.JSONException

import org.json.JSONArray
import java.lang.Exception


class ReminderRepositoryImpl(private val api: ApiService) : ReminderRepository {

    override fun getLocationByAddress(address: String)  = flow {
        val str = URLEncoder.encode(address)
        api.getLocationByAddress(str).run {
            if (this.results == null || !this.status.contentEquals("OK")) {
                emit(Result.Error(CallErrors.ErrorEmptyData))
            } else {
                emit(Result.Success(this))
            }
        }
        }.applyCommonSideEffects().catch {
            emit(Result.Error(CallErrors.ErrorException(it)))
    }

    override fun getDirection(origin: LatLng, dest: LatLng) = flow {
        api.getDirection(origin.asString(), dest.asString()).run {
            val json = JSONObject(this.string())
            emit(Result.Success(parseDirections(json)))

        }
    }.applyCommonSideEffects().catch {
        emit(Result.Error(CallErrors.ErrorException(it)))
    }

    private fun parseDirections(jsonObject: JSONObject) : LocationDirection {
        val routes: MutableList<List<LocationLatLng>> = ArrayList()
        val jRoutes: JSONArray?
        var jLegs: JSONArray?
        var jSteps: JSONArray?
        var totalTime = 0;

        try {
            jRoutes = jsonObject.getJSONArray("routes")
            /** Traversing all routes  */
            for (i in 0 until jRoutes.length()) {
                jLegs = (jRoutes[i] as JSONObject).getJSONArray("legs")
                val path = ArrayList<LocationLatLng>()
                /** Traversing all legs  */
                for (j in 0 until jLegs.length()) {
                    jSteps = (jLegs[j] as JSONObject).getJSONArray("steps")
                    /** Traversing all steps  */
                    for (k in 0 until jSteps.length()) {
                        var polyline = ""
                        polyline =
                            ((jSteps[k] as JSONObject)["polyline"] as JSONObject)["points"] as String
                        val list: List<*> = decodePoly(polyline)
                        /** Traversing all points  */
                        for (l in list.indices) {
                            val hm = LocationLatLng((list[l] as LatLng).latitude, (list[l] as LatLng).longitude)
                            path.add(hm)
                        }
                    }
                    routes.add(path)
                    (jLegs[j] as JSONObject).getJSONObject("duration").run {
                        getInt("value").run {
                            totalTime += this
                        }
                    }
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: Exception) {
        }

        return LocationDirection(routes, totalTime)
    }
    private fun decodePoly(encoded: String): List<*> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            val p = LatLng(
                lat.toDouble() / 1E5,
                lng.toDouble() / 1E5
            )
            poly.add(p)
        }
        return poly
    }

}

