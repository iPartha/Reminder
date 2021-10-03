package com.know.reminder.common

import android.os.Bundle
import androidx.annotation.LayoutRes

abstract class BaseActivity<INTENT : ViewIntent, ACTION : ViewAction, STATE : ViewState,
        VM : BaseViewModel<INTENT, ACTION, STATE>>(private val modelClass: Class<VM>) :
    RootBaseActivity(), IViewRenderer<STATE> {
    private lateinit var viewState: STATE
    val mState get() = viewState

    private val viewModel: VM by lazy {
        viewModelProvider(
            this.viewModelFactory,
            modelClass.kotlin
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResId())
        initUI()
        viewModel.state.observe(this, {
            viewState = it
            render(it)
        })
        initEVENT()
    }


    @LayoutRes
    abstract fun getLayoutResId(): Int
    abstract fun initUI()
    abstract fun initEVENT()
    fun dispatchIntent(intent: INTENT) {
        viewModel.dispatchIntent(intent)
    }
}