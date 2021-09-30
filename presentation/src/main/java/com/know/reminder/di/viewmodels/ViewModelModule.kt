package com.know.reminder.di.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.know.reminder.di.annotations.ViewModelKey
import com.know.reminder.features.home.ReminderViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(ReminderViewModel::class)
    abstract fun bindHomeViewModel(homeViewModel: ReminderViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: DaggerViewModelFactory): ViewModelProvider.Factory
}