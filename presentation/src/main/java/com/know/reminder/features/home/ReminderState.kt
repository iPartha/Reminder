package com.know.reminder.features.home

import com.know.data.common.CallErrors
import com.know.domain.GeoLocation
import com.know.domain.LocationDirection
import com.know.reminder.common.ViewState


sealed class ReminderState : ViewState{
    object Loading : ReminderState()
    object Nothing : ReminderState()
    data class ResultLocation(val location : GeoLocation): ReminderState()
    data class ResultDirection(val direction : LocationDirection): ReminderState()
    data class Exception(val callErrors: CallErrors) : ReminderState()
}