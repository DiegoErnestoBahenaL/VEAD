package com.example.vead.ui.slideshow

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vead.R
import com.example.vead.data.entities.Book
import com.example.vead.data.entities.Request
import com.example.vead.data.repositories.BookRepository
import com.example.vead.data.repositories.RequestRepository
import com.example.vead.data.repositories.UserRepository
import com.example.vead.databinding.FragmentSlideshowBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Locale

class SlideshowFragment : Fragment() {

    private val bookRepo = BookRepository()
    private val requestRepo = RequestRepository()

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LibroAdapter

    private var tipoUsuario: String? = null

    private var email: String? = null
    private var code: String? = null


    private var _binding: FragmentSlideshowBinding? = null


    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val slideshowViewModel =
            ViewModelProvider(this).get(SlideshowViewModel::class.java)

        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val root: View = binding.root

        tipoUsuario = activity?.intent?.getStringExtra("UserType")
        email = activity?.intent?.getStringExtra("Email")
        code = (requireActivity().intent.getLongExtra("Code", 0) ?: "").toString()



        recyclerView = root.findViewById(R.id.recyclerLibros)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        actualizarListaLibros()

        // Configura el botón de agregar (solo para administradores)
        val btnAgregarLibro = root.findViewById<Button>(R.id.btnAgregarLibro)
        if (tipoUsuario == "Administrador") {
            btnAgregarLibro.visibility = View.VISIBLE
            btnAgregarLibro.setOnClickListener {
                mostrarDialogAgregarLibro()
            }
        } else {
            btnAgregarLibro.visibility = View.GONE
        }


        return root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun actualizarListaLibros() {

        bookRepo.getAllBooks { data ->
            adapter = LibroAdapter(
                data,
                tipoUsuario ?: "",
                onSolicitarClick = { libro -> solicitarPrestamo(libro) },
                onActualizarClick = { libro -> mostrarDialogActualizarLibro(libro) },
                onEliminarClick = { libro -> mostrarDialogConfirmarEliminacion(libro) }
            )
            recyclerView.adapter = adapter
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun solicitarPrestamo(book: Book) {
        val dialog = DialogSolicitudPrestamo(
            tituloLibro = book.title,
            registroEstudiante = code!!.toLong()
        ) { solicitud ->
            requestRepo.addRequest(solicitud){ successful ->
                if (successful) {

                    // Schedule alarm with notification
                    scheduleReturnReminder(solicitud)

                } else {
                    Toast.makeText(requireContext(), "Hubo un error al crear la solicitud.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        dialog.show(parentFragmentManager, "DialogSolicitudPrestamo")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun scheduleReturnReminder(request: Request) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        try {
            val dueDate = sdf.parse(request.requestDate)
            if (dueDate != null) {
                calendar.time = dueDate

                val current = LocalDateTime.now()



                calendar.set(Calendar.HOUR_OF_DAY, current.hour)
                calendar.set(Calendar.MINUTE, current.minute)
                calendar.set(Calendar.SECOND, current.second + 15)

                val triggerTimeMillis = calendar.timeInMillis
                if (triggerTimeMillis > System.currentTimeMillis()) {

                    val intent = Intent(requireContext(), ReturnReminderReceiver::class.java).apply {
                        putExtra("bookTitle", request.bookTitle)
                    }

                    //using folio because is unique/random for each request
                    val pendingIntent = PendingIntent.getBroadcast(
                        requireContext(),
                        request.folio,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )


                    val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        triggerTimeMillis,
                        pendingIntent
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun mostrarDialogAgregarLibro() {
        val dialog = DialogAgregarLibro { libro ->

            bookRepo.addBook(libro){ successful ->
                if (successful){
                    actualizarListaLibros()
                }
                else {
                    Toast.makeText(requireContext(), "Hubo un error al agregar el libro.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        dialog.show(parentFragmentManager, "DialogAgregarLibro")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun mostrarDialogActualizarLibro(book: Book) {
        val dialog = DialogActualizarLibro(book) { libroActualizado ->

            bookRepo.updateBookByTitle(book.title, libroActualizado){ successful ->
                if (successful){
                    actualizarListaLibros()
                }
                else {
                    Toast.makeText(requireContext(), "Hubo un error al actualizar el libro.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        dialog.show(parentFragmentManager, "DialogActualizarLibro")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun mostrarDialogConfirmarEliminacion(book: Book) {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmar eliminación")
            .setMessage("¿Estás seguro de que deseas eliminar '${book.title}'?")
            .setPositiveButton("Sí") { _, _ ->

                bookRepo.deleteBookByTitle(book.title){ successful ->
                    if (successful){
                        actualizarListaLibros()
                    }
                    else {
                        Toast.makeText(requireContext(), "Hubo un error al eliminar el libro.", Toast.LENGTH_SHORT).show()

                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}