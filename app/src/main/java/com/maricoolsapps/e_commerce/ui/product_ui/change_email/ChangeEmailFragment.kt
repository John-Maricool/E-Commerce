package com.maricoolsapps.e_commerce.ui.product_ui.change_email

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.data.model.User
import com.maricoolsapps.e_commerce.databinding.FragmentChangeEmailAndPasswordBinding
import com.maricoolsapps.e_commerce.ui.user_authentication_ui.MainActivity
import com.maricoolsapps.e_commerce.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangeEmailFragment : Fragment(R.layout.fragment_change_email_and_password) {

    private var _binding: FragmentChangeEmailAndPasswordBinding? = null
    private val binding get() = _binding!!
    private val model: ChangeEmailViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentChangeEmailAndPasswordBinding.bind(view)
        getViews()
        toolbarInit()
        observeLiveData()
    }

    private fun observeLiveData() {
        model.defaultRepo.dataLoading.observe(viewLifecycleOwner) {
            (activity as MainActivity).progressBar.toggleVisibility(it)
        }
        model.defaultRepo.resultError.observe(viewLifecycleOwner) {
            (activity as MainActivity).progressBar.displaySnack(it)
        }
        model.result.observe(viewLifecycleOwner) {
            if (it != null) {
                activity?.onBackPressed()
                activity?.showToast("Sucessfully changed email")
            }
        }
    }

    private fun toolbarInit() {
        (activity as MainActivity).apply {
            toolbar.title = "Change Email"
            toolbar.setBackgroundColor(resources.getColor(R.color.white, null))
        }
    }

    private fun getViews() {
        binding.reset.setOnClickListener {
            val email = binding.oldEmail.text.toString().trim()
            val password = binding.password.text.toString().trim()
            val new_email = binding.newEmail.text.toString().trim()

            if (!validateEmail(email) || !validateEmail(new_email) || !validatePassword(password)) {
                binding.newEmail.displaySnack("Error in your entries")
                return@setOnClickListener
            } else {
                val user = User(email, password)
                model.changeEmail(new_email, user)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}