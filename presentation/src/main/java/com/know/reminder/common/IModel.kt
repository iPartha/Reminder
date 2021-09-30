package com.know.reminder.common

import androidx.lifecycle.LiveData

interface IModel<STATE, INTENT> {

    val state: LiveData<STATE>

    fun dispatchIntent(intent: INTENT)
}