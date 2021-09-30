package com.know.reminder.features.home

import com.google.android.gms.maps.model.LatLng
import com.know.reminder.common.ViewIntent


sealed class ReminderIntent : ViewIntent {
    data class SearchLocation(val address: String) : ReminderIntent()
    data class GetDirection(val origin: LatLng, val dest: LatLng) : ReminderIntent()
}