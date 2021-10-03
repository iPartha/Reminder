package com.know.reminder.di.component

import android.app.Service
import com.know.data.services.ApiService
import com.know.reminder.ReminderApplication
import com.know.reminder.common.RootBaseActivity
import com.know.reminder.di.module.ApplicationModule
import com.know.reminder.di.module.NetworkModule
import com.know.reminder.di.viewmodels.DaggerViewModelFactory
import com.know.reminder.di.viewmodels.ViewModelModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton


@Component(modules = [ApplicationModule::class, NetworkModule::class, ViewModelModule::class])
@Singleton
interface ApplicationComponent {

    @Component.Builder
    interface Builder {
        fun build(): ApplicationComponent


        @BindsInstance
        fun application(app: ReminderApplication): Builder
    }

    fun provideDaggerViewModelFactory(): DaggerViewModelFactory
    fun providesService() : ApiService
}