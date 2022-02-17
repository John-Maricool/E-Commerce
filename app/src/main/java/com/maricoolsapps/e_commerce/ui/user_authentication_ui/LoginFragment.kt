package com.maricoolsapps.e_commerce.ui.user_authentication_ui

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.databinding.FragmentLoginBinding
import com.maricoolsapps.e_commerce.data.model.User
import com.maricoolsapps.e_commerce.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val model: LoginViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginBinding.bind(view)

        binding.signIn.setOnClickListener {
            userLogin()
        }
        binding.registerHere.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            findNavController().navigate(action)
        }
        binding.forgotPassword.setOnClickListener {
            forgotPassword()
        }
        observeLiveData()
    }

    private fun observeLiveData() {
        model.defaultRepo.dataLoading.observe(viewLifecycleOwner) {
            binding.progressBar.toggleVisibility(it)
        }
        model.defaultRepo.resultData.observe(viewLifecycleOwner){
            binding.progressBar.displaySnack(it)
        }
    }

    private fun userLogin() {
        val userEmail: String = binding.email.text.toString().trim()
        val userPassword: String = binding.password.text.toString().trim()
        if (!validateEmail(userEmail)) {
            binding.email.requestFocus()
            binding.email.displaySnack("Error, Please check your Email Address")
            return
        }

        if (!validatePassword(userPassword)){
            binding.password.requestFocus()
            binding.password.displaySnack("Error, Please check your password")
            return
        }

        val user = User(userEmail, userPassword)
        model.loginUser(user)
    }

    private fun forgotPassword() {
        val userEmail = binding.email.text.toString().trim()
        if (!validateEmail(userEmail)) {
            binding.email.requestFocus()
            binding.email.displaySnack("Error, Please check your Email Address")
            return
        }
        model.retrievePassword(userEmail)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}