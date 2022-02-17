package com.maricoolsapps.e_commerce.ui.user_authentication_ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.databinding.FragmentFirstBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FirstFragment : Fragment(R.layout.fragment_first) {
    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFirstBinding.bind(view)

        binding.signIn.setOnClickListener {
            val action = FirstFragmentDirections.actionFirstFragmentToLoginFragment()
            findNavController().navigate(action)
        }
        binding.register.setOnClickListener {
            val action = FirstFragmentDirections.actionFirstFragmentToRegisterFragment()
            findNavController().navigate(action)
        }
    }

    override fun onStart() {
        super.onStart()
        if(auth.currentUser != null){
            findNavController().navigate(R.id.mainFragment)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}