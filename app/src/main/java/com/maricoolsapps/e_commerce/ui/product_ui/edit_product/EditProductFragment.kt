package com.maricoolsapps.e_commerce.ui.product_ui.edit_product

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.data.model.Product
import com.maricoolsapps.e_commerce.databinding.FragmentEditProductBinding
import com.maricoolsapps.e_commerce.ui.user_authentication_ui.MainActivity
import com.maricoolsapps.e_commerce.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProductFragment : Fragment(R.layout.fragment_edit_product) {

    private var _binding: FragmentEditProductBinding? = null
    private val binding get() = _binding!!
    private val model: EditProductViewModel by viewModels()
    private val args: EditProductFragmentArgs by navArgs()
    lateinit var car: Product

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentEditProductBinding.bind(view)

        (activity as MainActivity).toolbar.apply {
            title = "Edit Car"
            setBackgroundColor(resources.getColor(R.color.grey, null))
        }

        model.getProduct(args.id, args.brand)
        observeLiveData()
        buttonClickListeners()
    }

    private fun buttonClickListeners() {
        binding.updateProduct.setOnClickListener {
            val cond = binding.condition.selectedItem.toString()
            val desc = binding.desc.text.toString().trim()
            val price = binding.price.text.toString().trim()
            val state = binding.state.selectedItem.toString()

            if (binding.state.selectedItemPosition == 0 ||
                binding.condition.selectedItemPosition == 0 ||
                price.isEmpty() ||
                desc.isEmpty() ||
                !::car.isInitialized
            ) {
                return@setOnClickListener
            } else {
                if (cond == car.condition && state == car.state && car.description == desc && car.price == price.toLong()) {
                    return@setOnClickListener
                } else {
                    car.condition = cond
                    car.state = state
                    car.description = desc
                    car.price = price.toLong()
                    model.updateCar(car)
                }
            }
        }
    }

    private fun observeLiveData() {
        model.result.observe(viewLifecycleOwner) {
            if (it != null) {
                car = it
                binding.apply {
                    desc.setText(car.description)
                    price.setText(car.price.toString())
                    condition.setSelection(requireActivity().getConditionIndex(car.condition))
                    state.setSelection(requireActivity().getStateIndex(car.state))
                }
            }
        }
        model.defaultRepo.resultError.observe(viewLifecycleOwner) {
            (activity as MainActivity).progressBar.displaySnack(it)
        }
        model.defaultRepo.dataLoading.observe(viewLifecycleOwner) {
            (activity as MainActivity).progressBar.toggleVisibility(it)
        }
        model.updated.observe(viewLifecycleOwner) {
            if (it != null) {
                requireActivity().showToast(it)
                findNavController().navigate(R.id.advertsFragment)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}