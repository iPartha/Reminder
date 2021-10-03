package com.know.reminder.features.home

import android.content.Context
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.know.data.repository.ReminderRepository
import com.know.domain.LocationDirection
import com.know.reminder.common.BaseViewModel
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

class ReminderViewModel @Inject constructor(private val repository: ReminderRepository,
                                            private val context: Context,
                                            ) :
    BaseViewModel<ReminderIntent, ReminderAction, ReminderState>() {

    override fun intentToAction(intent: ReminderIntent): ReminderAction {
        return when (intent) {
            is ReminderIntent.SearchLocation -> ReminderAction.SearchLocation(intent.address)
            is ReminderIntent.GetDirection -> ReminderAction.GetDirection(intent.origin, intent.dest)
            is ReminderIntent.SetReminder -> ReminderAction.SetReminder(intent.locationDirection, intent.remindBeforeInSecs)
        }
    }

    override fun handleAction(action: ReminderAction) {
        launchOnUI {
            when (action) {
                is ReminderAction.SearchLocation -> {
                    repository.getLocationByAddress(action.address).collect {
                        mState.postValue(it.reduce())
                    }
                }
                is ReminderAction.GetDirection -> {
                    repository.getDirection(
                        action.origin, action.dest).collect {
                            mState.postValue(it.reduce())
                    }
                }
                is ReminderAction.SetReminder -> {
                    setReminder(action.locationDirection, action.remindBeforeInSecs)
                    mState.postValue(ReminderState.ReminderSet)
                }
            }
        }
    }

    private fun setReminder(locationDirection: LocationDirection, remindBeforeInSecs : Long) {

        locationDirection.endLocation?.run {
            val reminderTime = (locationDirection.travelTimeInSecs*1000L)-
                    (ReminderService.getLocationUpdateTime(locationDirection.travelTimeInSecs)
                            +(remindBeforeInSecs*1000L))

            Log.i(ReminderService.TAG, "Total Travel Time=${locationDirection.travelTimeInSecs}, Reminder Time=${reminderTime/1000}")

            ReminderService.setAlarm(context, reminderTime, LatLng(this.lat, this.lng), remindBeforeInSecs)

        }

    }

}