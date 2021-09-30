package com.know.data.common

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retryWhen
import java.io.IOException

object Utils {
    const val MAX_RETRIES = 3L
    private const val INITIAL_BACKOFF = 2000L

    fun getBackoffDelay(attempt: Long) = INITIAL_BACKOFF * (attempt + 1)
}

fun <T : Any> Flow<Result<T>>.applyCommonSideEffects() =
    retryWhen { cause, attempt ->
        when {
            (cause is IOException && attempt < Utils.MAX_RETRIES) -> {
                delay(Utils.getBackoffDelay(attempt))
                true
            }
            else -> {
                false
            }
        }
    }
        .onStart {
            emit(Result.Loading)
        }

fun LatLng.asString() : String {
    return "$latitude,$longitude"
}