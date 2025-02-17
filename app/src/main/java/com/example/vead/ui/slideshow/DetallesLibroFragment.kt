package com.example.vead.ui.slideshow

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.vead.R
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

class DetallesLibroFragment : Fragment(), OnMapReadyCallback {

    private lateinit var tvTitulo: TextView
    private lateinit var tvAutor: TextView
    private lateinit var tvGenero: TextView
    private lateinit var tvNumeroCopias: TextView
    private lateinit var tvUbicacion: TextView

    private lateinit var mapView: MapView
    private var googleMap: GoogleMap? = null

    // Book's coordinates
    private var bookLatLng: LatLng? = null
    // Device location
    private var userLatLng: LatLng? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_detalles_libro, container, false)

        tvTitulo = root.findViewById(R.id.tvTitulo)
        tvAutor = root.findViewById(R.id.tvAutor)
        tvGenero = root.findViewById(R.id.tvGenero)
        tvNumeroCopias = root.findViewById(R.id.tvNumeroCopias)
        tvUbicacion = root.findViewById(R.id.tvUbicacion)
        mapView = root.findViewById(R.id.mapView)


        // Obtener datos del libro desde los argumentos
        val titulo = arguments?.getString("titulo") ?: ""
        val autor = arguments?.getString("autor") ?: ""
        val genero = arguments?.getString("genero") ?: ""
        val numeroCopias = arguments?.getInt("numeroCopias") ?: 0
        val ubicacion = arguments?.getString("ubicacion") ?: ""

        // Establecer valores en las vistas
        tvTitulo.text = titulo
        tvAutor.text = "Autor: $autor"
        tvGenero.text = "Género: $genero"
        tvNumeroCopias.text = "Número de copias: $numeroCopias"
        tvUbicacion.text = "Ubicación: $ubicacion"


        val coords = ubicacion.split(",")
        if (coords.size == 2) {
            val lat = coords[0].trim().toDoubleOrNull()
            val lng = coords[1].trim().toDoubleOrNull()
            if (lat != null && lng != null) {
                bookLatLng = LatLng(lat, lng)
            }
        }

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        return root
    }

    // Called when the map is ready to be used
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap?.uiSettings?.isZoomControlsEnabled = true

        // Check GPS permission & fetch user location
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            getUserLocationAndDrawRoute()
        } else {
            Toast.makeText(requireContext(), "GPS Permission not granted", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Gets the user's location via FusedLocationProviderClient,
     * then draws markers and route on the map.
     */
    private fun getUserLocationAndDrawRoute() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    userLatLng = LatLng(location.latitude, location.longitude)
                    // Show user location marker
                    googleMap?.addMarker(MarkerOptions().position(userLatLng!!).title("Tu ubicación"))

                    // Show book location marker
                    bookLatLng?.let { latLng ->
                        googleMap?.addMarker(MarkerOptions().position(latLng).title("Ubicación del libro"))

                        // Adjust camera to show both markers
                        val bounds = com.google.android.gms.maps.model.LatLngBounds.Builder()
                            .include(userLatLng!!)
                            .include(latLng)
                            .build()
                        googleMap?.moveCamera(
                            CameraUpdateFactory.newLatLngBounds(bounds, 100)
                        )

                        // Draw the route between user and book location
                        drawRoute(userLatLng!!, latLng)
                    }
                } else {
                    Toast.makeText(requireContext(), "No se pudo obtener ubicación actual", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al obtener la ubicación", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * This example uses a very simplistic approach to "draw" a line
     * directly from user to book. For a real route, see below about Directions API.
     */
    private fun drawRoute(start: LatLng, end: LatLng) {
        val polylineOptions = PolylineOptions()
            .add(start)
            .add(end)
            .width(5f)
            .color(ContextCompat.getColor(requireContext(), R.color.purple_500))
        googleMap?.addPolyline(polylineOptions)
    }

    // MapView lifecycle management
    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }
    override fun onPause() {
        mapView.onPause()
        super.onPause()
    }
    override fun onDestroy() {
        mapView.onDestroy()
        super.onDestroy()
    }
    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }
}
