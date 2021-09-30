package com.know.reminder.features.home


import com.know.data.common.Result
import com.know.domain.GeoLocation
import com.know.domain.LocationDirection
import com.know.reminder.common.ViewAction


fun Result<Any>.reduce(viewAction: ViewAction): ReminderState {
    return when (this) {
        is Result.Success -> {
            when (viewAction) {
                is ReminderAction.SearchLocation -> {
                    ReminderState.ResultLocation(data as GeoLocation)
                }
                is ReminderAction.GetDirection -> {
                    ReminderState.ResultDirection(data as LocationDirection)
                }
                else -> {
                    ReminderState.Nothing
                }
            }
        }
        is Result.Error -> ReminderState.Exception(exception)
        is Result.Loading -> ReminderState.Loading
    }
}
