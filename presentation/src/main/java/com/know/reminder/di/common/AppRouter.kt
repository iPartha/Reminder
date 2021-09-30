package com.know.reminder.di.common

import com.know.reminder.di.annotations.ActivityScope
import com.know.reminder.common.RootBaseActivity
import javax.inject.Inject


@ActivityScope
class AppRouter @Inject constructor(private val activity : RootBaseActivity){


}