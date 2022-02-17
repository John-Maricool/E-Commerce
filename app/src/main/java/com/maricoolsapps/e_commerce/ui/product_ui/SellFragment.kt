package com.maricoolsapps.e_commerce.ui.product_ui

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.maricoolsapps.e_commerce.R
import dagger.hilt.android.AndroidEntryPoint
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import android.content.ClipData
import android.widget.*
import androidx.fragment.app.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.maricoolsapps.e_commerce.databinding.FragmentSellBinding
import com.maricoolsapps.e_commerce.data.model.Product
import com.maricoolsapps.e_commerce.utils.Status
import javax.inject.Inject

@AndroidEntryPoint
class SellFragment : Fragment(R.layout.fragment_sell), AdapterView.OnItemSelectedListener {

    private var _binding: FragmentSellBinding? = null

    @Inject
    lateinit var auth: FirebaseAuth

    private val viewModel: SellViewModel by viewModels()
    private val binding get() = _binding!!
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private var position: Int = 0
    val imagesUri = mutableListOf<Uri>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                    binding.image.visibility = View.VISIBLE
                    binding.next.visibility = View.VISIBLE
                    binding.removeImage.visibility = View.VISIBLE
                    binding.previous.visibility = View.VISIBLE
                    if (result.data!!.clipData != null) {
                        val mClipData: ClipData? = result.data!!.clipData
                        val cout: Int = mClipData!!.itemCount
                        for (i in 0 until cout) {
                            val imageurl: Uri = mClipData.getItemAt(i).uri
                            imagesUri.add(imageurl)
                        }
                        binding.image.setImageURI(imagesUri[0])
                        binding.image.tag = imagesUri[0]
                    } else {
                        val imageurl = result.data!!.data
                        imagesUri.add(imageurl!!)
                        binding.image.setImageURI(imagesUri[0]);
                    }
                } else {
                    binding.image.visibility = View.GONE
                    binding.next.visibility = View.GONE
                    binding.removeImage.visibility = View.GONE
                    binding.previous.visibility = View.GONE
                    Toast.makeText(activity, "Error loading images", Toast.LENGTH_LONG).show()
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentSellBinding.bind(view)

        binding.image.setFactory {
            ImageView(activity)
        }
        toolbarInit()
        buttonListener()
        binding.models.isEnabled = false
        binding.brands.onItemSelectedListener = this
    }

    private fun buttonListener() {
        binding.addPhoto.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.action = Intent.ACTION_GET_CONTENT
            resultLauncher.launch(intent)
        }

        binding.next.setOnClickListener {
            if (position < imagesUri.size - 1) {
                position++
                binding.image.setImageURI(imagesUri[position])
                binding.image.tag = imagesUri[position]
            } else {
                Toast.makeText(activity, "You have reached the end", Toast.LENGTH_LONG).show()
            }
        }

        binding.previous.setOnClickListener {
            if (position > 0) {
                position--
                binding.image.setImageURI(imagesUri[position]);
                binding.image.tag = imagesUri[position]

            } else {
                Toast.makeText(activity, "You have reached the start", Toast.LENGTH_LONG).show()
            }
        }

        binding.removeImage.setOnClickListener {
                val tag = binding.image.tag
                imagesUri.remove(tag)
                binding.next.callOnClick()
        }

        binding.postAd.setOnClickListener {
            getAllItems()
        }
    }

    private fun getAllItems() {
        if (imagesUri.size < 1){
            viewModel.showSnackBar(binding.progressBar, "Minimum of 3 photos is required", requireActivity())
            return
        }
        if (binding.brands.selectedItemPosition == 0 ||
            binding.regions.selectedItemPosition == 0 ||
            binding.models.selectedItemPosition == 0 ||
            binding.colors.selectedItemPosition == 0 ||
            binding.rating.selectedItemPosition == 0 ||
            binding.state.selectedItemPosition == 0 ||
            binding.condition.selectedItemPosition == 0 ||
            binding.price.text.toString().trim().isEmpty() ||
            binding.location.text.toString().trim().isEmpty() ||
            binding.yrOfManufacturing.text.toString().trim().isEmpty() ||
            binding.desc.text.toString().trim().isEmpty()){

                viewModel.showSnackBar(binding.progressBar, "Entries is not complete", requireActivity())
            return
        }

      sendItems()
    }

    private fun sendItems() {
        val brand = binding.brands.selectedItem.toString()
        val model = binding.models.selectedItem.toString()
        val region = binding.regions.selectedItem.toString()
        val state = binding.state.selectedItem.toString()
        val condition =  binding.condition.selectedItem.toString()
        val colors = binding.colors.selectedItem.toString()
        val ratings = binding.rating.selectedItem.toString()
        val price = binding.price.text.toString().toLong()
        val address = binding.location.text.toString().trim()
        val yearOfManufacturing = binding.yrOfManufacturing.text.toString().trim()
        val desc = binding.desc.text.toString().trim()
        val id = viewModel.getRandomString()

        disableAllViews()

        binding.progressBar.visibility = View.VISIBLE
        viewModel.getAllImages(imagesUri.toList())
        viewModel.result.observe(viewLifecycleOwner, {
            val product = Product(
                model,
                desc,
                state,
                auth.currentUser?.uid!!,
                address,
                yearOfManufacturing,
                region,
                id,
                condition,
                brand,
                colors,
                price,
                ratings,
                it)

            viewModel.addCarToDb(product, product.ownerId)
        })
        viewModel.addResult.observe(viewLifecycleOwner, {
            when(it.status){
                Status.SUCCESS -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(activity, "Successful", Toast.LENGTH_SHORT).show()
                    enableAllViews()
                }
                Status.ERROR -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show()
                    enableAllViews()
                }
                Status.LOADING -> TODO()
            }
        })
    }

    private fun enableAllViews() {
        binding.brands.isEnabled = true
        binding.models.isEnabled = true
        binding.regions.isEnabled = true
        binding.state.isEnabled = true
        binding.condition.isEnabled = true
        binding.colors.isEnabled = true
        binding.rating.isEnabled = true
        binding.price.isEnabled = true
        binding.location.isEnabled = true
        binding.yrOfManufacturing.isEnabled = true
        binding.desc.isEnabled = true
        binding.postAd.isEnabled = true
    }

    private fun disableAllViews() {
        binding.brands.isEnabled = false
        binding.models.isEnabled = false
        binding.regions.isEnabled = false
        binding.state.isEnabled = false
        binding.condition.isEnabled = false
        binding.colors.isEnabled = false
        binding.rating.isEnabled = false
        binding.price.isEnabled = false
        binding.location.isEnabled = false
        binding.yrOfManufacturing.isEnabled = false
        binding.desc.isEnabled = false
        binding.postAd.isEnabled = false
    }

    private fun toolbarInit() {
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        binding.toolbar.title = "Sell Car"
        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (position > 0) {
            when (parent?.getItemAtPosition(position).toString()) {
                "Lexus" -> {
                    val carModels = resources.getStringArray(R.array.Lexus)
                    val spinnerAdapter = ArrayAdapter(
                        requireActivity(),
                        R.layout.support_simple_spinner_dropdown_item,
                        carModels
                    )
                    binding.models.adapter = spinnerAdapter
                }
                "Acura" -> {
                    val carModels = resources.getStringArray(R.array.Acura)
                    val spinnerAdapter = ArrayAdapter(
                        requireActivity(),
                        R.layout.support_simple_spinner_dropdown_item,
                        carModels
                    )
                    binding.models.adapter = spinnerAdapter
                }
                "Toyota" -> {
                    val carModels = resources.getStringArray(R.array.Toyota)
                    val spinnerAdapter = ArrayAdapter(
                        requireActivity(),
                        R.layout.support_simple_spinner_dropdown_item,
                        carModels
                    )
                    binding.models.adapter = spinnerAdapter
                }
                "Honda" -> {
                    val carModels = resources.getStringArray(R.array.Honda)
                    val spinnerAdapter = ArrayAdapter(
                        requireActivity(),
                        R.layout.support_simple_spinner_dropdown_item,
                        carModels
                    )
                    binding.models.adapter = spinnerAdapter
                }
                "Ford" -> {
                    val carModels = resources.getStringArray(R.array.Ford)
                    val spinnerAdapter = ArrayAdapter(
                        requireActivity(),
                        R.layout.support_simple_spinner_dropdown_item,
                        carModels
                    )
                    binding.models.adapter = spinnerAdapter
                }
                "Mercedes-Benz" -> {
                    val carModels =
                        resources.getStringArray(R.array.MercedesBenz)
                    val spinnerAdapter = ArrayAdapter(
                        requireActivity(),
                        R.layout.support_simple_spinner_dropdown_item,
                        carModels
                    )
                    binding.models.adapter = spinnerAdapter
                }
                "BMW" -> {
                    val carModels = resources.getStringArray(R.array.BMW)
                    val spinnerAdapter = ArrayAdapter(
                        requireActivity(),
                        R.layout.support_simple_spinner_dropdown_item,
                        carModels
                    )
                    binding.models.adapter = spinnerAdapter
                }
            }
            binding.models.isEnabled = true
        } else {
            binding.models.isEnabled = false
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}