package com.maricoolsapps.e_commerce.ui.product_ui.product_detail

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.data.adapters.FeedbackListAdapter
import com.maricoolsapps.e_commerce.data.adapters.ProductListAdapter
import com.maricoolsapps.e_commerce.data.adapters.SliderImageAdapter
import com.maricoolsapps.e_commerce.databinding.FragmentProductDetailBinding
import com.maricoolsapps.e_commerce.data.interfaces.OnItemClickListener
import com.maricoolsapps.e_commerce.data.model.ChatChannel
import com.maricoolsapps.e_commerce.data.model.Product
import com.maricoolsapps.e_commerce.data.model.ProductModel
import com.maricoolsapps.e_commerce.utils.toggleVisibility
import com.smarteist.autoimageslider.SliderView
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
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

    @Inject
    lateinit var feedbackAdapter: FeedbackListAdapter

    @Inject
    lateinit var productAdapter: ProductListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProductDetailBinding.bind(view)

        val nav = activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)
        nav?.visibility = View.GONE
        model.getCarAndFeedback(args.brand, args.id)
        binding.recyelerFeedback.setHasFixedSize(true)
        binding.recyclerSimilarAds.setHasFixedSize(true)
        binding.recyclerSimilarAds.adapter = productAdapter
        binding.recyelerFeedback.adapter = feedbackAdapter
        toolbarInit()
        observeAllLiveData()
        viewlisteners()
    }

    override fun onStart() {
        super.onStart()
        binding.sliderView.autoCycleDirection = SliderView.LAYOUT_DIRECTION_LTR
        binding.retry.setOnClickListener {
            model.getCarAndFeedback(args.brand, args.id)
        }
        productAdapter.setOnItemClickListener(object : OnItemClickListener<ProductModel> {
            override fun onItemClick(t: Any, p: Any?) {
                val action =
                    ProductDetailFragmentDirections.goToProductDetails(t as String, p as String)
                findNavController().navigate(action)
            }
        })
    }

    private fun observeAllLiveData() {
        model.result.observe(viewLifecycleOwner) {
            if (it != null) {
                val product = it
                showViews(product)
                theProduct = product
                binding.sliderView.setSliderAdapter(adapter)
                binding.parent.toggleVisibility(true)
                binding.error.toggleVisibility(false)
                binding.retry.toggleVisibility(false)
            }
        }
        model.channelCreated.observe(viewLifecycleOwner){
            if (it != null){
              val action = ProductDetailFragmentDirections.goToChat(it)
                findNavController().navigate(action)
            }
        }
        model.defaultRepo.dataLoading.observe(viewLifecycleOwner) {
            binding.progressBar.toggleVisibility(it)
        }
        model.defaultRepo.resultError.observe(viewLifecycleOwner) {
            binding.error.text = it
            binding.parent.toggleVisibility(false)
            binding.error.toggleVisibility(true)
            binding.retry.toggleVisibility(true)
        }
        model.done.observe(viewLifecycleOwner) {
            if (it != null) {
                if (it.isEmpty()) {
                    binding.recyelerFeedback.toggleVisibility(false)
                    binding.feedbackText.toggleVisibility(false)
                    binding.viewAll.toggleVisibility(false)
                } else {
                    binding.recyelerFeedback.toggleVisibility(true)
                    binding.feedbackText.toggleVisibility(true)
                    binding.viewAll.toggleVisibility(true)
                    binding.parent.toggleVisibility(true)
                    binding.error.toggleVisibility(false)
                    binding.retry.toggleVisibility(false)
                    feedbackAdapter.getFeedbacks(it, 3)
                }
            }
        }
        model.cars.observe(viewLifecycleOwner) {
            if (it != null) {
                val products = it.filter { product ->
                    product.id != theProduct.id
                }
                if (products.isEmpty()) {
                    binding.recyclerSimilarAds.toggleVisibility(false)
                    binding.similarAds.toggleVisibility(false)
                    binding.viewSecond.toggleVisibility(false)
                } else {
                    productAdapter.getProducts(products)
                    binding.recyclerSimilarAds.toggleVisibility(true)
                    binding.similarAds.toggleVisibility(true)
                    binding.viewSecond.toggleVisibility(true)
                }
            }
        }
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

    private fun viewlisteners() {
        binding.ownerId.setOnClickListener {
            val action =
                ProductDetailFragmentDirections.actionProductDetailFragmentToSellerFragment(
                    theProduct.ownerId
                )
            findNavController().navigate(action)
        }
        binding.postAdLikeThis.setOnClickListener {
            findNavController().navigate(R.id.sellFragment)
        }
        binding.leaveFeedback.setOnClickListener {
            val action =
                ProductDetailFragmentDirections.actionProductDetailFragmentToFeedbackFragment(
                    theProduct.id,
                    theProduct.brand
                )
            findNavController().navigate(action)
        }
        binding.viewAll.setOnClickListener {
            val action =
                ProductDetailFragmentDirections.actionProductDetailFragmentToFeedbackListFragment(
                    theProduct.brand,
                    theProduct.id
                )
            findNavController().navigate(action)
        }
        binding.messageSeller.setOnClickListener {
            model.createChatChannel(userToChat = theProduct.ownerId)
        }
        binding.report.setOnClickListener {
            val action =
                ProductDetailFragmentDirections.actionProductDetailFragmentToReportFragment(
                    theProduct
                )
            findNavController().navigate(action)
        }
        adapter.setOnItemClickListener(this)
    }

    private fun showViews(product: Product) {
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