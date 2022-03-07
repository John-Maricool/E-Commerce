package com.maricoolsapps.e_commerce.ui.product_ui.adverts

import android.content.DialogInterface
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.AdapterView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.get
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.data.adapters.AdvertsListAdapter
import com.maricoolsapps.e_commerce.data.adapters.ProductListAdapter
import com.maricoolsapps.e_commerce.data.interfaces.OptionsMenuClickListener
import com.maricoolsapps.e_commerce.databinding.FragmentAdvertsBinding
import com.maricoolsapps.e_commerce.utils.showAlertDialog
import com.maricoolsapps.e_commerce.utils.toggleVisibility
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AdvertsFragment : Fragment(R.layout.fragment_adverts), AdapterView.OnItemSelectedListener,
    OptionsMenuClickListener {

    private var _binding: FragmentAdvertsBinding? = null
    private val binding get() = _binding!!
    private val model: AdvertsViewModel by viewModels()

    @Inject
    lateinit var adapterCar: AdvertsListAdapter

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
            val place = parent?.getItemAtPosition(position) as String
            model.getCarsFromSeller(place)
        }
    }

    private fun observeLiveData() {
        model.result.observe(viewLifecycleOwner) {
            binding.textError.toggleVisibility(false)
            binding.retry.toggleVisibility(false)
            if (it != null) {
                if (it.isEmpty()) {
                    binding.noResults.toggleVisibility(true)
                    binding.recyclerView.toggleVisibility(false)
                } else {
                    adapterCar.getProducts(it)
                    binding.recyclerView.toggleVisibility(true)
                    binding.noResults.toggleVisibility(false)
                }
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
        adapterCar.setOnItemClickListener(this)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun onOptionsClick(t: String, pos: Int) {
        performOptionClickMenu(t, pos)
    }

    private fun performOptionClickMenu(t: String, pos: Int) {
        val brand = binding.brands.selectedItem.toString()
        val view = binding.recyclerView[pos].findViewById<ImageView>(R.id.select)
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.inflate(R.menu.adverts_menu)
        popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem?): Boolean {
                when (item?.itemId) {
                    R.id.edit_prod -> {
                        val action = AdvertsFragmentDirections.actionAdvertsFragmentToEditProductFragment(t, brand)
                        findNavController().navigate(action)
                        return true
                    }
                    R.id.del_prod -> {
                        //delete product from db
                        showDialog(brand, t, pos)
                        return true
                    }
                    R.id.product_det -> {
                        val action = AdvertsFragmentDirections.goToProductDetails(t, brand)
                        findNavController().navigate(action)
                        return true
                    }
                }
                return false
            }
        })
        popupMenu.show()
    }

    private fun showDialog(brand: String, t: String, pos: Int) {
        val alert = context?.showAlertDialog(brand, "Do you want to delete this Product")
            alert?.setPositiveButton("Yes") { _, _ ->
                model.deleteProduct(t, brand)
                adapterCar.removeProduct(pos)
            }?.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
    }
}