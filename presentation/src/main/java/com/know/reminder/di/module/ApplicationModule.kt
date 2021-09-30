package com.know.reminder.di.module

import android.content.Context
import com.know.data.services.ApiService
import com.know.data.repository.ReminderRepository
import com.know.data.repository.ReminderRepositoryImpl
import com.know.reminder.ReminderApplication
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class ApplicationModule {
    @Provides
    fun provideApplicationContext(application : ReminderApplication) : Context = application.applicationContext


    @Provides
    @Singleton
    fun provideReminderRepositoryAccessor(apiService: ApiService): ReminderRepository{
        return ReminderRepositoryImpl(apiService)
    }
}