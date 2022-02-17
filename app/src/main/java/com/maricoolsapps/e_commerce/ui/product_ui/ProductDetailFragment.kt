package com.maricoolsapps.e_commerce.ui.product_ui


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.data.adapters.SliderImageAdapter
import com.maricoolsapps.e_commerce.databinding.FragmentProductDetailBinding
import com.maricoolsapps.e_commerce.data.interfaces.OnItemClickListener
import com.maricoolsapps.e_commerce.data.model.Product
import com.maricoolsapps.e_commerce.utils.Status
import com.smarteist.autoimageslider.SliderView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProductDetailFragment : Fragment(R.layout.fragment_product_detail),
    OnItemClickListener<List<String>> {

    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!
    private val args: ProductDetailFragmentArgs by navArgs()
    private val model: ProductDetailViewModel by viewModels()
    lateinit var theProduct: Product

    @Inject
    lateinit var adapter: SliderImageAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProductDetailBinding.bind(view)
        val nav = activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)
        nav?.visibility = View.GONE
        model.getCar(args.brand, args.id)
        toolbarInit()
        placeAllViews()
        viewlisteners()
    }

    override fun onStart() {
        super.onStart()
        binding.retry.setOnClickListener {
            placeAllViews()
        }
    }

    private fun viewlisteners() {
        binding.ownerId.setOnClickListener {
            val action =
                ProductDetailFragmentDirections.actionProductDetailFragmentToSellerFragment(theProduct.ownerId)
            findNavController().navigate(action)
        }

        binding.report.setOnClickListener {
            val action =
                ProductDetailFragmentDirections.actionProductDetailFragmentToReportFragment(theProduct)
            findNavController().navigate(action)
        }

        adapter.setOnItemClickListener(this)
    }

    private fun placeAllViews() {
        binding.sliderView.autoCycleDirection = SliderView.LAYOUT_DIRECTION_LTR

        binding.progressBar.visibility = View.VISIBLE
        model.result.observe(viewLifecycleOwner, {
            when(it.status){
                Status.SUCCESS -> {
                    val product = it.data
                    if (product != null) {
                        showViews(product)
                        theProduct = product
                        binding.progressBar.visibility = View.GONE
                    }else{
                        binding.progressBar.visibility = View.GONE
                        binding.error.visibility = View.VISIBLE
                        binding.retry.visibility = View.VISIBLE
                    }
                }

                Status.ERROR -> {
                    binding.progressBar.visibility = View.GONE
                    binding.error.visibility = View.VISIBLE
                    binding.retry.visibility = View.VISIBLE
                    binding.error.text = it.message
                }
                Status.LOADING -> TODO()
            }
        })
        binding.sliderView.setSliderAdapter(adapter)
    }

    private fun toolbarInit() {
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        binding.toolbar.title = args.brand
        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(t: Any, p: Any?) {
        val action = ProductDetailFragmentDirections.actionProductDetailFragmentToPictureFragment(
            theProduct.photos.toTypedArray()
        )
        findNavController().navigate(action)
    }

    private fun showViews(product: Product){
        binding.apply {
            productName.text = "${product.brand} ${product.type}"
            brand.text = product.brand
            type.text = product.type
            color.text = product.color
            condition.text = product.condition
            yr.text = product.yearOfManufacturing
            prodDesc.text = product.description
            state.text = product.state
            ratingText.text = product.rating
            productPrice.text = "$${product.price}"
            location.text = product.location
        }
        adapter.getProductsImage(product.photos)

    }
}