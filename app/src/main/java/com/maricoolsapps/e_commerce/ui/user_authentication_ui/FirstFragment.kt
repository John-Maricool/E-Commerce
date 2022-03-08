package com.maricoolsapps.e_commerce.ui.user_authentication_ui

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.first = this
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        if(auth.currentUser != null){
            findNavController().navigate(R.id.mainFragment)
        }
    }

    fun navigateToLogin(){
        findNavController().navigate(R.id.loginFragment)
    }
     fun navigateToRegister(){
        findNavController().navigate(R.id.registerFragment)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}