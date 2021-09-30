package com.know.reminder

import android.app.Application
import com.know.reminder.di.component.ApplicationComponent
import com.know.reminder.di.component.DaggerApplicationComponent


class ReminderApplication: Application() {

    companion object {
        lateinit var appComponents: ApplicationComponent
    }

    override fun onCreate() {
        super.onCreate()
        appComponents = initDI()
    }

    private fun initDI(): ApplicationComponent =
        DaggerApplicationComponent.builder().application(this).build()
}