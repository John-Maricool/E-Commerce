package com.maricoolsapps.e_commerce.product_ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.adapters.ProductListAdapter
import com.maricoolsapps.e_commerce.databinding.FragmentAdvertsBinding
import com.maricoolsapps.e_commerce.utils.Status
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
    }

    override fun onStart() {
        super.onStart()
        initRecyclerView()
        binding.retry.setOnClickListener {
            updateRecyclerView(binding.brands.selectedItem.toString())
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
        if (position > 0){
            val place: String = parent?.getItemAtPosition(position) as String
            updateRecyclerView(place)
        }
    }

    private fun updateRecyclerView(place: String) {
        binding.progressBar.visibility = View.VISIBLE
        model.getCarsFromSeller(place).observe(viewLifecycleOwner, Observer {
            when(it.status){
                Status.SUCCESS -> {
                    binding.progressBar.visibility = View.GONE
                    if (it.data == null || it.data.isEmpty()){
                        binding.noResults.visibility = View.VISIBLE
                    }else{
                        adapterCar.getProducts(it.data)
                    }
                }
                Status.ERROR -> {
                    if (it.message == "Check your internet connection"){
                        binding.retry.visibility = View.VISIBLE
                    }
                    binding.progressBar.visibility = View.GONE
                    binding.noResults.visibility = View.GONE
                    binding.textError.text = it.message
                    binding.textError.visibility = View.VISIBLE
                }
                Status.LOADING -> TODO()
            }
        })
    }

    private fun initRecyclerView(){
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = adapterCar
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}