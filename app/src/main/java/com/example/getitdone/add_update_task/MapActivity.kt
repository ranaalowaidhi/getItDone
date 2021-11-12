package com.example.getitdone.add_update_task

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.example.getitdone.R
import com.example.getitdone.database.Task
import com.example.getitdone.home.KEY_ID
import com.example.getitdone.home.MainActivity
import com.example.getitdone.home.TaskFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.MainScope
import java.io.IOException
const val ADDRESS_KEY = "address information"
@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener{

    private val addUpdateTaskViewModel by lazy { ViewModelProvider(this).get(AddUpdateTaskViewModel::class.java)}

    lateinit var mMap:GoogleMap
    lateinit var lastLocation:Location
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var searchBtn: TextInputLayout
    lateinit var searchEt: TextInputEditText
    var searchLocation:String =""
    var addressLine:String = ""

    companion object{
        val LOCATION_REQUEST_CODE = 1
    }

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
            intent.putExtra("fragment",1)
            if (searchLocation.isNotEmpty()&&addressLine.isNotEmpty()){
                intent.putExtra("locationName", searchLocation)
                intent.putExtra("locationAddress", addressLine)
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

    }

    override fun onStop() {
        super.onStop()
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

}