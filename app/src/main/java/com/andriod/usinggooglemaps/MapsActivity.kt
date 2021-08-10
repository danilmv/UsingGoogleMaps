package com.andriod.usinggooglemaps

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.andriod.usinggooglemaps.databinding.ActivityMapsBinding
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapsBinding

    private val locationManager by lazy { getSystemService(LOCATION_SERVICE) as LocationManager }

    private var _googleMap: GoogleMap? = null
    private val googleMap: GoogleMap get() = _googleMap!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                ZZ_PERMISSION_REQUEST_CODE)
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                TIME_PASSED,
                DISTANCE_PASSED,
                MyLocListener())
        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener {
            addMarker(it, "Last known location")
        }
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    override fun onMapReady(map: GoogleMap) {
        _googleMap = map
        googleMap.apply {
            uiSettings.isZoomControlsEnabled = true
            isBuildingsEnabled = true
            isIndoorEnabled = true
            isTrafficEnabled = true
            isMyLocationEnabled = true
        }
        // Add a marker in Sydney and move the camera

        val sydney = LatLng(-34.0, 151.0)
        addMarker(sydney, "Marker in Sydney")
    }


    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ZZ_PERMISSION_REQUEST_CODE) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                TIME_PASSED,
                DISTANCE_PASSED,
                MyLocListener())
        }
    }

    private fun addMarker(location: Location, title: String) {
        val latLng = LatLng(location.latitude, location.longitude)
        addMarker(latLng, title)
    }

    private fun addMarker(location: LatLng, title: String) {
        if (_googleMap == null) return
        googleMap.apply {
            addMarker(MarkerOptions().position(location).title(title))
            moveCamera(CameraUpdateFactory.newLatLng(location))
            animateCamera(CameraUpdateFactory.zoomTo(13.0f))
        }
    }

    inner class MyLocListener :
        LocationListener {
        override fun onLocationChanged(location: Location) {
            addMarker(location, "My position")
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        }
    }

    companion object {
        const val ZZ_PERMISSION_REQUEST_CODE = 1

        const val TIME_PASSED = 10_000L
        const val DISTANCE_PASSED = 100f
    }
}