package com.mywings.emergencyvehicle

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.mywings.emergencyvehicle.models.SignalPoints
import com.mywings.emergencyvehicle.process.*
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject


class MainActivity : AppCompatActivity(),
    OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, LocationListener, OnPointListener, OnUpdateLightListener,
    OnUpdateRouteListener, OnUpdateLocationListener {


    private var mMap: GoogleMap? = null
    private val SHOW_ICON_IN_MAP = 49
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mLocationRequest: LocationRequest? = null
    private var latLng: LatLng = LatLng(18.515665, 73.924090)
    private var locationManager: LocationManager? = null
    private lateinit var cPosition: Marker
    private lateinit var marker: Marker
    private lateinit var circle: Circle
    private lateinit var progressDialogUtil: ProgressDialogUtil


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var frame = activity_place_map as SupportMapFragment
        frame.getMapAsync(this)

        progressDialogUtil = ProgressDialogUtil(this)

        imgForwad.setOnClickListener {

        }

        imgForwadRound.setOnClickListener {

        }

        imgLeft.setOnClickListener {

        }

        imgLeftRound.setOnClickListener {

        }
        imgRight.setOnClickListener {

        }
        imgRightRound.setOnClickListener {

        }
    }


    private fun setupMap() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val enabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if (enabled) {
            var location = LocationUtil.getBestLastKnownLocation(this)

            latLng = LatLng(location.latitude, location.longitude)
        }

        mMap!!.uiSettings.isMyLocationButtonEnabled = false


        mGoogleApiClient = GoogleApiClient.Builder(this!!)

            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()



        mLocationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval((10 * 1000).toLong())
            .setFastestInterval((1 * 1000).toLong())
        mGoogleApiClient!!.connect()

        val strokeColor = ContextCompat.getColor(this, R.color.map_circle_stroke)
        val shadeColor = ContextCompat.getColor(this, R.color.map_circle_shade)
        val latLng = this.latLng
        circle = mMap!!.addCircle(
            CircleOptions()
                .center(latLng)
                .radius(5.0)
                .fillColor(shadeColor)
                .strokeColor(strokeColor)
                .strokeWidth(2f)
        )

        val icon = BitmapDescriptorFactory.fromResource(R.drawable.icon)

        marker = mMap!!.addMarker(MarkerOptions().position(latLng).icon(icon).title("Ambulance location"))
        val cameraPos = CameraPosition.Builder().tilt(60f).target(latLng).zoom(20f).build()
        mMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos), 1000, null)

        init()

    }

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onMapReady(map: GoogleMap?) {
        mMap = map

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            setupMap()
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                SHOW_ICON_IN_MAP
            )
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return
            if (locationResult.locations[0].speed > 5) {
                if (null != marker) marker.remove()
                if (null != circle) circle.remove()
                latLng = LatLng(locationResult.locations[0].latitude, locationResult.locations[0].longitude)
                val icon = BitmapDescriptorFactory.fromResource(R.drawable.icon)
                marker = mMap!!.addMarker(MarkerOptions().position(latLng).icon(icon).title("Ambulance location"))
                val cameraPos = CameraPosition.Builder().tilt(60f).target(latLng).zoom(20f).build()
                mMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos), 1000, null)
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onConnected(p0: Bundle?) {
        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(
            mLocationRequest, locationCallback,
            Looper.myLooper()
        );
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            SHOW_ICON_IN_MAP ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    setupMap()
        }
    }

    override fun onConnectionSuspended(p0: Int) {

    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }

    override fun onLocationChanged(p0: Location?) {

    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {

    }

    override fun onProviderEnabled(p0: String?) {

    }

    override fun onProviderDisabled(p0: String?) {

    }

    override fun onPoints(result: JSONArray) {
        progressDialogUtil.hide()
        if (result.length() > 0) {
            var lst = ArrayList<SignalPoints>()
            for (i in 0..(result.length() - 1)) {
                val jNode = result.getJSONObject(i)
                if (null != jNode) {
                    var node = SignalPoints()
                    node.id = jNode.getInt("Id")
                    node.name = jNode.getString("Name")
                    node.lat = jNode.getString("Lat")
                    node.lng = jNode.getString("Lng")
                    lst.add(node)
                    mMap!!.addMarker(MarkerOptions().position(LatLng(node.lat.toDouble(), node.lng.toDouble()))).title =
                        "${node.name}"
                }
            }
        }
    }

    override fun onUpdateLight(result: String) {

    }

    override fun onUpdateRoute(result: String) {

    }

    override fun onLocationUpdateSuccess(result: String) {

    }

    private fun initUpdateLocation() {
        val updateLocation = UpdateLocationAsync()
        var jRequest = JSONObject()
        var param = JSONObject()
        jRequest.put("request", param)
        updateLocation.setOnUpdateLocationListener(this, jRequest)
    }

    private fun initUpdateLight() {
        var jRequest = JSONObject()
        var param = JSONObject()
        jRequest.put("request", param)
        val updateLight = UpdateLightAsync()
        updateLight.setOnUpdateLightListener(this, jRequest)
    }

    private fun initUpdateRoute() {
        var jRequest = JSONObject()
        var param = JSONObject()
        jRequest.put("request", param)
        val updateRoute = UpdateRouteAsync()
        updateRoute.setOnUpdateRouteListener(this, jRequest)
    }

    private fun init() {
        progressDialogUtil.show()
        val getPointAsync = GetPointAsync()
        getPointAsync.setOnPointListener(this)
    }

}
