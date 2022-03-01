package com.maricoolsapps.e_commerce.ui.user_authentication_ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.databinding.FragmentRegisterBinding
import com.maricoolsapps.e_commerce.data.model.CarBuyerOrSeller
import com.maricoolsapps.e_commerce.data.model.User
import com.maricoolsapps.e_commerce.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.fragment_register) {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val model: RegisterViewModel by viewModels()
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private var intent_data: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    intent_data = result.data?.data
                    if (intent_data != null) {
                        binding.userImage.setResourceCenterCrop(intent_data.toString())
                    }
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRegisterBinding.bind(view)
        showLoginDetails()
        observeLiveData()
        buttonClicks()
    }

    private fun buttonClicks() {
        binding.next.setOnClickListener {
            userRegistration()
        }
        binding.finish.setOnClickListener {
            saveToDb()
        }
        binding.logIn.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }

        binding.camera.setOnClickListener {
            resultLauncher.launch(model.gotoMedia())
        }
    }

    private fun observeLiveData() {
        model.done.observe(viewLifecycleOwner) {
            when (it) {
                true -> {
                    findNavController().navigate(R.id.mainFragment)
                }
            }
        }
        model.result.observe(viewLifecycleOwner) {
            when (it) {
                true -> {
                    hideLoginDetails()
                }
            }
        }
        model.defaultRepo.dataLoading.observe(viewLifecycleOwner) {
            binding.progressBar.toggleVisibility(it)
        }
        model.defaultRepo.resultError.observe(viewLifecycleOwner) {
            binding.progressBar.displaySnack(it)
        }
    }

    private fun userRegistration() {
        val userEmail: String = binding.email.text.toString().trim()
        val userPassword: String = binding.password.text.toString().trim()
        val userReenterPassword: String = binding.reenterPassword.text.toString().trim()

        if (!validateEmail(userEmail)) {
            binding.email.requestFocus()
            binding.email.displaySnack("Error, Please check your Email Address")
            return
        }

        if (!validateTwoPasswords(userPassword, userReenterPassword)) {
            binding.reenterPassword.requestFocus()
            binding.reenterPassword.displaySnack("Error, Please check your Email Address")
            return
        }

        val user = User(userEmail, userPassword)
        model.createNewUser(user)
    }

    private fun saveToDb() {
        val location = binding.location.text.toString().trim()
        val name = binding.username.text.toString().trim()
        val email = binding.email.text.toString().trim()
        val number = binding.number.text.toString().trim()
        val region = binding.regions.selectedItem.toString()

        if (name.isEmpty() || email.isEmpty() ||
            number.isEmpty() || binding.regions.selectedItemPosition == 0 || intent_data == null
        ) {
            binding.progressBar.displaySnack("Complete your Entries")
        } else {
            model.getUser().observe(viewLifecycleOwner) {
                if (it != null) {
                    val user = CarBuyerOrSeller(
                        it.uid,
                        intent_data.toString(),
                        name,
                        email,
                        number,
                        region,
                        location
                    )
                    model.completeRegistration(it.uid, user)
                }
            }
        }
    }

    private fun hideLoginDetails() {
        binding.apply {
            userImage.toggleVisibility(true)
            camera.toggleVisibility(true)
            usernameField.toggleVisibility(true)
            finish.toggleVisibility(true)
            location.toggleVisibility(true)
            number.toggleVisibility(true)
            regions.toggleVisibility(true)

            emailField.toggleVisibility(false)
            passwordField.toggleVisibility(false)
            reenterPasswordField.toggleVisibility(false)
            next.toggleVisibility(false)
        }
    }

    private fun showLoginDetails() {
        binding.apply {
            userImage.toggleVisibility(false)
            camera.toggleVisibility(false)
            usernameField.toggleVisibility(false)
            finish.toggleVisibility(false)
            location.toggleVisibility(false)
            number.toggleVisibility(false)
            regions.toggleVisibility(false)

            emailField.toggleVisibility(true)
            passwordField.toggleVisibility(true)
            reenterPasswordField.toggleVisibility(true)
            next.toggleVisibility(true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}