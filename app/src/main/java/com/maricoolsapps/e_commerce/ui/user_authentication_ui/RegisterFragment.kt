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
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    model.intent_data.value = result.data?.data
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRegisterBinding.bind(view)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = model
        binding.fragment = this
        //showLoginDetails()
        observeLiveData()
    }

    fun navigateToLoginFragment() {
        findNavController().navigate(R.id.loginFragment)
    }

    fun goToGallery(){
        resultLauncher.launch(model.gotoMedia())
    }

    private fun observeLiveData() {
        model.done.observe(viewLifecycleOwner) {
            when (it) {
                true -> {
                    findNavController().navigate(R.id.mainFragment)
                }
            }
        }
        /*model.result.observe(viewLifecycleOwner) {
            when (it) {
                true -> {
                    hideLoginDetails()
                }
            }
        }*/

        model.defaultRepo.resultError.observe(viewLifecycleOwner) {
            binding.progressBar.displaySnack(it)
        }
    }
/*

    private fun hideLoginDetails() {
        binding.secondHalf.toggleVisibility(true)
        binding.firstHalf.toggleVisibility(false)
    }

    private fun showLoginDetails() {
        binding.secondHalf.toggleVisibility(false)
        binding.firstHalf.toggleVisibility(true)
    }
*/

    override fun onDestroyView() {
        super.onDestroyView()
        model.getUser().removeObservers(this)
        _binding = null
    }

}