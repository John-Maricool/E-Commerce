package com.maricoolsapps.e_commerce.ui.product_ui.main_screen

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.data.adapters.ProductListAdapter
import com.maricoolsapps.e_commerce.data.interfaces.OnItemClickListener
import com.maricoolsapps.e_commerce.data.model.ProductModel
import com.maricoolsapps.e_commerce.data.model.UserStatus
import com.maricoolsapps.e_commerce.databinding.FragmentMainBinding
import com.maricoolsapps.e_commerce.utils.Constants
import com.maricoolsapps.e_commerce.utils.toggleVisibility
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main), TabLayout.OnTabSelectedListener{
    //, OnItemClickListener<ProductModel> {

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
       // getAllCarBrands()
        binding.recyclerView.setHasFixedSize(true)
        handleBackPress()
        val status = UserStatus(true, Date().time)
        model.toggleUserOnline(status)
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

    override fun onPause() {
        binding.tabLayout.addOnTabSelectedListener(this)
        val status = UserStatus(false, Date().time)
        model.toggleUserOnline(status)
        super.onPause()
    }

    override fun onStart() {
        super.onStart()
        //adapter.setOnItemClickListener(this)
        /*binding.retry.setOnClickListener {
            getCarsFromBrand(binding.tabLayout[binding.tabLayout.selectedTabPosition].toString())
        }
*/
        observeLiveData()
    }

    fun navigateToFilterFragment(){
        findNavController().navigate(R.id.filteredListFragment)
    }

    private fun observeLiveData() {
      /*  model.result.observe(viewLifecycleOwner) {
            if (it != null) {
                adapter.getProducts(it)
                binding.recyclerView.adapter = adapter
            }
        }*/
       /* model.defaultRepo.dataLoading.observe(viewLifecycleOwner) {
            binding.progressBar.toggleVisibility(it)
        }*/
        model.defaultRepo.resultError.observe(viewLifecycleOwner){
            if (it != Constants.no_data) {
                binding.retry.toggleVisibility(true)
            }

            binding.checkInternet.toggleVisibility(true)
            binding.checkInternet.text = it
        }
    }

    /*private fun getAllCarBrands() {
        model.apply {
            binding.tabLayout.getAllCarBrands()
        }
    }*/

    private fun getCarsFromBrand(brand: String){
        binding.retry.toggleVisibility(false)
        binding.checkInternet.toggleVisibility(false)
        model.getCarsFromBrand(brand)
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        getCarsFromBrand(tab?.text.toString())
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        adapter.getProducts(listOf())
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
        getCarsFromBrand(tab?.text.toString())
    }
/*
    override fun onItemClick(t: Any, p: Any?) {
        val action = MainFragmentDirections.actionMainFragmentToProductDetailFragment(
            t as String,
            p as String
        )
        findNavController().navigate(action)
    }*/

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}