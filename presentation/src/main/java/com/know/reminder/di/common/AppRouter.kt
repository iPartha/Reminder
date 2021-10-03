package com.know.reminder.di.common

import com.know.reminder.di.annotations.ActivityScope
import com.know.reminder.common.RootBaseActivity
import com.know.reminder.common.RootBaseService
import com.know.reminder.di.annotations.ServiceScope
import javax.inject.Inject


@ActivityScope
class AppRouter @Inject constructor(private val activity : RootBaseActivity){


}

@ServiceScope
class ServiceRouter @Inject constructor(private val service : RootBaseService){


}