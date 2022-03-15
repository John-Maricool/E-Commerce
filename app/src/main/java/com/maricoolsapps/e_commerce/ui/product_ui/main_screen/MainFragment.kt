package com.maricoolsapps.e_commerce.ui.product_ui.main_screen

import android.os.Bundle
import android.util.Log
import android.view.SearchEvent
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.data.adapters.ProductListAdapter
import com.maricoolsapps.e_commerce.data.adapters.ProductListAdapterPager
import com.maricoolsapps.e_commerce.data.interfaces.OnItemClickListener
import com.maricoolsapps.e_commerce.data.interfaces.OnViewSelectListener
import com.maricoolsapps.e_commerce.data.model.ProductModel
import com.maricoolsapps.e_commerce.data.model.UserStatus
import com.maricoolsapps.e_commerce.databinding.FragmentMainBinding
import com.maricoolsapps.e_commerce.ui.user_authentication_ui.MainActivity
import com.maricoolsapps.e_commerce.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main),
    OnItemClickListener<ProductModel>, OnViewSelectListener {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val model: MainViewModel by viewModels()

    @Inject
    lateinit var adapter: ProductListAdapter

    var selectedTabText = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMainBinding.bind(view)

        getAllCarBrands()
        binding.tabLayout.onSelectListener(this)
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

    override fun onStop() {
        super.onStop()
        model.toggleUserOnline(false)
    }

    override fun onStart() {
        super.onStart()
        adapter.setOnItemClickListener(this)
        binding.retry.setOnClickListener {
            getCarsFromBrand(selectedTabText)
        }
        binding.searchView.setOnClickListener {
            requireActivity().showToast("Not implemented this functionality")
        }
        binding.filter.setOnClickListener {
            findNavController().navigate(R.id.filteredListFragment)
        }
        observeLiveData()
        model.toggleUserOnline(true)
    }

    private fun observeLiveData() {
        model.result.observe(viewLifecycleOwner) {
            if (it != null) {
                adapter.getProducts(it)
                binding.recyclerView.adapter = adapter
            }
        }
        model.defaultRepo.dataLoading.observe(viewLifecycleOwner) {
            (activity as MainActivity).progressBar.toggleVisibility(it)
        }

        model.defaultRepo.resultError.observe(viewLifecycleOwner) {
            if (it != Constants.no_data) {
                binding.retry.toggleVisibility(true)
            }
            binding.checkInternet.toggleVisibility(true)
            binding.checkInternet.text = it
        }
    }

    private fun getAllCarBrands() {
        model.apply {
            binding.tabLayout.getAllCarBrands()
        }
    }

    private fun getCarsFromBrand(brand: String) {
        binding.retry.toggleVisibility(false)
        binding.checkInternet.toggleVisibility(false)
        model.getCarsFromBrand(brand)
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

    override fun onSelect(value: String) {
        getCarsFromBrand(value)
        selectedTabText = value
    }

    override fun onNoSelect() {
        adapter.getProducts(listOf())
    }
}