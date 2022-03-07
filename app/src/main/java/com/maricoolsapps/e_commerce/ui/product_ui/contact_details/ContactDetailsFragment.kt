package com.maricoolsapps.e_commerce.ui.product_ui.contact_details

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.data.model.CarBuyerOrSeller
import com.maricoolsapps.e_commerce.databinding.FragmentContactDetailsBinding
import com.maricoolsapps.e_commerce.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContactDetailsFragment : Fragment(R.layout.fragment_contact_details) {

    private var _binding: FragmentContactDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private val model: ContactDetailsViewModel by viewModels()
    private var imageUri: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    imageUri = result.data?.data.toString()
                    if (imageUri != null) {
                        binding.image.setResourceCenterCrop(imageUri!!)
                    }
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentContactDetailsBinding.bind(view)

        toolbarInit()
        setHasOptionsMenu(true)
        fillViews()
    }

    override fun onStart() {
        super.onStart()
        binding.image.setOnClickListener {
            val intent =
                Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            intent.type = "image/*"
            resultLauncher.launch(intent)
        }
        binding.changeEmail.setOnClickListener {
            findNavController().navigate(R.id.changeEmailAndPassword)
        }
    }

    private fun fillViews() {
        model.result.observe(viewLifecycleOwner) {
            if (it != null) {
                val user = it
                binding.name.setText(user.name)
                binding.email.setText(model.email)
                binding.number.setText(user.phoneNumber)
                binding.location.setText(user.businessLocation)
                binding.regions.setSelection(requireActivity().getRegionIndex(user.state))
                binding.image.setResourceCenterCrop(user.image.toString())
                imageUri = user.image
            }
        }
        model.changedProfile.observe(viewLifecycleOwner){
            if (it != null){
                activity?.showToast(Constants.successful)
                activity?.onBackPressed()
            }
        }
        model.defaultRepo.dataLoading.observe(viewLifecycleOwner){
            binding.progressBar.toggleVisibility(it)
        }
        model.defaultRepo.resultError.observe(viewLifecycleOwner){
            binding.progressBar.displaySnack(it)
        }
    }

    private fun saveToDb() {
        val location = binding.location.text.toString().trim()
        val name = binding.name.text.toString().trim()
        val email = binding.email.text.toString().trim()
        val number = binding.number.text.toString().trim()
        val region = binding.regions.selectedItem.toString()

        if (location.isEmpty() || name.isEmpty() || !validateEmail(email) ||
            number.isEmpty() || binding.regions.selectedItemPosition == 0
        ) {
            binding.progressBar.displaySnack("Complete your Entries")
        } else {
            val user =
                CarBuyerOrSeller(model.userID, imageUri, name, email, number, region, location)
            model.changeProfile(user)
        }
    }

    private fun toolbarInit() {
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        val actionBar = (activity as AppCompatActivity).supportActionBar
        binding.toolbar.title = "Edit Contact Details"
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.save, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> {
                saveToDb()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}