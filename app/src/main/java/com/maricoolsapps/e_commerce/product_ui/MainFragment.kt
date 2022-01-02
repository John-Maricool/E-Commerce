package com.maricoolsapps.e_commerce.product_ui

import android.os.Bundle
import android.view.View
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.adapters.ProductListAdapter
import com.maricoolsapps.e_commerce.databinding.FragmentMainBinding
import com.maricoolsapps.e_commerce.interfaces.onItemClickListener
import com.maricoolsapps.e_commerce.model.Product
import com.maricoolsapps.e_commerce.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main), TabLayout.OnTabSelectedListener,
    onItemClickListener<Product> {

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
    }

    override fun onStart() {
        super.onStart()
            binding.recyclerView.layoutManager = LinearLayoutManager(activity)
            binding.recyclerView.setHasFixedSize(true)
            binding.recyclerView.adapter = adapter
            binding.tabLayout.addOnTabSelectedListener(this)
        adapter.setOnItemClickListener(this)
    }

   private fun getAllCarBrands(){
       binding.progressBar.visibility = View.VISIBLE
       model.getAllCarBrands().observe(viewLifecycleOwner, {
            when(it.status){
                Status.SUCCESS -> {
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                    it.data?.forEach {
                        val oneTab = binding.tabLayout.newTab()
                        oneTab.text= it
                        binding.tabLayout.addTab(oneTab)
                    }
                }

                Status.ERROR -> {
                    binding.progressBar.visibility = View.GONE
                    Snackbar.make(binding.tabLayout, it.message.toString(), Snackbar.LENGTH_LONG)
                        .setBackgroundTint(resources.getColor(R.color.pink2, null))
                        .show()
                }
                Status.LOADING -> TODO()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getCarsFromBrand(brand: String){
        binding.progressBar.visibility = View.VISIBLE
        model.getCarsFromBrand(brand).observe(viewLifecycleOwner, {
            when(it.status){
                Status.SUCCESS -> {
                    binding.progressBar.visibility = View.GONE
                    if (it.data!!.isEmpty()) {
                        binding.noResult.visibility = View.VISIBLE
                    } else {
                        adapter.getProducts(it.data)
                        binding.noResult.visibility = View.GONE
                    }
                    binding.checkInternet.visibility = View.GONE
                    binding.retry.visibility = View.GONE
                }
                Status.ERROR -> {
                    binding.progressBar.visibility = View.GONE
                    binding.checkInternet.visibility = View.VISIBLE
                    binding.retry.visibility = View.VISIBLE
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
        getCarsFromBrand(tab?.text.toString())
    }

    override fun onItemClick(t: Product) {
        val action = MainFragmentDirections.actionMainFragmentToProductDetailFragment(t)
        findNavController().navigate(action)
    }

    override fun onButtonClick(t: Product) {

    }
}