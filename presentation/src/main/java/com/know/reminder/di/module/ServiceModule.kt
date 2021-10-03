package com.know.reminder.di.module

import android.content.Context
import com.know.data.services.ApiService
import com.know.data.repository.ReminderRepository
import com.know.data.repository.ReminderRepositoryImpl
import com.know.reminder.di.common.AppRouter
import com.know.reminder.common.RootBaseActivity
import com.know.reminder.common.RootBaseService
import com.know.reminder.di.common.ServiceRouter
import dagger.Module
import dagger.Provides


@Module
class ServiceModule constructor(private val service : RootBaseService) {

    @Provides
    fun providesServiceContext() : Context = service.applicationContext

    @Provides
    fun providesService() : RootBaseService { return service}

    @Provides
    fun providesRouterComponent() : ServiceRouter = ServiceRouter(service)

    @Provides
    fun providesReminderRepository(apiService: ApiService) : ReminderRepository {
        return ReminderRepositoryImpl(apiService)
    }
}