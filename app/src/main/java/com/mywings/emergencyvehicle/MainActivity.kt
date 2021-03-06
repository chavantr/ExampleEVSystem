package com.mywings.emergencyvehicle

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
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
import com.mywings.emergencyvehicle.models.UserInfoHolder
import com.mywings.emergencyvehicle.process.*
import com.mywings.emergencyvehicle.routes.DirectionsJSONParser
import com.mywings.emergencyvehicle.routes.JsonUtil
import com.mywings.messmanagementsystem.routes.Constants
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


class MainActivity : AppCompatActivity(),
    OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, LocationListener, OnPointListener, OnUpdateLightListener,
    OnUpdateRouteListener, OnUpdateLocationListener {


    private var mMap: GoogleMap? = null
    private val SHOW_ICON_IN_MAP = 49
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mLocationRequest: LocationRequest? = null
    private var latLng: LatLng = LatLng(18.515665, 73.924090)
    private lateinit var latLngH: LatLng
    private var locationManager: LocationManager? = null
    private lateinit var cPosition: Marker
    private lateinit var marker: Marker
    private lateinit var circle: Circle
    private lateinit var progressDialogUtil: ProgressDialogUtil


    private lateinit var jsonUtil: JsonUtil
    private lateinit var nsource: String
    private lateinit var ndest: String
    private var destlat: Double = 0.0
    private var destlng: Double = 0.0
    private var srctlat: Double = 0.0
    private var srclng: Double = 0.0

    private var flag: Boolean = false

    private lateinit var timer: Timer

    private lateinit var text: String

    private var signal: String = ""

    private var direction: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var frame = activity_place_map as SupportMapFragment
        frame.getMapAsync(this)

        progressDialogUtil = ProgressDialogUtil(this)

        timer = Timer()

        imgForwad.setOnClickListener {

            if (flag) {
                direction = "Straight"

                text = "Signal:$signal/Direction:$direction"

                lblName.text = text
                initUpdateRoute(5)
            } else {
                Toast.makeText(this@MainActivity, "Please select hospital to notify", Toast.LENGTH_LONG).show()
            }

        }

        imgForwadRound.setOnClickListener {
            if (flag) {
                //imgLeftRound.setImageResource(R.drawable.ic_traffic_black_24dp)
                //imgForwadRound.setImageResource(R.drawable.ic_traffic_green_24dp)
                //imgRightRound.setImageResource(R.drawable.ic_traffic_black_24dp)
                signal = "Straight"

                text = "Signal:$signal/Direction:$direction"

                lblName.text = text
                initUpdateLight(2)
                //imgForwadRound.setImageDrawable(null)
            } else {
                Toast.makeText(this@MainActivity, "Please select hospital to notify", Toast.LENGTH_LONG).show()
            }
        }

        imgLeft.setOnClickListener {
            if (flag) {

                direction = "Left"

                text = "Signal:$signal/Direction:$direction"

                lblName.text = text

                initUpdateRoute(4)
            } else {
                Toast.makeText(this@MainActivity, "Please select hospital to notify", Toast.LENGTH_LONG).show()
            }
        }

        imgLeftRound.setOnClickListener {
            if (flag) {
                // imgLeftRound.setImageResource(R.drawable.ic_traffic_green_24dp)
                //  imgForwadRound.setImageResource(R.drawable.ic_traffic_black_24dp)
                //  imgRightRound.setImageResource(R.drawable.ic_traffic_black_24dp)
                signal = "Left"

                text = "Signal:$signal/Direction:$direction"

                lblName.text = text
                initUpdateLight(1)
            } else {
                Toast.makeText(this@MainActivity, "Please select hospital to notify", Toast.LENGTH_LONG).show()
            }
        }
        imgRight.setOnClickListener {
            if (flag) {
                direction = "Right"

                text = "Signal:$signal/Direction:$direction"

                lblName.text = text
                initUpdateRoute(6)
            } else {
                Toast.makeText(this@MainActivity, "Please select hospital to notify", Toast.LENGTH_LONG).show()
            }
        }

        imgRightRound.setOnClickListener {
            if (flag) {
                //  imgLeftRound.setImageResource(R.drawable.ic_traffic_black_24dp)
                //    imgForwadRound.setImageResource(R.drawable.ic_traffic_black_24dp)
                //    imgRightRound.setImageResource(R.drawable.ic_traffic_green_24dp)

                signal = "Right"


                text = "Signal:$signal/Direction:$direction"

                lblName.text = text

                initUpdateLight(3)
            } else {
                Toast.makeText(this@MainActivity, "Please select hospital to notify", Toast.LENGTH_LONG).show()
            }
        }

        jsonUtil = JsonUtil()
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


    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.dashboard, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this@MainActivity, SelectHospitalActivity::class.java)
                startActivityForResult(intent, 1001)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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
                    node.hid = jNode.getInt("HId")
                    lst.add(node)
                    mMap!!.addMarker(MarkerOptions().position(LatLng(node.lat.toDouble(), node.lng.toDouble()))).title =
                        "${node.name}"
                }
            }
            UserInfoHolder.getInstance().signalPoints = lst
        }
    }

    override fun onUpdateLight(result: String) {
        progressDialogUtil.hide()
    }

    override fun onUpdateRoute(result: String) {
        progressDialogUtil.hide()
    }

    override fun onLocationUpdateSuccess(result: String) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1001) {
                nsource = "Ambulance location"
                ndest = UserInfoHolder.getInstance().hospital.name
                val str = getDirectionsUrl(
                    latLng,
                    LatLng(
                        UserInfoHolder.getInstance().hospital.lat.toDouble(),
                        UserInfoHolder.getInstance().hospital.lng.toDouble()
                    )
                )
                val downloadTask = DownloadTask()
                downloadTask.execute(str)

                latLngH = LatLng(
                    UserInfoHolder.getInstance().hospital.lat.toDouble(),
                    UserInfoHolder.getInstance().hospital.lng.toDouble()
                )

                flag = true

                timer.schedule(CheckDistance(), 10000, 1000 * 60)


            }
        }
    }

    private fun check() {
        if (UserInfoHolder.getInstance().signalPoints.isNotEmpty()) {
            for (i in UserInfoHolder.getInstance().signalPoints.indices) {
                if (UserInfoHolder.getInstance().signalPoints[i].hid == UserInfoHolder.getInstance().hospital.id) {
                    var locationF = Location("Ambulance location")
                    locationF.latitude = latLng.latitude
                    locationF.longitude = latLng.longitude
                    var locationFF = Location("Signal location")
                    locationFF.latitude = UserInfoHolder.getInstance().signalPoints[i].lat.toDouble()
                    locationFF.longitude = UserInfoHolder.getInstance().signalPoints[i].lng.toDouble()
                    if ((locationF.distanceTo(locationFF) / 1000) <= 1) {
                        val updateStatus = UpdateStatus()
                        updateStatus.executeOnExecutor(
                            AsyncTask.THREAD_POOL_EXECUTOR,
                            UserInfoHolder.getInstance().signalPoints[i]!!.id
                        )
                    }
                }
            }
        }
    }


    private fun getId(): Int {
        if (UserInfoHolder.getInstance().signalPoints.isNotEmpty()) {
            for (i in UserInfoHolder.getInstance().signalPoints.indices) {
                if (UserInfoHolder.getInstance().signalPoints[i].hid == UserInfoHolder.getInstance().hospital.id) {
                    return UserInfoHolder.getInstance().signalPoints[i].id
                }
            }
        }
        return 0
    }


    inner class CheckDistance : TimerTask() {
        override fun run() {
            check()
        }
    }


    private fun initUpdateLocation() {
        val updateLocation = UpdateLocationAsync()
        var jRequest = JSONObject()

        updateLocation.setOnUpdateLocationListener(this, jRequest)
    }

    private fun initUpdateLight(light: Int?) {
        progressDialogUtil.show()
        var jRequest = JSONObject()
        jRequest.put("light", light)
        jRequest.put("id", getId())
        val updateLight = UpdateLightAsync()
        updateLight.setOnUpdateLightListener(this, jRequest)
    }

    private fun initUpdateRoute(direction: Int?) {
        progressDialogUtil.show()
        var jRequest = JSONObject()
        jRequest.put("direction", direction)
        jRequest.put("id", getId())
        val updateRoute = UpdateRouteAsync()
        updateRoute.setOnUpdateRouteListener(this, jRequest)
    }

    private fun init() {
        progressDialogUtil.show()
        val getPointAsync = GetPointAsync()
        getPointAsync.setOnPointListener(this)
    }

    // IMP

    private var key = "&key=AIzaSyClCN7T0VPX7MIoOJEMA3W9JLXhV_S7yx4"

    private fun getDirectionsUrl(origin: LatLng, dest: LatLng): String {
        val strOrigin = ("origin=" + origin.latitude + ","
                + origin.longitude)
        val strDest = "destination=" + dest.latitude + "," + dest.longitude
        val sensor = "sensor=false"

        val parameters = "$strOrigin&$strDest&$sensor$key"
        val output = "json"
        return ("https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + parameters)
    }

    private inner class DownloadTask : AsyncTask<String, Void, String>() {

        // Downloading data in non-ui thread
        override fun doInBackground(vararg url: String): String {

            // For storing data from web service
            var data = ""

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0])
            } catch (e: Exception) {
                Log.d("Background Task", e.toString())

            }

            return data
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        override fun onPostExecute(result: String) {
            super.onPostExecute(result)

            val parserTask = ParserTask(mMap!!)

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result)

        }
    }

    /** A method to download json data from url  */
    @Throws(IOException::class)
    private fun downloadUrl(strUrl: String): String {
        var data = ""
        var iStream: InputStream? = null
        var urlConnection: HttpURLConnection? = null
        try {
            val url = URL(strUrl)

            // Creating an http connection to communicate with url
            urlConnection = url.openConnection() as HttpURLConnection

            // Connecting to url
            urlConnection.connect()

            iStream = urlConnection.inputStream

            data = jsonUtil.convertStreamToString(iStream)

        } catch (e: Exception) {

        } finally {
            iStream!!.close()
            urlConnection!!.disconnect()
        }
        return data
    }

    /** A class to parse the Google Places in JSON format  */
    private inner class ParserTask(internal var map: GoogleMap?) :
        AsyncTask<String, Int, List<List<HashMap<String, String>>>>() {

        // Parsing the data in non-ui thread
        override fun doInBackground(
            vararg jsonData: String
        ): List<List<HashMap<String, String>>>? {

            val jObject: JSONObject
            var jArray: JSONArray
            var routes: List<List<HashMap<String, String>>>? = null

            try {
                jObject = JSONObject(jsonData[0])
                val parser = DirectionsJSONParser()
                routes = parser.parse(jObject)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return routes
        }

        // Executes in UI thread, after the parsing process
        override fun onPostExecute(result: List<List<HashMap<String, String>>>) {

            //progressDialogUtil.show()

            var points: java.util.ArrayList<LatLng>? = null

            var lineOptions: PolylineOptions? = null

            // MarkerOptions markerOptions = new MarkerOptions();

            // Traversing through all the routes
            for (i in result.indices) {
                points = java.util.ArrayList()
                lineOptions = PolylineOptions()
                // Fetching i-th route
                val path = result[i]
                // Fetching all the points in i-th route
                for (j in path.indices) {
                    // lineOptions = new PolylineOptions();
                    val point = path[j]
                    val lat = java.lang.Double.parseDouble(point[Constants.LAT]!!)
                    val lng = java.lang.Double.parseDouble(point[Constants.LNG]!!)
                    val position = LatLng(lat, lng)
                    points.add(position)
                }
                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points)
                lineOptions.width(9f)
                lineOptions.color(Color.RED)
            }

            // Drawing polyline in the Google Map for the i-th route

            //map!!.clear()

            if (null != lineOptions) {
                map!!.addPolyline(lineOptions)
                setStartPosition(srctlat, srclng)
                setDestPosition(destlat, destlng)
                if (map != null) {
                    fixZoom(lineOptions.points)
                }

                progressDialogUtil.hide()

            } else {
                Toast.makeText(
                    this@MainActivity,
                    "Enable to draw routes, Please try again",
                    Toast.LENGTH_LONG
                ).show()
            }

        }
    }

    /**
     * @param lat
     * @param lng
     */
    private fun setStartPosition(lat: Double, lng: Double) {
        var startmark = mMap!!.addMarker(
            MarkerOptions()
                .position(LatLng(lat, lng))
                .title(nsource)
                .snippet("")
        )
        startmark.tag = 1
    }

    /**
     * @param lat
     * @param lng
     */
    private fun setDestPosition(lat: Double, lng: Double) {
        var destmark = mMap!!.addMarker(
            MarkerOptions()
                .position(LatLng(lat, lng))
                .title(ndest)
                .snippet("")
        )

        destmark.tag = 1
    }


    private fun fixZoom(points: List<LatLng>) {
        val bc = LatLngBounds.Builder()
        for (item in points) {
            bc.include(item)
        }
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), 90))
    }

}
