package com.maricoolsapps.e_commerce.ui.product_ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileBinding.bind(view)
        toolbarInit()
        buttonClicks()
    }

    private fun buttonClicks() {
        binding.apply {
            feedback.setOnClickListener {
                val action = ProfileFragmentDirections.actionProfileFragmentToFeedbackFragment()
                findNavController().navigate(action)
            }
            notification.setOnClickListener {
                val action = ProfileFragmentDirections.actionProfileFragmentToNotificationFragment()
                findNavController().navigate(action)
            }
            contactDetails.setOnClickListener {
                val action = ProfileFragmentDirections.actionProfileFragmentToContactDetailsFragment()
                findNavController().navigate(action)
            }
            /*followers.setOnClickListener {
                val action = ProfileFragmentDirections.actionProfileFragmentToFollowersFragment()
                findNavController().navigate(action)
            }*/
            myAdverts.setOnClickListener {
                val action = ProfileFragmentDirections.actionProfileFragmentToAdvertsFragment()
                findNavController().navigate(action)
            }
        }
    }

    private fun toolbarInit() {
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        val actionBar = (activity as AppCompatActivity).supportActionBar
        binding.toolbar.title = "Profile"
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}