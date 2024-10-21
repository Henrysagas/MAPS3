package com.example.maps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import android.util.Log

import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.model.DirectionsResult
import com.google.maps.model.TravelMode
import com.google.maps.android.PolyUtil
import com.google.android.gms.common.api.ApiException

import com.google.android.gms.maps.model.PolylineOptions

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.maps.databinding.ActivityMapsBinding
import com.google.android.gms.common.api.Status;


import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private var currentMapType = GoogleMap.MAP_TYPE_NORMAL
    private lateinit var placesClient: PlacesClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Obtener el SupportMapFragment y notificar cuando el mapa esté listo para ser usado
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Inicializa la API de Places
        Places.initialize(applicationContext, "TU_API_KEY_AQUI")
        placesClient = Places.createClient(this)

        // Botón para cambiar el tipo de mapa
        val btnChangeMapType = findViewById<Button>(R.id.btnChangeMapType)
        btnChangeMapType.setOnClickListener {
            changeMapType()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Añadir un marcador en Sídney y mover la cámara
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 12f))
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true

        // Habilitar "Mi ubicación" si los permisos están otorgados
        enableMyLocation()
    }

    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation()
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun changeMapType() {
        currentMapType = (currentMapType + 1) % 4
        when (currentMapType) {
            0 -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                Toast.makeText(this, "Mapa Normal", Toast.LENGTH_SHORT).show()
            }
            1 -> {
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                Toast.makeText(this, "Mapa Satélite", Toast.LENGTH_SHORT).show()
            }
            2 -> {
                mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                Toast.makeText(this, "Mapa Terreno", Toast.LENGTH_SHORT).show()
            }
            3 -> {
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                Toast.makeText(this, "Mapa Híbrido", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun searchNearbyPlaces() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val placeFields = listOf(Place.Field.NAME, Place.Field.LAT_LNG)
            val request = FindCurrentPlaceRequest.newInstance(placeFields)

            placesClient.findCurrentPlace(request)
                .addOnSuccessListener { response ->
                    for (placeLikelihood in response.placeLikelihoods) {
                        val place = placeLikelihood.place
                        mMap.addMarker(
                            MarkerOptions()
                                .position(place.latLng!!)
                                .title(place.name)
                        )
                    }
                }
                .addOnFailureListener { exception ->
                    if (exception is ApiException) {
                        Log.e("PlacesAPI", "Place not found: ${exception.statusCode}")
                    }
                }
        }
    }

    private fun drawRoute(origin: LatLng, destination: LatLng) {
        val context = GeoApiContext.Builder()
            .apiKey("TU_API_KEY_AQUI")
            .build()

        Thread {
            try {
                val result = DirectionsApi.newRequest(context)
                    .origin(com.google.maps.model.LatLng(origin.latitude, origin.longitude))
                    .destination(com.google.maps.model.LatLng(destination.latitude, destination.longitude))
                    .mode(TravelMode.DRIVING)
                    .await()

                runOnUiThread {
                    if (result.routes.isNotEmpty()) {
                        val decodedPath = PolyUtil.decode(result.routes[0].overviewPolyline.encodedPath)
                        mMap.addPolyline(PolylineOptions().addAll(decodedPath))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }
}
