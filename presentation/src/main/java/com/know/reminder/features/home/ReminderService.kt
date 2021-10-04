package com.know.reminder.features.home

import android.annotation.SuppressLint
import android.app.*

import android.content.Intent


import android.content.Context
import android.location.Location


import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.know.reminder.R

import android.os.Looper
import android.util.Log
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.know.data.repository.ReminderRepository
import com.know.domain.LocationLatLng
import com.know.reminder.common.RootBaseService
import com.know.reminder.common.toMilliSeconds
import com.know.reminder.di.component.ApplicationComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject


class ReminderService : RootBaseService() {

    private var locationRequest: LocationRequest? = null
    private val fusedLocationClient : FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)
    private var reminderTimeInSecs = 0L
    private lateinit var destinationLocation : LatLng

    companion object {

        const val LOCATION_SERVICE_NOTIFICATION_ID = 1000
        const val LOCATION_UPDATE_INTERVAL_IN_MS = 5*60*1000L // 5 MINS
        const val KEY_LOCATION_LAT ="location_lat"
        const val KEY_LOCATION_LNG ="location_lng"
        const val KEY_REMINDER_TIME = "reminderTime"
        const val TAG = "ReminderService"
        const val NOTIFICATION_CHANNEL_ID ="ReminderNotification"
        const val NOTIFICATION_CHANNEL_NAME ="ReminderService"

        fun setAlarm(context: Context, locationUpdateTime: Long, location : LatLng, reminderTimeInSecs : Long) {
            val am = context.applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context.applicationContext, ReminderService::class.java)
            //intent.putExtra(KEY_LOCATION, location)
            intent.putExtra(KEY_REMINDER_TIME, reminderTimeInSecs)
            intent.putExtra(KEY_LOCATION_LAT, location.latitude)
            intent.putExtra(KEY_LOCATION_LNG, location.longitude)
            intent.action = "ReminderService"
            val pi = PendingIntent.getService(context.applicationContext, 1001, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, (System.currentTimeMillis()+locationUpdateTime), pi)

            val serviceIntent = Intent(context.applicationContext, ReminderService::class.java)
            context.startService(serviceIntent)
        }

        fun cancelAlarm(context: Context) {
            val intent = Intent(context, ReminderService::class.java)
            val sender = PendingIntent.getBroadcast(context, 0, intent, 0)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(sender)
            context.stopService(Intent(context.applicationContext, ReminderService::class.java))
        }


        fun getLocationUpdateTime(travelTimeInSecs : Long) : Long {
            return when {
                travelTimeInSecs > 7200L -> {
                    30L.toMilliSeconds()
                }
                travelTimeInSecs > 3600L -> {
                    15L.toMilliSeconds()
                }
                travelTimeInSecs > 1800L -> {
                    10L.toMilliSeconds()
                }
                else -> {
                    5L.toMilliSeconds()
                }
            }
        }
    }


    override fun onCreate() {
        super.onCreate()
        createLocationRequest()
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.create()
        locationRequest?.run {
            interval = (LOCATION_UPDATE_INTERVAL_IN_MS)
            setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        }

    }

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val currentLocation: Location = locationResult.lastLocation
            Log.i(TAG, "Current location=$currentLocation")
            serviceScope.launch {
                repository.getDuration(
                    LatLng(currentLocation.latitude,
                        currentLocation.longitude
                    ), destinationLocation
                ).collect {
                    it.reduce().run {
                        if (this is ReminderState.ResultDuration) {
                            Log.i(TAG, "Remaining travel time=${this.duration}, Reminder time=$reminderTimeInSecs")
                            if (this.duration <= reminderTimeInSecs) {
                                notifyUser()
                                stopSelf()
                            }
                        }
                    }

                }

            }

        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.run {
            extras?.run {
                destinationLocation = LatLng(
                    getDoubleExtra(KEY_LOCATION_LAT,0.0),
                    getDoubleExtra(KEY_LOCATION_LNG,0.0)
                )

                getLongExtra(KEY_REMINDER_TIME, 0).run {
                    reminderTimeInSecs = this
                }
            }
        }

        startForeground(LOCATION_SERVICE_NOTIFICATION_ID,
            createForegroundInfo(applicationContext.getString(R.string.location_update)))

        if (this::destinationLocation.isInitialized) {
            startLocationUpdates()

        }

        return START_STICKY
    }

    private fun notifyUser() {
        val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        val r = RingtoneManager.getRingtone(
            applicationContext,
            notification
        )
        r.play()
    }

    private fun createForegroundInfo(progress: String): Notification {
        val title = applicationContext.getString(R.string.reminder_set)
        val cancel = applicationContext.getString(R.string.cancel)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }

        return NotificationCompat.Builder(
            applicationContext,
            ReminderService.NOTIFICATION_CHANNEL_ID
        )
            .setContentTitle(title)
            .setTicker(title)
            .setContentText(progress)
            .setSmallIcon(R.drawable.map)
            .setOngoing(true)
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {

        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val mChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, importance)
        val notificationManager = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }

    override fun onDestroy() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        cancelAlarm(applicationContext)
        serviceJob.cancel()
        super.onDestroy()
    }
}