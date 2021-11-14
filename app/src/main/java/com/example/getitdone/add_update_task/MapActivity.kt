package com.example.getitdone.add_update_task

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.provider.Settings.Global.getString
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProvider
import com.example.getitdone.R
import com.example.getitdone.home.MainActivity
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.io.IOException
import java.nio.file.attribute.AclEntry
import kotlin.random.Random

const val GEOFENCE_ID ="REMINDER GEOFENCE ID"
const val GEOFENCE_RADIUS = 500
const val GEOFENCE_EXPIRATION = 10*24*60*60*1000
const val GEOFENCE_DWELL_DELAY = 10*1000

var FRAGMENT = 0

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener{

    private val addUpdateTaskViewModel by lazy { ViewModelProvider(this).get(AddUpdateTaskViewModel::class.java)}

    lateinit var mMap:GoogleMap
    lateinit var lastLocation:Location
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var geofencingClient: GeofencingClient
    lateinit var searchBtn: TextInputLayout
    lateinit var searchEt: TextInputEditText
    var searchLocation:String =""
    var addressLine:String = ""
    val LOCATION_REQUEST_CODE = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        searchBtn = findViewById(R.id.search_lo)
        searchEt = findViewById(R.id.search_et)

        val taskTitle = intent.getStringExtra("taskTitle")
        val taskDesc = intent.getStringExtra("taskDesc")

        val addLocationBtn:Button =findViewById(R.id.add_location_btn)
        addLocationBtn.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            FRAGMENT = 1
//            intent.putExtra("fragment",1)
            if (searchLocation.isNotEmpty()&&addressLine.isNotEmpty()){
                intent.putExtra("locationName", searchLocation)
                intent.putExtra("locationAddress", addressLine)
                intent.putExtra("taskDesc",taskDesc)
                intent.putExtra("taskTitle",taskTitle)
            }else{
                intent.putExtra("locationName", "")
                intent.putExtra("locationAddress", "")
                intent.putExtra("taskDesc",taskDesc)
                intent.putExtra("taskTitle",taskTitle)
            }
            startActivity(intent)
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        searchBtn.setStartIconOnClickListener{
            getSearchedLocation()
        }

    }

    private fun getSearchedLocation() {

        searchLocation = searchEt.text.toString().trim()
        var addressList:List<Address>? = null
        if (searchLocation == ""){
            Toast.makeText(this,"Provide a location",Toast.LENGTH_LONG).show()
        }else{
            val geocoder = Geocoder(this)
            try {
                addressList = geocoder.getFromLocationName(searchLocation,1)
            }catch (e:IOException){
                e.printStackTrace()
            }
        }

        val address = addressList!![0]
        val latLng = LatLng(address.latitude,address.longitude)
        addressLine = addressList!![0].getAddressLine(0).toString()
        placeMarkerOnMap(latLng)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,12f))

        val database = Firebase.database
        val reference = database.getReference("reminders")
        val key = reference.push().key
        if (key!=null){
            val reminder = Reminder(key,address.latitude,address.longitude)
            reference.child(key).setValue(reminder)
        }
        geofencingClient = LocationServices.getGeofencingClient(this)
        createGeofencing(latLng,key!!,geofencingClient)
    }

    private fun createGeofencing(location:LatLng,key:String,geofencingClient: GeofencingClient){
        val geofence = Geofence.Builder()
            .setRequestId(GEOFENCE_ID)
            .setCircularRegion(location.latitude,location.longitude, GEOFENCE_RADIUS.toFloat())
            .setExpirationDuration(GEOFENCE_EXPIRATION.toLong())
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_DWELL)
            .setLoiteringDelay(GEOFENCE_DWELL_DELAY)
            .build()

        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        val intent = Intent(this, GeofenceReceiver::class.java)
            .putExtra("key",key)
            .putExtra("message","You Have A Task To Be Done Around Here!")

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        geofencingClient.addGeofences(geofencingRequest,pendingIntent)

    }



    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true

        mMap.setOnMarkerClickListener(this)

        setUpMap()

    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission
                .ACCESS_FINE_LOCATION),LOCATION_REQUEST_CODE)
            return
        }

        mMap.isMyLocationEnabled = true

        fusedLocationProviderClient.lastLocation.addOnSuccessListener(this) { location ->

            if (location != null){
                lastLocation = location
                val currentLatLng = LatLng(location.latitude,location.longitude)
                placeMarkerOnMap(currentLatLng)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,30f))
            }

        }
    }



    private fun placeMarkerOnMap(currentLatLng: LatLng) {
        val markerOptions = MarkerOptions().position(currentLatLng)
        markerOptions.title("I'm Here")
        mMap.addMarker(markerOptions)
    }

    override fun onMarkerClick(p0: Marker) = false

    companion object{
        fun showNotification(context: Context,message: String){
            val CHANNEL_ID = "REMINDER_NOTIFICATION_CHANNEL"
            var notificationId = 1224
            notificationId += Random(notificationId).nextInt(1,30)

            val notificationBuilder = NotificationCompat.Builder(context.applicationContext,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_location_alarm)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            val notificationManger = context.getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply { description = context.getString(R.string.app_name) }

                notificationManger.createNotificationChannel(channel)
            }

            notificationManger.notify(notificationId,notificationBuilder.build())
        }
    }

}