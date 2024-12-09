package com.example.vead.ui.multas

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import com.example.vead.R
import com.example.vead.data.entities.Multa
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DialogAgregarMulta(
    private val registrosEstudiantes: List<String>,
    private val onMultaAgregada: (Multa) -> Unit
) : DialogFragment() {

    @SuppressLint("MissingInflatedId")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_agregar_multa, null)

        val spinnerEstudiantes = view.findViewById<Spinner>(R.id.spinnerEstudiantes)
        val lvGravedad = view.findViewById<ListView>(R.id.lvGravedad)
        val etDescripcion = view.findViewById<EditText>(R.id.etDescripcion)
        val etFecha = view.findViewById<EditText>(R.id.etFecha)
        val etFirma = view.findViewById<EditText>(R.id.etFirma)


        etFecha.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    etFecha.setText("$year-${month + 1}-$dayOfMonth")
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        val adapterSpinner = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, registrosEstudiantes)
        spinnerEstudiantes.adapter = adapterSpinner

        val opcionesGravedad = listOf("Leve", "Medio", "Grave")
        val adapterListView = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_single_choice, opcionesGravedad)
        lvGravedad.adapter = adapterListView

        builder.setView(view)
            .setTitle("Agregar Multa")
            .setPositiveButton("Agregar") { _, _ ->
                val folio = (1000..9999).random()
                val multa = Multa(
                    folio = folio,
                    fecha = etFecha.text.toString(),
                    registroEstudiante = spinnerEstudiantes.selectedItem.toString(),
                    gravedad = lvGravedad.checkedItemPosition.let { opcionesGravedad.getOrNull(it) } ?: "",
                    descripcion = etDescripcion.text.toString(),
                    firma = etFirma.text.toString()
                )
                onMultaAgregada(multa)
            }
            .setNegativeButton("Cancelar", null)

        return builder.create()
    }
}
