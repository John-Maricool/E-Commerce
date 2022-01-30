package com.maricoolsapps.e_commerce.user_authentication_ui

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.utils.Status
import com.maricoolsapps.e_commerce.databinding.FragmentLoginBinding
import com.maricoolsapps.e_commerce.model.User
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val model: LoginViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
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
    }

    private fun userLogin(){
        val userEmail: String = binding.email.text.toString().trim()
        val userPassword: String = binding.password.text.toString().trim()

        if (userEmail.isEmpty()) {
            binding.email.error = "Email is required"
            binding.email.requestFocus()
            return
        }

        if (userPassword.isEmpty()) {
            binding.password.error = "Password is required"
            binding.password.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            binding.email.error = "Please enter a correct email"
            return
        }

        if (userPassword.length < 6) {
            binding.password.error = "Please enter a correct password"
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        val user = User(userEmail, userPassword)
        model.loginUser(user).observe(viewLifecycleOwner, {
            when(it.status) {
                Status.SUCCESS -> {
                    binding.progressBar.visibility = View.GONE
                    //navigate
                    Toast.makeText(activity, it.data, Toast.LENGTH_LONG)
                        .show()
                    findNavController().navigate(R.id.mainFragment)
                }

                Status.ERROR -> {
                    binding.progressBar.visibility = View.GONE
                    Snackbar.make(binding.progressBar, it.data.toString(), Snackbar.LENGTH_LONG)
                        .setBackgroundTint(resources.getColor(R.color.pink2, null))
                        .show()
                }
                Status.LOADING -> TODO()
            }
        })
    }

    private fun forgotPassword(){
       val userEmail = binding.email.text.toString().trim()
        if (userEmail.isEmpty()) {
            binding.email.error = "Email is required"
            binding.email.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            binding.email.error = "Please enter a correct email"
            return
        }
        binding.progressBar.visibility = View.VISIBLE

        model.retrievePassword(userEmail).observe(viewLifecycleOwner, {
            when(it.status){
                Status.SUCCESS -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(activity, it.data, Toast.LENGTH_LONG)
                        .show()
                }
                Status.ERROR -> {
                    binding.progressBar.visibility = View.GONE
                    Snackbar.make(binding.progressBar, it.message.toString(), Snackbar.LENGTH_LONG)
                        .setBackgroundTint(resources.getColor(R.color.pink2, null))
                        .show()
                }
                Status.LOADING -> TODO()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}