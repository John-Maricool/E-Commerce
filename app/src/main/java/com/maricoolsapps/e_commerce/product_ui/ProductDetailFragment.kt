package com.maricoolsapps.e_commerce.product_ui

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavArgs
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.adapters.SliderImageAdapter
import com.maricoolsapps.e_commerce.databinding.FragmentProductDetailBinding
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import dagger.hilt.android.AndroidEntryPoint
import hilt_aggregated_deps._com_maricoolsapps_e_commerce_product_ui_MainViewModel_HiltModules_BindsModule
import javax.inject.Inject

@AndroidEntryPoint
class ProductDetailFragment : Fragment(R.layout.fragment_product_detail) {

    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!
    private val args: ProductDetailFragmentArgs by navArgs()
    @Inject
    lateinit var adapter: SliderImageAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProductDetailBinding.bind(view)
        val nav = activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)
        nav?.visibility = View.GONE
      placeAllViews()
        viewlisteners()
    }

    private fun viewlisteners() {
        binding.ownerId.setOnClickListener {
            val action = ProductDetailFragmentDirections.actionProductDetailFragmentToSellerFragment(args.product.ownerId)
            findNavController().navigate(action)
        }
    }

    private fun placeAllViews(){
        binding.sliderView.autoCycleDirection = SliderView.LAYOUT_DIRECTION_LTR

        args.product.apply {
            adapter.getProductsImage(args.product.photos)
            binding.productName.text = type
            binding.brand.text = brand
            binding.type.text = type
            binding.ownerId.text = ownerId
            binding.color.text = color
            binding.condition.text = condition
            binding.yr.text = yearOfManufacturing
            binding.prodDesc.text = description
            binding.state.text = state
            binding.ratingText.text = rating
            binding.productPrice.text = "$${price}"
            binding.location.text = location
        }
        binding.sliderView.setSliderAdapter(adapter)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}