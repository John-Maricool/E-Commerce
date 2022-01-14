package com.maricoolsapps.e_commerce.user_authentication_ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.databinding.FragmentCompleteRegistrationBinding
import com.maricoolsapps.e_commerce.product_ui.EcommerceActivity
import com.maricoolsapps.e_commerce.utils.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CompleteRegistrationFragment : Fragment(R.layout.fragment_complete_registration) {

    private var _binding: FragmentCompleteRegistrationBinding? = null
    private val binding get() = _binding!!
    private val model: CompleteRegisterViewModel by viewModels()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentCompleteRegistrationBinding.bind(view)

        binding.finish.setOnClickListener {
            completeRegistration()
        }

        binding.camera.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            intent.type = "image/*"
            resultLauncher.launch(intent)
        }
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

        model.changeProfileNameAndPhoto(intent_data, username)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}