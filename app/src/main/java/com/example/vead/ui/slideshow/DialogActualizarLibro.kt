package com.example.vead.ui.slideshow

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import com.example.vead.R
import com.example.vead.data.entities.Book
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DialogActualizarLibro(
    private val book: Book,
    private val onLibroActualizado: (Book) -> Unit
) : DialogFragment() {

    private lateinit var etTitulo: EditText
    private lateinit var etAutor: EditText
    private lateinit var etGenero: EditText
    private lateinit var etNumeroCopias: EditText
    private lateinit var etUbicacion: EditText
    private lateinit var btnObtenerUbicacion: Button
    private lateinit var btnTomarFoto: Button
    private lateinit var imgPortada: ImageView

    // Will store the final path/URI to the photo
    private var photoUri: Uri? = null
    private var coverUrl: String = ""

    private var gpsLocation: String = ""

    // Activity Result to get location permission
    private val requestLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                getUserLocation()
            } else {
                Toast.makeText(requireContext(), "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
            }
        }

    // Activity Result to get camera permission
    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                launchCamera()
            } else {
                Toast.makeText(requireContext(), "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
            }
        }

    // Activity Result to capture the photo
    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
            if (success) {
                // If the photo was successfully taken, show it in the ImageView
                photoUri?.let { uri ->
                    imgPortada.setImageURI(uri)
                    coverUrl = uri.toString() // Store the URI path for the Book
                }
            } else {
                Toast.makeText(requireContext(), "Error al tomar la foto", Toast.LENGTH_SHORT).show()
            }
        }

    @SuppressLint("MissingInflatedId")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_actualizar_libro, null)

        // Initialize Views
        etTitulo = view.findViewById(R.id.etTituloActualizar)
        etAutor = view.findViewById(R.id.etAutorActualizar)
        etGenero = view.findViewById(R.id.etGeneroActualizar)
        etNumeroCopias = view.findViewById(R.id.etNumeroCopiasActualizar)
        etUbicacion = view.findViewById(R.id.etUbicacionActualizar)
        btnObtenerUbicacion = view.findViewById(R.id.btnObtenerUbicacionActualizar)
        btnTomarFoto = view.findViewById(R.id.btnTomarFotoActualizar)
        imgPortada = view.findViewById(R.id.imgPortadaActualizar)

        // When user clicks "Obtener Ubicación GPS"
        btnObtenerUbicacion.setOnClickListener {
            checkLocationPermissionAndFetch()
        }

        // When user clicks "Tomar Foto"
        btnTomarFoto.setOnClickListener {
            checkCameraPermissionAndLaunch()
        }



        // Pre-cargar datos del libro
        etTitulo.setText(book.title)
        etAutor.setText(book.author)
        etGenero.setText(book.genre)
        etNumeroCopias.setText(book.copiesNumber.toString())
        etUbicacion.setText(book.location)

        builder.setView(view)
            .setTitle("Actualizar Libro")
            .setPositiveButton("Guardar") { _, _ ->
                val titulo = etTitulo.text.toString()
                val autor = etAutor.text.toString()
                val genero = etGenero.text.toString()
                val copias = etNumeroCopias.text.toString().toIntOrNull() ?: 0
                val ubicacion = if (gpsLocation.isNotEmpty()) {
                    // If we have a GPS fix, use that for "location"
                    gpsLocation
                } else {
                    // Otherwise, use the user’s typed text
                    etUbicacion.text.toString()
                }

                val book = Book(
                    title = titulo,
                    author = autor,
                    genre = genero,
                    copiesNumber = copias,
                    location = ubicacion,
                    coverUrl = coverUrl // The camera URI path
                )
                onLibroActualizado(book)
            }
            .setNegativeButton("Cancelar", null)

        return builder.create()
    }

    /**
     * Checks if we have location permission; if not, requests it. If yes, calls getUserLocation().
     */
    private fun checkLocationPermissionAndFetch() {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        if (ContextCompat.checkSelfPermission(requireContext(), permission) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            getUserLocation()
        } else {
            requestLocationPermissionLauncher.launch(permission)
        }
    }

    /**
     * Uses FusedLocationProviderClient to get the user's last known location.
     */
    private fun getUserLocation() {

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


        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Request high accuracy GPS-only location updates
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000).build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    if (location != null) {
                        gpsLocation = "${location.latitude}, ${location.longitude}"
                        //Toast.makeText(intent, "Ubicación obtenida: $gpsLocation (GPS Only)", Toast.LENGTH_SHORT).show()
                        fusedLocationClient.removeLocationUpdates(this) // Stop updates after first successful fix
                        break
                    }
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    gpsLocation = "${location.latitude}, ${location.longitude}"

                    etUbicacion.setText(gpsLocation)

                } else {
                    Toast.makeText(requireContext(), "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al obtener la ubicación", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Checks if we have camera permission; if not, requests it. If yes, calls launchCamera().
     */
    private fun checkCameraPermissionAndLaunch() {
        val permission = Manifest.permission.CAMERA
        if (ContextCompat.checkSelfPermission(requireContext(), permission) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            launchCamera()
        } else {
            requestCameraPermissionLauncher.launch(permission)
        }
    }

    /**
     * Creates a temporary file for storing the photo, then launches the camera app.
     */
    private fun launchCamera() {
        val photoFile = createImageFile()
        photoUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            photoFile
        )
        takePictureLauncher.launch(photoUri)
    }

    /**
     * Creates an empty file in the app's external files directory to store the image.
     */
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("IMG_${timeStamp}_", ".jpg", storageDir)
    }
}
