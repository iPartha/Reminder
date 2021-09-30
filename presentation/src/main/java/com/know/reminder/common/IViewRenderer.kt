package com.know.reminder.common


interface IViewRenderer<STATE> {
    fun render(state: STATE)
}