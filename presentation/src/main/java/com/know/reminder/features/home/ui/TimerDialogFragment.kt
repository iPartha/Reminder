package com.know.reminder.features.home.ui

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.know.reminder.R

class TimerDialogFragment : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "TimerDialogFragment"
    }
    private lateinit var timerClickListener: TimerClickListener

    interface TimerClickListener {
        fun onTimerChange(timerInSecs : Long)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.timer_bottom_sheet, container, false)
        view.findViewById<Button>(R.id.minute5).setOnClickListener {
            dismissTimer(5)
        }
        view.findViewById<Button>(R.id.minute10).setOnClickListener {
            dismissTimer(10)
        }
        view.findViewById<Button>(R.id.minute15).setOnClickListener {
            dismissTimer(15)
        }
        view.findViewById<Button>(R.id.minute20).setOnClickListener {
            dismissTimer(20)
        }
        view.findViewById<Button>(R.id.minute25).setOnClickListener {
            dismissTimer(30)
        }
        view.findViewById<Button>(R.id.minute30).setOnClickListener {
            dismissTimer(30)
        }
        return view
    }

    override fun onAttach(activity: Activity) {
        if (activity is TimerClickListener) {
            timerClickListener = activity as TimerClickListener
        }
        super.onAttach(activity)
    }

    private fun dismissTimer(time : Int) {
        timerClickListener.onTimerChange(time*60L)
        dismiss()
    }
}