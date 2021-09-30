package com.know.reminder.common

import com.know.data.common.Result


interface IReducer<STATE, T :Any> {
    fun reduce(result: Result<T>, state: STATE,): STATE
}