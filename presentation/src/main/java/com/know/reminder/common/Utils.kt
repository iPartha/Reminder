package com.know.reminder.common

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.know.data.common.CallErrors
import com.know.reminder.R
import kotlin.reflect.KClass

fun <T : ViewModel> RootBaseActivity.viewModelProvider(
    factory: ViewModelProvider.Factory,
    model: KClass<T>
): T {
    return ViewModelProvider(this, factory).get(model.java)
}

fun Boolean.runIfTrue(block: () -> Unit) {
    if (this) {
        block()
    }
}

fun CallErrors.getMessage(context: Context): String {
    return when (this) {
        is CallErrors.ErrorEmptyData -> context.getString(R.string.error_empty_data)
        is CallErrors.ErrorServer -> context.getString(R.string.error_server_error)
        is CallErrors.ErrorException ->  context.getString(
            R.string.error_exception
        )
    }

}

fun Activity.showAlert(context: Context, title : String, msg: String, success: ()->Unit) {
    val alertBuilder = AlertDialog.Builder(context)
    alertBuilder.run {
        setCancelable(true)
        setTitle(title)
        setMessage(msg)
        setPositiveButton(android.R.string.yes){dialog,which->
            success()
        }
        create().show()
    }
}