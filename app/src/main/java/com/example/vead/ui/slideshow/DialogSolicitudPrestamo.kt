package com.example.vead.ui.slideshow

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.example.vead.R
import com.example.vead.data.entities.SolicitudPrestamo
import java.util.Calendar

class DialogSolicitudPrestamo(
    private val tituloLibro: String,
    private val registroEstudiante: String,
    private val onSolicitudCreada: (SolicitudPrestamo) -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_solicitud_prestamo, null)

        val etFechaPrestamo = view.findViewById<EditText>(R.id.etFechaPrestamo)
        val etFechaDevolucion = view.findViewById<EditText>(R.id.etFechaDevolucion)

        // Configurar el calendario para las fechas
        etFechaPrestamo.setOnClickListener {
            mostrarSelectorDeFecha { fecha -> etFechaPrestamo.setText(fecha) }
        }

        etFechaDevolucion.setOnClickListener {
            mostrarSelectorDeFecha { fecha -> etFechaDevolucion.setText(fecha) }
        }

        builder.setView(view)
            .setTitle("Solicitud de Préstamo")
            .setPositiveButton("Solicitar") { _, _ ->
                val folio = (1000..9999).random() // Generar folio aleatorio
                val solicitud = SolicitudPrestamo(
                    folio = folio,
                    registroEstudiante = registroEstudiante,
                    tituloLibro = tituloLibro,
                    fechaPrestamo = etFechaPrestamo.text.toString(),
                    fechaDevolucion = etFechaDevolucion.text.toString(),
                    estado = "Solicitado"
                )
                onSolicitudCreada(solicitud)
            }
            .setNegativeButton("Cancelar", null)

        return builder.create()
    }

    private fun mostrarSelectorDeFecha(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val fecha = "$year-${month + 1}-$dayOfMonth"
                onDateSelected(fecha)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}
