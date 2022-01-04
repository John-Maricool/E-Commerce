package com.maricoolsapps.e_commerce.product_ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.databinding.FragmentSellBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SellerFragment : Fragment(R.layout.fragment_seller) {

    private var _binding: FragmentSellBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSellBinding.bind(view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}