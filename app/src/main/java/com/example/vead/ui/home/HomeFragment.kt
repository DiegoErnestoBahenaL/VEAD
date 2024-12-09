package com.example.vead.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.vead.R
import com.example.vead.data.entities.Administrador
import com.example.vead.data.entities.Estudiante
import com.example.vead.data.repositories.AdministradorRepository
import com.example.vead.data.repositories.EstudianteRepository
import com.example.vead.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {


    private lateinit var email: String
    private lateinit var tipoUsuario: String

    // Campos comunes de la clase Usuario
    private lateinit var editEmail: EditText
    private lateinit var editContrasena: EditText
    private lateinit var editNombre: EditText
    private lateinit var editFechaCreacion: EditText
    private lateinit var editFechaExpiracion: EditText
    private lateinit var editTipo: EditText

    // Campos para Administrador
    private lateinit var textCodigoTrabajador: TextView
    private lateinit var editCodigoTrabajador: EditText
    private lateinit var textRFC: TextView
    private lateinit var editRFC: EditText
    private lateinit var textNombreSupervisor: TextView
    private lateinit var editNombreSupervisor: EditText
    private lateinit var textTelefono: TextView
    private lateinit var editTelefono: EditText
    private lateinit var textTurno: TextView
    private lateinit var editTurno: EditText

    // Campos para Estudiante
    private lateinit var textRegistro: TextView
    private lateinit var editRegistro: EditText
    private lateinit var textGrado: TextView
    private lateinit var editGrado: EditText
    private lateinit var textCarrera: TextView
    private lateinit var editCarrera: EditText
    private lateinit var textNombreTutor: TextView
    private lateinit var editNombreTutor: EditText
    private lateinit var textGrupo: TextView
    private lateinit var editGrupo: EditText

    private lateinit var buttonActualizar : Button

    private var administrador : Administrador? = null
    private var estudiante : Estudiante? = null

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Recuperar los extras del Intent
        email = requireActivity().intent.getStringExtra("Email") ?: ""
        tipoUsuario = requireActivity().intent.getStringExtra("TipoUsuario") ?: ""

        if (tipoUsuario == "Administrador") {
            administrador = AdministradorRepository().buscarUno(email)
        }
        else if (tipoUsuario == "Estudiante") {
            estudiante = EstudianteRepository().buscarUno(email)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        inicializarCampos(root)

        // Inicializar el formulario basado en el tipo de usuario
        if (tipoUsuario == "Administrador") {
            configurarFormularioAdministrador()
        } else if (tipoUsuario == "Estudiante") {
            configurarFormularioEstudiante()
        } else {
            Toast.makeText(requireContext(), "Tipo de usuario desconocido", Toast.LENGTH_SHORT).show()
        }

        buttonActualizar.setOnClickListener {

            if (tipoUsuario == "Administrador") {
                actualizarInformacion(administrador!!)
            }
            else if (tipoUsuario == "Estudiante") {
                actualizarInformacion(estudiante!!)
            }
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun inicializarCampos(rootView: View) {
        // Inicializar campos comunes de Usuario
        editEmail = rootView.findViewById(R.id.editEmail)
        editContrasena = rootView.findViewById(R.id.editContrasena)
        editNombre = rootView.findViewById(R.id.editNombre)
        editFechaCreacion = rootView.findViewById(R.id.editFechaCreacion)
        editFechaExpiracion = rootView.findViewById(R.id.editFechaExpiracion)
        editTipo = rootView.findViewById(R.id.editTipo)

        // Inicializar campos para Administrador
        textCodigoTrabajador = rootView.findViewById(R.id.textCodigoTrabajador)
        editCodigoTrabajador = rootView.findViewById(R.id.editCodigoTrabajador)
        textRFC = rootView.findViewById(R.id.textRFC)
        editRFC = rootView.findViewById(R.id.editRFC)
        textNombreSupervisor = rootView.findViewById(R.id.textNombreSupervisor)
        editNombreSupervisor = rootView.findViewById(R.id.editNombreSupervisor)
        textTelefono = rootView.findViewById(R.id.textTelefono)
        editTelefono = rootView.findViewById(R.id.editTelefono)
        textTurno = rootView.findViewById(R.id.textTurno)
        editTurno = rootView.findViewById(R.id.editTurno)

        // Inicializar campos para Estudiante
        textRegistro = rootView.findViewById(R.id.textRegistro)
        editRegistro = rootView.findViewById(R.id.editRegistro)
        textGrado = rootView.findViewById(R.id.textGrado)
        editGrado = rootView.findViewById(R.id.editGrado)
        textCarrera = rootView.findViewById(R.id.textCarrera)
        editCarrera = rootView.findViewById(R.id.editCarrera)
        textNombreTutor = rootView.findViewById(R.id.textNombreTutor)
        editNombreTutor = rootView.findViewById(R.id.editNombreTutor)
        textGrupo = rootView.findViewById(R.id.textGrupo)
        editGrupo = rootView.findViewById(R.id.editGrupo)

        buttonActualizar = rootView.findViewById(R.id.buttonActualizar)
    }

    private fun configurarFormularioAdministrador() {
        if (administrador != null) {
            // Llenar campos comunes
            llenarCamposUsuario(administrador!!)

            // Llenar campos específicos de Administrador
            editCodigoTrabajador.setText(administrador!!.codigoTrabajador)
            editRFC.setText(administrador!!.rfc)
            editNombreSupervisor.setText(administrador!!.nombreSupervisor)
            editTelefono.setText(administrador!!.telefono)
            editTurno.setText(administrador!!.turno)

            // Hacer visibles los campos específicos
            mostrarCamposAdministrador()
        } else {
            Toast.makeText(requireContext(), "No se encontró el administrador", Toast.LENGTH_SHORT).show()
        }
    }

    private fun configurarFormularioEstudiante() {
        if (estudiante != null) {
            // Llenar campos comunes
            llenarCamposUsuario(estudiante!!)

            // Llenar campos específicos de Estudiante
            editRegistro.setText(estudiante!!.registro)
            editGrado.setText(estudiante!!.grado.toString())
            editCarrera.setText(estudiante!!.carrera)
            editNombreTutor.setText(estudiante!!.nombreTutor)
            editGrupo.setText(estudiante!!.grupo)

            // Hacer visibles los campos específicos
            mostrarCamposEstudiante()
        } else {
            Toast.makeText(requireContext(), "No se encontró el estudiante", Toast.LENGTH_SHORT).show()
        }
    }

    private fun llenarCamposUsuario(administrador: Administrador) {
        editEmail.setText(administrador.email)
        editContrasena.setText(administrador.contrasena)
        editNombre.setText(administrador.nombre)
        editFechaCreacion.setText(administrador.fechaCreacion)
        editFechaExpiracion.setText(administrador.fechaExpiracion)
        editTipo.setText(administrador.tipo)

    }

    private fun llenarCamposUsuario(estudiante: Estudiante) {
        editEmail.setText(estudiante.email)
        editContrasena.setText(estudiante.contrasena)
        editNombre.setText(estudiante.nombre)
        editFechaCreacion.setText(estudiante.fechaCreacion)
        editFechaExpiracion.setText(estudiante.fechaExpiracion)
        editTipo.setText(estudiante.tipo)

    }

    private fun mostrarCamposAdministrador() {
        textCodigoTrabajador.visibility = View.VISIBLE
        editCodigoTrabajador.visibility = View.VISIBLE
        textRFC.visibility = View.VISIBLE
        editRFC.visibility = View.VISIBLE
        textNombreSupervisor.visibility = View.VISIBLE
        editNombreSupervisor.visibility = View.VISIBLE
        textTelefono.visibility = View.VISIBLE
        editTelefono.visibility = View.VISIBLE
        textTurno.visibility = View.VISIBLE
        editTurno.visibility = View.VISIBLE
    }

    private fun mostrarCamposEstudiante() {
        textRegistro.visibility = View.VISIBLE
        editRegistro.visibility = View.VISIBLE
        textGrado.visibility = View.VISIBLE
        editGrado.visibility = View.VISIBLE
        textCarrera.visibility = View.VISIBLE
        editCarrera.visibility = View.VISIBLE
        textNombreTutor.visibility = View.VISIBLE
        editNombreTutor.visibility = View.VISIBLE
        textGrupo.visibility = View.VISIBLE
        editGrupo.visibility = View.VISIBLE
    }


    private fun actualizarInformacion(administrador: Administrador){
        val nuevoAdministrador = Administrador(
            email = editEmail.text.toString(),
            contrasena = administrador.contrasena, // No se actualiza en el formulario
            nombre = editNombre.text.toString(),
            fechaCreacion = editFechaCreacion.text.toString(),
            fechaExpiracion = editFechaExpiracion.text.toString(),
            tipo = editTipo.text.toString(),
            codigoTrabajador = editCodigoTrabajador.text.toString(),
            rfc = editRFC.text.toString(),
            nombreSupervisor = editNombreSupervisor.text.toString(),
            telefono = editTelefono.text.toString(),
            turno = editTurno.text.toString()
        )
        val exito = AdministradorRepository().actualizar(email, nuevoAdministrador)
        if (exito) {
            Toast.makeText(
                requireContext(),
                "Administrador actualizado con éxito",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                requireContext(),
                "Error al actualizar el administrador",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun actualizarInformacion(estudiante: Estudiante) {
        val nuevoEstudiante = Estudiante(
            email = editEmail.text.toString(),
            contrasena = editContrasena.text.toString(),
            nombre = editNombre.text.toString(),
            fechaCreacion = editFechaCreacion.text.toString(),
            fechaExpiracion = editFechaExpiracion.text.toString(),
            tipo = estudiante.tipo,
            registro = editRegistro.text.toString(),
            grado = editGrado.text.toString().toIntOrNull() ?: 0,
            carrera = editCarrera.text.toString(),
            nombreTutor = editNombreTutor.text.toString(),
            grupo = editGrupo.text.toString()
        )
        val exito = EstudianteRepository().actualizar(email, nuevoEstudiante)
        if (exito) {
            Toast.makeText(
                requireContext(),
                "Estudiante actualizado con éxito",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                requireContext(),
                "Error al actualizar el estudiante",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}