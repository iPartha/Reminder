package com.know.reminder.features.home

import com.know.data.repository.ReminderRepository
import com.know.reminder.common.BaseViewModel
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

class ReminderViewModel @Inject constructor(private val repository: ReminderRepository) :
    BaseViewModel<ReminderIntent, ReminderAction, ReminderState>() {
    override fun intentToAction(intent: ReminderIntent): ReminderAction {
        return when (intent) {
            is ReminderIntent.SearchLocation -> ReminderAction.SearchLocation(intent.address)
            is ReminderIntent.GetDirection -> ReminderAction.GetDirection(intent.origin, intent.dest)
        }
    }

    override fun handleAction(action: ReminderAction) {
        launchOnUI {
            when (action) {
                is ReminderAction.SearchLocation -> {
                    repository.getLocationByAddress(action.address).collect {
                        mState.postValue(it.reduce(action))
                    }
                }
                is ReminderAction.GetDirection -> {
                    repository.getDirection(
                        action.origin, action.dest).collect {
                            mState.postValue(it.reduce(action))
                    }
                }
            }
        }
    }
}