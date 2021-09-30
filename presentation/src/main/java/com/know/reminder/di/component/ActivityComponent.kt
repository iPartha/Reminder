package com.know.reminder.di.component

import com.know.reminder.di.annotations.ActivityScope
import com.know.reminder.di.common.AppRouter
import com.know.reminder.di.module.ActivityModule
import com.know.reminder.common.RootBaseActivity
import dagger.Component


@ActivityScope
@Component(modules = [ActivityModule::class], dependencies = [ApplicationComponent::class])
interface ActivityComponent {
    fun inject(baseActivity: RootBaseActivity)
    fun appRouter(): AppRouter
}