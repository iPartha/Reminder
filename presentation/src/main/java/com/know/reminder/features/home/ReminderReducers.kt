package com.know.reminder.features.home


import com.know.data.common.Result
import com.know.domain.GeoLocation
import com.know.domain.LocationDirection
import com.know.reminder.common.ViewAction


fun Result<Any>.reduce(): ReminderState {
    return when (this) {
        is Result.Success ->
            when (data) {
                is GeoLocation -> {
                    ReminderState.ResultLocation(data as GeoLocation)
                }
                is LocationDirection -> {
                    ReminderState.ResultDirection(data as LocationDirection)
                }
                is Long -> {
                    ReminderState.ResultDuration(data as Long)
                }
                else -> {
                    ReminderState.Nothing
                }
            }
        is Result.Error -> ReminderState.Exception(exception)
        is Result.Loading -> ReminderState.Loading
    }
}
