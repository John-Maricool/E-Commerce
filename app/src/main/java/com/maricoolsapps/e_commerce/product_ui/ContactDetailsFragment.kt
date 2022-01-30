package com.maricoolsapps.e_commerce.product_ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.databinding.FragmentContactDetailsBinding
import com.maricoolsapps.e_commerce.model.CarBuyerOrSeller
import com.maricoolsapps.e_commerce.utils.Constants
import com.maricoolsapps.e_commerce.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ContactDetailsFragment : Fragment(R.layout.fragment_contact_details){

    private var _binding: FragmentContactDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private val model: ContactDetailsViewModel by viewModels()
    private var imageUri: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                imageUri = result.data?.data.toString()
                if (imageUri != null) {
                    Glide.with(requireActivity())
                        .load(imageUri)
                        .circleCrop()
                        .into(binding.image)
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

    private fun setRegion(region: String?): Int {
        val regionsInArray = resources.getStringArray(R.array.car_town)
        var index = 0
        if (region != null) {
            for (i in regionsInArray.indices) {
                if (regionsInArray[i] == region) {
                    index = i
                }
            }
        }else{
            index = 0
        }
        return index
    }

    override fun onStart() {
        super.onStart()
        binding.image.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            intent.type = "image/*"
            resultLauncher.launch(intent)
        }
        binding.changeEmail.setOnClickListener {
            val action = ContactDetailsFragmentDirections.actionContactDetailsFragmentToChangeEmailAndPassword()
            findNavController().navigate(action)
        }
    }

    private fun fillViews() {
        binding.progressBar.visibility = View.VISIBLE
        model.result.observe(viewLifecycleOwner, {
            when(it.status){
                Status.SUCCESS -> {
                    val user = it.data
                    binding.name.setText(user?.name)
                    binding.email.setText(model.profileChanges.auth.currentUser?.email)
                    binding.number.setText(user?.phoneNumber)
                    binding.location.setText(user?.businessLocation)
                    binding.regions.setSelection(setRegion(user?.state))
                    Glide.with(this)
                        .load(user?.image)
                        .placeholder(R.drawable.car)
                        .circleCrop()
                        .into(binding.image)
                    imageUri = user?.image
                    binding.progressBar.visibility = View.INVISIBLE
                }
                Status.ERROR -> {
                    Snackbar.make(binding.image, it.message.toString(), Snackbar.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.INVISIBLE
                }
                Status.LOADING -> TODO()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.save, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.save -> {
                saveToDb()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveToDb() {
        val location = binding.location.text.toString().trim()
        val name = binding.name.text.toString().trim()
        val email = binding.email.text.toString().trim()
        val number = binding.number.text.toString().trim()
        val region = binding.regions.selectedItem.toString()

        if (location.isEmpty() || name.isEmpty() || email.isEmpty() ||
            number.isEmpty() || binding.regions.selectedItemPosition == 0){
            model.showSnackBar(binding.progressBar, "Complete your Entries", requireActivity())
        }else{
            binding.progressBar.visibility = View.VISIBLE
            val user = CarBuyerOrSeller("", imageUri, name, email, number, region, location)
            lifecycleScope.launch(Main) {
                val job = lifecycleScope.launch(IO) {
                    model.changeProfile(user)
                }
                job.join()
                if (job.isCompleted){
                    binding.progressBar.visibility = View.INVISIBLE
                    Toast.makeText(activity, "Success", Toast.LENGTH_SHORT).show()
                    activity?.onBackPressed()
                }
            }
        }
    }

    private fun toolbarInit() {
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        val actionBar = (activity as AppCompatActivity).supportActionBar
        binding.toolbar.title = "Edit Contact Details"
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}