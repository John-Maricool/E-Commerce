package com.maricoolsapps.e_commerce.ui.product_ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.data.adapters.ProductListAdapter
import com.maricoolsapps.e_commerce.databinding.FragmentFavoriteBinding
import com.maricoolsapps.e_commerce.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FavoriteFragment : Fragment(R.layout.fragment_favorite) {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private val model: FavoriteViewModel by viewModels()

    @Inject
    lateinit var adapter: ProductListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFavoriteBinding.bind(view)
        toolbarInit()
        initAdapter()

        model.result.observe(viewLifecycleOwner, {
            binding.progressBar.visibility = View.VISIBLE
            when(it.status) {
                Status.SUCCESS -> {
                    if (it.data != null && it.data.isNotEmpty()) {
                        binding.error.visibility = View.GONE
                        adapter.getProducts(it.data)
                    } else {
                        binding.error.visibility = View.VISIBLE
                    }
                    binding.progressBar.visibility = View.GONE
                }
            Status.ERROR -> {
                    binding.progressBar.visibility = View.GONE
                    binding.error.visibility = View.VISIBLE
                }
                Status.LOADING -> TODO()
            }
        })
    }

    private fun toolbarInit() {
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        binding.toolbar.title = resources.getString(R.string.favorites)
        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initAdapter() {
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        binding.recyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}