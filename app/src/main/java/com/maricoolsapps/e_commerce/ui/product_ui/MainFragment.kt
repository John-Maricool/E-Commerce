package com.maricoolsapps.e_commerce.ui.product_ui

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.data.adapters.ProductListAdapter
import com.maricoolsapps.e_commerce.databinding.FragmentMainBinding
import com.maricoolsapps.e_commerce.data.interfaces.OnItemClickListener
import com.maricoolsapps.e_commerce.data.model.ProductModel
import com.maricoolsapps.e_commerce.utils.Constants
import com.maricoolsapps.e_commerce.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main), TabLayout.OnTabSelectedListener,
    OnItemClickListener<ProductModel> {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val model: MainViewModel by viewModels()

    @Inject
    lateinit var adapter: ProductListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMainBinding.bind(view)
        val nav = activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)
        nav?.visibility = View.VISIBLE
        getAllCarBrands()
        binding.tabLayout.addOnTabSelectedListener(this)
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        binding.recyclerView.setHasFixedSize(true)
        handleBackPress()
    }

    private fun handleBackPress() {
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    activity?.finish()
                }
            }
            )
    }

    override fun onStart() {
        super.onStart()
        adapter.setOnItemClickListener(this)
        binding.retry.setOnClickListener {
            getCarsFromBrand(binding.tabLayout[binding.tabLayout.selectedTabPosition].toString())
        }
    }

    private fun getAllCarBrands() {
        val brands = resources.getStringArray(R.array.brands).drop(1)
        brands.forEach {
            val oneTab: TabLayout.Tab = binding.tabLayout.newTab()
            oneTab.text = it
            binding.tabLayout.addTab(oneTab)
        }
        getCarsFromBrand(brands[0])
    }

    private fun getCarsFromBrand(brand: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.retry.visibility = View.GONE
        binding.checkInternet.visibility = View.GONE

        model.getCarsFromBrand(brand).observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    adapter.getProducts(it.data!!)
                    binding.recyclerView.adapter = adapter
                    binding.retry.visibility = View.GONE
                    binding.progressBar.visibility = View.GONE
                    binding.checkInternet.visibility = View.GONE
                }

                Status.ERROR -> {
                    if (it.message != Constants.no_data){
                        binding.retry.visibility = View.VISIBLE
                    }
                    binding.progressBar.visibility = View.GONE
                    binding.checkInternet.visibility = View.VISIBLE
                    binding.checkInternet.text = it.message
                }
                Status.LOADING -> TODO()
            }
        })
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        getCarsFromBrand(tab?.text.toString())
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        adapter.getProducts(listOf())
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
        TODO("Not yet implemented")
    }

    override fun onItemClick(t: Any, p: Any?) {
        val action = MainFragmentDirections.actionMainFragmentToProductDetailFragment(
            t as String,
            p as String
        )
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}