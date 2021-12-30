package com.maricoolsapps.e_commerce.user_authentication_ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.maricoolsapps.e_commerce.product_ui.EcommerceActivity
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.utils.Status
import com.maricoolsapps.e_commerce.databinding.FragmentRegisterBinding
import com.maricoolsapps.e_commerce.model.User
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
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                 intent_data = result.data?.data
                if (intent_data != null) {
                    Glide.with(requireActivity())
                        .load(intent_data)
                        .circleCrop()
                        .into(binding.userImage)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRegisterBinding.bind(view)
        showLoginDetails()

        binding.next.setOnClickListener {
            userRegistration()
        }
        binding.finish.setOnClickListener {
            completeRegistration()
        }
        binding.logIn.setOnClickListener {
            val action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
            findNavController().navigate(action)
        }
        binding.camera.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            intent.type = "image/*"
            resultLauncher.launch(intent)
        }
    }

    private fun userRegistration(){
        val userEmail: String = binding.email.text.toString().trim()
        val userPassword: String = binding.password.text.toString().trim()
        val userReenterPassword: String = binding.reenterPassword.text.toString().trim()

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

        if (userReenterPassword.isEmpty()) {
            binding.reenterPassword.error = "Password is required"
            binding.reenterPassword.requestFocus()
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

        if (userReenterPassword.length < 6 || userPassword != userReenterPassword) {
            binding.reenterPassword.error = "Please enter a correct password"
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        val user = User(userEmail, userPassword)
        model.createNewUser(user).observe(viewLifecycleOwner, {
            when(it.status){
                Status.SUCCESS -> {
                    binding.progressBar.visibility = View.GONE
                    hideLoginDetails()
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

    private fun showLoginDetails(){
        binding.apply {
            userImage.visibility = View.GONE
            camera.visibility = View.GONE
            usernameField.visibility = View.GONE
            finish.visibility = View.GONE

            emailField.visibility = View.VISIBLE
            passwordField.visibility = View.VISIBLE
            reenterPasswordField.visibility = View.VISIBLE
            next.visibility = View.VISIBLE
        }
    }

    private fun hideLoginDetails(){
        binding.apply {
            userImage.visibility = View.VISIBLE
            camera.visibility = View.VISIBLE
            usernameField.visibility = View.VISIBLE
            finish.visibility = View.VISIBLE

            emailField.visibility = View.GONE
            passwordField.visibility = View.GONE
            reenterPasswordField.visibility = View.GONE
            next.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun completeRegistration(){

        val username = binding.username.text.toString().trim()

        if (username.isEmpty()){
            binding.username.error = "Enter a valid username"
            return
        }
        if (intent_data == null){
            Toast.makeText(activity, "Please select an image", Toast.LENGTH_LONG).show()
            return
        }

        binding.progressBar.visibility = View.VISIBLE

        model.changeProfileNameAndPhoto(intent_data!!, username)
            .observe(viewLifecycleOwner, {
                when(it.status){
                    Status.SUCCESS -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(activity, "Successfully Registered", Toast.LENGTH_LONG).show()
                        startActivity(Intent(activity, EcommerceActivity::class.java))
                        activity?.finish()
                    }
                    Status.ERROR -> {
                        binding.progressBar.visibility = View.GONE
                        Snackbar.make(binding.progressBar, it.data.toString(), Snackbar.LENGTH_LONG)
                            .setBackgroundTint(resources.getColor(R.color.pink, null))
                            .show()

                    }
                    Status.LOADING -> TODO()
                }
            })
    }

}