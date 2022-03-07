package com.maricoolsapps.e_commerce.ui.product_ui.filter

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.AdapterView
import androidx.fragment.app.viewModels
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.data.adapters.ProductListAdapter
import com.maricoolsapps.e_commerce.databinding.FilteredListFragmentBinding
import com.maricoolsapps.e_commerce.ui.user_authentication_ui.MainActivity
import com.maricoolsapps.e_commerce.utils.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FilteredListFragment : Fragment(R.layout.filtered_list_fragment),
    AdapterView.OnItemSelectedListener {

    private var _binding: FilteredListFragmentBinding? = null
    private val binding get() = _binding!!
    private val model: FilteredListViewModel by viewModels()

    @Inject
    lateinit var adapter: ProductListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FilteredListFragmentBinding.bind(view)
        initToolBar()
        binding.spinnerModels.isEnabled = false
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = adapter
        observeLiveData()
        binding.spinnerBrands.onItemSelectedListener = this
    }

    private fun observeLiveData() {
        model.defaultRepo.resultError.observe(viewLifecycleOwner){
            binding.progressBar.displaySnack(it)
        }
        model.defaultRepo.dataLoading.observe(viewLifecycleOwner){
            binding.progressBar.toggleVisibility(it)
        }
        model.result.observe(viewLifecycleOwner){
            if (it != null) {
                binding.recyclerView.toggleVisibility(true)
                adapter.getProducts(it)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        binding.showAdverts.setOnClickListener {
            val brand = binding.spinnerBrands.selectedItem.toString()
            val type = binding.spinnerModels.selectedItem.toString()
            val condition = binding.spinnerCondition.selectedItem.toString()
            val location = binding.spinnerLocation.selectedItem.toString()
            val minPrice = binding.priceMin.text.toString().trim()
            val maxPrice = binding.priceMax.text.toString().trim()
                if (binding.spinnerBrands.selectedItemPosition == 0 ||
                    binding.spinnerModels.selectedItemPosition == 0 ||
                    binding.spinnerCondition.selectedItemPosition == 0 ||
                    binding.spinnerLocation.selectedItemPosition == 0 ||
                    minPrice.isEmpty() ||
                    maxPrice.isEmpty()
                ) {
                    binding.progressBar.displaySnack("Entries is not Complete")
                    return@setOnClickListener
                }
            binding.myGroup.toggleVisibility(false)
            model.showAdverts(brand, type, location, condition, minPrice.toLong(), maxPrice.toLong())
        }
    }

    private fun initToolBar() {
        (activity as MainActivity).setSupportActionBar(binding.toolbar)
        val actionBar = (activity as MainActivity).supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.title = "Filter"
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (position > 0) {
            when (parent?.getItemAtPosition(position).toString()) {
                "Lexus" -> binding.spinnerModels.adapter = activity?.getLexusAdapter()
                "Acura" -> binding.spinnerModels.adapter = activity?.getAcuraAdapter()
                "Toyota" -> binding.spinnerModels.adapter = activity?.getToyotaAdapter()
                "Honda" ->binding.spinnerModels.adapter = activity?.getHondaAdapter()
                "Ford" -> binding.spinnerModels.adapter = activity?.getFordAdapter()
                "Mercedes-Benz" -> binding.spinnerModels.adapter = activity?.getBenzAdapter()
                "BMW" -> binding.spinnerModels.adapter = activity?.getBMWAdapter()
            }
            binding.spinnerModels.isEnabled = true
        } else {
            binding.spinnerModels.isEnabled = false
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}