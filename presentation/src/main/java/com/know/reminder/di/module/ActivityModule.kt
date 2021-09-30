package com.know.reminder.di.module

import android.content.Context
import com.know.data.services.ApiService
import com.know.data.repository.ReminderRepository
import com.know.data.repository.ReminderRepositoryImpl
import com.know.reminder.di.common.AppRouter
import com.know.reminder.common.RootBaseActivity
import dagger.Module
import dagger.Provides


@Module
class ActivityModule constructor(private val activity : RootBaseActivity) {

    @Provides
    fun providesActivityContext() : Context = activity.application

    @Provides
    fun providesActivity() : RootBaseActivity { return activity}


    @Provides
    fun providesRouterComponent() : AppRouter = AppRouter(activity)

    @Provides
    fun providesReminderRepository(apiService: ApiService) : ReminderRepository {
        return ReminderRepositoryImpl(apiService)
    }
}