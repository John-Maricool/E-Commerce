package com.maricoolsapps.e_commerce.product_ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.adapters.ProductListAdapter
import com.maricoolsapps.e_commerce.databinding.FragmentMainBinding
import com.maricoolsapps.e_commerce.interfaces.OnItemClickListener
import com.maricoolsapps.e_commerce.model.Product
import com.maricoolsapps.e_commerce.model.ProductModel
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
    }

    override fun onStart() {
        super.onStart()
        adapter.setOnItemClickListener(this)
    }

   private fun getAllCarBrands(){
       val brands = resources.getStringArray(R.array.brands).drop(1)
       brands.forEach {
           val oneTab: TabLayout.Tab = binding.tabLayout.newTab()
           oneTab.text = it
           binding.tabLayout.addTab(oneTab)
       }
       getCarsFromBrand(brands[0])
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
                    if (it.data == null || it.data.isEmpty()){
                        binding.noResult.visibility = View.VISIBLE
                    }else{
                        adapter.getProducts(it.data)
                        binding.recyclerView.adapter = adapter
                    }
                    binding.progressBar.visibility = View.GONE
                    binding.noResult.visibility = View.GONE
                    binding.checkInternet.visibility = View.GONE
                }

                Status.ERROR -> {
                    binding.progressBar.visibility = View.GONE
                    binding.noResult.visibility = View.GONE
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
    }

    override fun onItemClick(t: Any, p: Any?) {
        val action = MainFragmentDirections.actionMainFragmentToProductDetailFragment(t as String, p as String)
        findNavController().navigate(action)
    }

}