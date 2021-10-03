package com.know.reminder.features.home

import com.google.android.gms.maps.model.LatLng
import com.know.domain.LocationDirection
import com.know.reminder.common.ViewAction


sealed class ReminderAction : ViewAction {
    data class SearchLocation(val address: String) : ReminderAction()
    data class GetDirection(val origin: LatLng, val dest: LatLng) : ReminderAction()
    data class SetReminder(val locationDirection: LocationDirection, val remindBeforeInSecs : Long) : ReminderAction()
}