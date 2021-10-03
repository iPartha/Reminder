package com.know.reminder.common

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.know.data.repository.ReminderRepository
import com.know.data.services.ApiService
import com.know.reminder.ReminderApplication
import com.know.reminder.di.common.AppRouter
import com.know.reminder.di.common.ServiceRouter
import com.know.reminder.di.component.DaggerServiceComponent
import com.know.reminder.di.component.ServiceComponent
import com.know.reminder.di.module.ServiceModule
import javax.inject.Inject

open class RootBaseService : Service() {

    private val serviceComponent: ServiceComponent by lazy {
        DaggerServiceComponent.builder().serviceModule(ServiceModule(this))
            .applicationComponent(ReminderApplication.appComponents).build()
    }

    @Inject
    lateinit var service : ApiService

    @Inject
    lateinit var repository: ReminderRepository

    @Inject
    lateinit var appRouter: ServiceRouter

    override fun onCreate() {
        serviceComponent.inject(this)
        super.onCreate()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }


}