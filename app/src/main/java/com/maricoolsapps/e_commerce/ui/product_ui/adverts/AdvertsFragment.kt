package com.maricoolsapps.e_commerce.ui.product_ui.adverts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.data.adapters.ProductListAdapter
import com.maricoolsapps.e_commerce.databinding.FragmentAdvertsBinding
import com.maricoolsapps.e_commerce.utils.toggleVisibility
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AdvertsFragment : Fragment(R.layout.fragment_adverts), AdapterView.OnItemSelectedListener {

    private var _binding: FragmentAdvertsBinding? = null
    private val binding get() = _binding!!
    private val model: AdvertsViewModel by viewModels()

    @Inject
    lateinit var adapterCar: ProductListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAdvertsBinding.bind(view)
        toolbarInit()
        observeLiveData()
    }

    override fun onStart() {
        super.onStart()
        initRecyclerView()
        binding.retry.setOnClickListener {
            model.getCarsFromSeller(binding.brands.selectedItem.toString())
        }
    }

    private fun toolbarInit() {
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        val actionBar = (activity as AppCompatActivity).supportActionBar
        binding.toolbar.title = "Adverts"
        actionBar?.setDisplayHomeAsUpEnabled(true)

        binding.brands.onItemSelectedListener = this
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (position > 0) {
            val place: String = parent?.getItemAtPosition(position) as String
            model.getCarsFromSeller(place)
        }
    }

    private fun observeLiveData() {
        model.result.observe(viewLifecycleOwner) {
            binding.textError.toggleVisibility(false)
            binding.retry.toggleVisibility(false)
            if (it.isEmpty()) {
                binding.noResults.toggleVisibility(true)
            } else {
                adapterCar.getProducts(it)
            }
        }

        model.defaultRepo.dataLoading.observe(viewLifecycleOwner) {
            binding.progressBar.toggleVisibility(it)
        }
        model.defaultRepo.resultError.observe(viewLifecycleOwner) {
            binding.textError.toggleVisibility(true)
            binding.textError.text = it
            binding.retry.toggleVisibility(true)
        }
    }

    private fun initRecyclerView() {
        binding.recyclerView.apply {
            setHasFixedSize(true)
            adapter = adapterCar
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}