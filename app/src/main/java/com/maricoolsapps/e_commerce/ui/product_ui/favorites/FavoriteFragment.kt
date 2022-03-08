package com.maricoolsapps.e_commerce.ui.product_ui.favorites

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.data.adapters.ProductListAdapter
import com.maricoolsapps.e_commerce.data.interfaces.OnItemClickListener
import com.maricoolsapps.e_commerce.data.model.ProductModel
import com.maricoolsapps.e_commerce.databinding.FragmentFavoriteBinding
import com.maricoolsapps.e_commerce.utils.displaySnack
import com.maricoolsapps.e_commerce.utils.toggleVisibility
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FavoriteFragment : Fragment(R.layout.fragment_favorite), OnItemClickListener<ProductModel> {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    //private val model: FavoriteViewModel by viewModels()

    @Inject
    lateinit var adapter: ProductListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFavoriteBinding.bind(view)
        toolbarInit()
        initAdapter()
        //observeLiveData()
    }
/*
    private fun observeLiveData() {
        model.result.observe(viewLifecycleOwner) {
            if (it != null && it.isNotEmpty()) {
                adapter.getProducts(it)
                binding.error.toggleVisibility(false)
            } else {
                binding.error.toggleVisibility(true)
            }
        }
        model.defaultRepo.dataLoading.observe(viewLifecycleOwner){
            binding.progressBar.toggleVisibility(it)
        }
        model.defaultRepo.resultError.observe(viewLifecycleOwner){
            binding.error.toggleVisibility(true)
            binding.error.displaySnack(it)
        }
    }*/

    private fun toolbarInit() {
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        binding.toolbar.title = resources.getString(R.string.favorites)
        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initAdapter() {
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = adapter
        adapter.setOnItemClickListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(t: Any, p: Any?) {
        val action = FavoriteFragmentDirections.goToProductDetails(t as String, p as String)
        findNavController().navigate(action)
    }
}