package com.know.reminder.common

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.know.data.repository.ReminderRepository
import com.know.data.services.ApiService
import com.know.reminder.ReminderApplication
import com.know.reminder.di.common.AppRouter
import com.know.reminder.di.component.ActivityComponent
import com.know.reminder.di.component.DaggerActivityComponent
import com.know.reminder.di.module.ActivityModule
import com.know.reminder.di.viewmodels.DaggerViewModelFactory
import javax.inject.Inject


open class RootBaseActivity : AppCompatActivity() {

    private val activityComponent: ActivityComponent by lazy {
        DaggerActivityComponent.builder().activityModule(ActivityModule(this))
            .applicationComponent(ReminderApplication.appComponents).build()
    }

    @Inject
    lateinit var service : ApiService

    @Inject
    lateinit var repository: ReminderRepository

    @Inject
    lateinit var appRouter: AppRouter

    @Inject
    lateinit var viewModelFactory: DaggerViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        activityComponent.inject(this)
        super.onCreate(savedInstanceState)
    }


}