package com.know.reminder.di.component

import com.know.reminder.common.RootBaseService
import com.know.reminder.di.annotations.ServiceScope
import com.know.reminder.di.common.AppRouter
import com.know.reminder.di.common.ServiceRouter
import com.know.reminder.di.module.ServiceModule
import dagger.Component

@ServiceScope
@Component(modules = [ServiceModule::class], dependencies = [ApplicationComponent::class])
interface ServiceComponent {
    fun inject(service: RootBaseService)
    fun serviceRouter(): ServiceRouter
}