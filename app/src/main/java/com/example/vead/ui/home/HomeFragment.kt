package com.example.vead.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.vead.R
import com.example.vead.data.entities.User
import com.example.vead.data.repositories.UserRepository
import com.example.vead.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    val userRepo = UserRepository()

    private lateinit var email: String
    private lateinit var password: String
    private lateinit var userType: String
    private lateinit var name: String
    private lateinit var lastName: String
    private lateinit var code: String
    private lateinit var phoneNumber: String

    // Campos comunes de la clase Usuario
    private lateinit var editEmail: EditText
    private lateinit var editPassword: EditText
    private lateinit var editName: EditText
    private lateinit var editLastName: EditText
    private lateinit var editPhoneNumber: EditText
    private lateinit var editUserType: EditText
    private lateinit var editCode: EditText



    private lateinit var buttonUpdate : Button

    private var user : User? = null


    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)


        // Recuperar los extras del Intent
        email = requireActivity().intent.getStringExtra("Email") ?: ""
        userType = requireActivity().intent.getStringExtra("UserType") ?: ""
        password = requireActivity().intent.getStringExtra("Password") ?: ""
        name = requireActivity().intent.getStringExtra("Name") ?: ""
        lastName = requireActivity().intent.getStringExtra("LastName") ?: ""
        code = (requireActivity().intent.getLongExtra("Code", 0) ?: "").toString()
        phoneNumber = (requireActivity().intent.getLongExtra("PhoneNumber", 0) ?: "").toString()




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

        initializeFields(root)

        fillUserFields()



        buttonUpdate.setOnClickListener {

            updateUser()

        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initializeFields(rootView: View) {
        // Inicializar campos comunes de Usuario
        editEmail = rootView.findViewById(R.id.editEmail)
        editPassword = rootView.findViewById(R.id.editPassword)
        editName = rootView.findViewById(R.id.editName)
        editLastName = rootView.findViewById(R.id.editLastName)
        editCode = rootView.findViewById(R.id.editCode)
        editPhoneNumber = rootView.findViewById(R.id.editPhoneNumber)
        editUserType = rootView.findViewById(R.id.editUserType)

        buttonUpdate = rootView.findViewById(R.id.buttonUpdate)
    }


    private fun fillUserFields() {

            editEmail.setText(email)
            editPassword.setText(password)
            editName.setText(name)
            editLastName.setText(lastName)
            editCode.setText(code)
            editPhoneNumber.setText(phoneNumber)
            editUserType.setText(userType)
    }


    private fun updateUser(){
        val updatedUser = User(
            editCode.text.toString().toLong(),
            editEmail.text.toString(),
            editLastName.text.toString(),
            editName.text.toString(),
            editPassword.text.toString(),
            editPhoneNumber.text.toString().toLong(),
            userType
        )
        userRepo.updateUserByEmail(email, updatedUser) {
            if (it) {
                val savedPassword = sharedPreferences.getString("password", "")

                if (updatedUser.password != savedPassword){
                    clearLoginDetails()
                }

                Toast.makeText(
                    requireContext(),
                    "Usuario actualizado con éxito",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Error al actualizar el usuario",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun clearLoginDetails() {
        val editor = sharedPreferences.edit()
        editor.remove("email")
        editor.remove("password")
        editor.remove("rememberMe")
        editor.apply()
    }

}