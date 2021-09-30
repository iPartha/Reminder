package com.know.reminder.features.home.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.os.Build
import android.util.Log
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.know.reminder.R
import com.know.reminder.common.BaseActivity
import com.know.reminder.common.showAlert
import com.know.reminder.features.home.ReminderAction
import com.know.reminder.features.home.ReminderIntent
import com.know.reminder.features.home.ReminderState
import com.know.reminder.features.home.ReminderViewModel
import java.util.*
import com.google.android.gms.maps.model.PolylineOptions
import com.know.domain.LocationDirection


class MapsActivity : BaseActivity<ReminderIntent, ReminderAction, ReminderState, ReminderViewModel>(
    ReminderViewModel::class.java), OnMapReadyCallback {

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1001
        private const val REQUEST_BG_LOCATION_PERMISSION = 1002
        private const val MAP_ZOOM_LEVEL = 12.0f
        private const val TAG="MapsActivity"
    }

    private lateinit var mMap: GoogleMap
    private val markerPoints = arrayListOf<LatLng>()

    private val fusedLocationClient : FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    private lateinit var searchView : SearchView

     /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
         mMap = googleMap
         mMap.setOnMarkerDragListener(mapDraggerListener)
         getLastLocation()
    }

    private fun getPermissions() : Array<String> {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            }
            else ->{
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var permissionGranted = false
        if (grantResults.isNotEmpty()) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(requestCode == REQUEST_LOCATION_PERMISSION) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(
                                    this,
                                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                                )
                            ) {
                                showAlert(this, getString(R.string.location_permission_rationale_title), getString(R.string.location_permission_rationale_msg)) {
                                    ActivityCompat.requestPermissions(
                                        this,
                                        arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                                        REQUEST_BG_LOCATION_PERMISSION
                                    )
                                }
                            }
                        } else {
                            permissionGranted = true
                        }
                    } else if (requestCode == REQUEST_BG_LOCATION_PERMISSION) {
                        permissionGranted = true
                    }
                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        showAlert(this, getString(R.string.location_permission_rationale_title), getString(R.string.location_permission_rationale_msg)) {
                            ActivityCompat.requestPermissions(
                                this,
                                getPermissions(),
                                REQUEST_LOCATION_PERMISSION
                            )
                        }
                    }
                }
            }
        if (permissionGranted) {
            getLastLocation()
        }
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
           ActivityCompat.requestPermissions(this, getPermissions(), REQUEST_LOCATION_PERMISSION)
            return
        }
        mMap.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener(this){location->
            //Got last known location, In some rare situations this can be null
             location?.let {
                val curLocation = LatLng(it.latitude, it.longitude)
                try {
                    val geo = Geocoder(this, Locale.getDefault())
                    val address = geo.getFromLocation(it.latitude, it.longitude, 1)
                    address?.run {
                        address[0].run {
                            val street = "$featureName, $locality, $adminArea"
                            mMap.addMarker(MarkerOptions().position(curLocation).title(street).draggable(true))
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curLocation, MAP_ZOOM_LEVEL))
                        }
                    }
                }catch (e : Exception) {
                    showAlert(this, getString(R.string.location_permission_rationale_title),
                    getString(R.string.location_not_found)) {

                    }
                }
            }
        }

    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_maps
    }

    override fun initUI() {
        supportActionBar?.hide()
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        searchView = findViewById(R.id.searchView)
    }

    override fun initDATA() {

    }

    override fun initEVENT() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.run {
                    dispatchIntent(ReminderIntent.SearchLocation(this))
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    override fun render(state: ReminderState) {
        when (state) {
            is ReminderState.ResultLocation -> {
                println(state.location)
                state.location.results?.get(0)?.geometry?.run {
                    selectMap(mMap.cameraPosition.target, LatLng(location.lat, location.lng))
                }
            }
            is ReminderState.ResultDirection -> {
                println(state)
                drawDirections(state.direction)
            }
            is ReminderState.Exception -> {
                Log.e(TAG, "Exception :${state.callErrors}")
            }
        }
    }

    private val mapDraggerListener = object:GoogleMap.OnMarkerDragListener {
        override fun onMarkerDragStart(marker: Marker) {

        }

        override fun onMarkerDrag(marker: Marker) {

        }

        override fun onMarkerDragEnd(marker: Marker) {
            marker.run {
                position?.run {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude,longitude), MAP_ZOOM_LEVEL))
                }

            }
        }

    }

    private fun selectMap(origin: LatLng, dest: LatLng) {

        markerPoints.clear();
        mMap.clear();


        // Adding new item to the ArrayList
        markerPoints.add(origin)
        markerPoints.add(dest)

        // Creating MarkerOptions
        val options = MarkerOptions()

        // Setting the position of the marker
        options.position(dest)

        if (markerPoints.size == 1) {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        } else if (markerPoints.size == 2) {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }

        // Add new marker to the Google Map Android API V2
        mMap.addMarker(options)

        dispatchIntent(ReminderIntent.GetDirection(origin, dest))

    }

    private fun drawDirections(locationDirection: LocationDirection) {
        val points = ArrayList<LatLng>()
        val lineOptions =  PolylineOptions()
        val routes = locationDirection.directions

        for (i in 0 until routes.size) {

            val path = routes[i]

            for ( j in 0 until path.size) {
                points.add(LatLng(path[j].lat, path[j].lng))
            }

            lineOptions.addAll(points)
            lineOptions.width(12.0f)
            lineOptions.color(Color.RED)
            lineOptions.geodesic(true)

        }

        // Drawing polyline in the Google Map

        if (points.size != 0) {
            mMap.addPolyline(lineOptions)
        }
    }
}




