package com.maricoolsapps.e_commerce.product_ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavArgs
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.databinding.FragmentProductDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import hilt_aggregated_deps._com_maricoolsapps_e_commerce_product_ui_MainViewModel_HiltModules_BindsModule

@AndroidEntryPoint
class ProductDetailFragment : Fragment(R.layout.fragment_product_detail) {

    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!
    private val args: ProductDetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProductDetailBinding.bind(view)
        val nav = activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)
        nav?.visibility = View.GONE
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}