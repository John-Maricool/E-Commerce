package com.maricoolsapps.e_commerce.ui.product_ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.databinding.FragmentProfileBinding
import com.maricoolsapps.e_commerce.ui.user_authentication_ui.MainActivity
import com.maricoolsapps.e_commerce.utils.toggleVisibility
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileBinding.bind(view)
        toolbarInit()
        buttonClicks()
    }

    private fun buttonClicks() {
        binding.apply {
            logOut.setOnClickListener {
                auth.signOut()
                (activity as MainActivity).bottomBar.toggleVisibility(false)
                findNavController().navigate(R.id.firstFragment)
                findNavController().backStack.clear()
            }

            contactDetails.setOnClickListener {
                val action =
                    ProfileFragmentDirections.actionProfileFragmentToContactDetailsFragment()
                findNavController().navigate(action)
            }
            followers.setOnClickListener {
                val action =
                    ProfileFragmentDirections.actionProfileFragmentToFollowersFragment(auth.currentUser?.uid!!)
                findNavController().navigate(action)
            }
            myAdverts.setOnClickListener {
                val action = ProfileFragmentDirections.actionProfileFragmentToAdvertsFragment()
                findNavController().navigate(action)
            }
        }
    }

    private fun toolbarInit() {
        (activity as MainActivity).apply {
            toolbar.title = "Profile"
            toolbar.setBackgroundColor(resources.getColor(R.color.white, null))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}