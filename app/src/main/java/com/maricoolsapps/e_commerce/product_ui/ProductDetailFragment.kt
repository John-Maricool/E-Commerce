package com.maricoolsapps.e_commerce.product_ui


import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.adapters.SliderImageAdapter
import com.maricoolsapps.e_commerce.databinding.FragmentProductDetailBinding
import com.maricoolsapps.e_commerce.room_db.FavoriteProductEntity
import com.maricoolsapps.e_commerce.utils.Status
import com.smarteist.autoimageslider.SliderView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
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
        toolbarInit()
        placeAllViews()
        viewlisteners()
    }

    private fun viewlisteners() {
        binding.ownerId.setOnClickListener {
            val action =
                ProductDetailFragmentDirections.actionProductDetailFragmentToSellerFragment(args.product.ownerId)
            findNavController().navigate(action)
        }

        binding.report.setOnClickListener {
            val action =
                ProductDetailFragmentDirections.actionProductDetailFragmentToReportFragment(args.product)
            findNavController().navigate(action)
        }
    }

    private fun placeAllViews() {
        binding.sliderView.autoCycleDirection = SliderView.LAYOUT_DIRECTION_LTR

        args.product.apply {
            adapter.getProductsImage(args.product.photos)
            binding.productName.text = "$brand $type"
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

    private fun toolbarInit() {
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        binding.toolbar.title = args.product.brand
        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}