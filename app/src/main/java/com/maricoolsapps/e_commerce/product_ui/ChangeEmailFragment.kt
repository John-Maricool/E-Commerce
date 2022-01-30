package com.maricoolsapps.e_commerce.product_ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.databinding.FragmentChangeEmailAndPasswordBinding
import com.maricoolsapps.e_commerce.utils.Resource
import com.maricoolsapps.e_commerce.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChangeEmailFragment : Fragment(R.layout.fragment_change_email_and_password) {

    private var _binding: FragmentChangeEmailAndPasswordBinding? = null
    private val binding get() = _binding!!
    private val model: ChangeEmailViewModel by viewModels()

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentChangeEmailAndPasswordBinding.bind(view)
        getViews()
        toolbarInit()
    }

    private fun toolbarInit() {
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        val actionBar = (activity as AppCompatActivity).supportActionBar
        binding.toolbar.title = "Change Email"
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    @ExperimentalCoroutinesApi
    private fun getViews() {
        binding.reset.setOnClickListener {
            val email = binding.oldEmail.text.toString().trim()
            val password = binding.password.text.toString().trim()
            val new_email = binding.newEmail.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || new_email.isEmpty() || password.length < 6) {
                model.showSnackBar(binding.progressBar, "Error in your entries", requireActivity())
            } else {
                binding.progressBar.visibility = View.VISIBLE
                model.getUser().observe(viewLifecycleOwner, {
                    when (it.status) {
                        Status.SUCCESS -> {
                            lifecycleScope.launch(Main) {
                                val job = lifecycleScope.async(IO) {
                                    model.changeEmail(it.data!!, email, password, new_email)
                                }
                                job.await()

                                if (job.getCompleted() == Resource.success("Success")) {
                                    activity?.onBackPressed()
                                    binding.progressBar.visibility = View.INVISIBLE
                                } else {
                                    binding.progressBar.visibility = View.INVISIBLE
                                    model.showSnackBar(
                                        binding.progressBar,
                                        "Error", requireActivity()
                                    )
                                }
                            }
                        }

                        Status.ERROR -> {
                            binding.progressBar.visibility = View.GONE
                            model.showSnackBar(
                                binding.progressBar,
                                it.message.toString(), requireActivity()
                            )
                        }
                        Status.LOADING -> TODO()
                    }
                })
            }
        }
    }

        override fun onDestroy() {
            super.onDestroy()
            _binding = null
        }

    }