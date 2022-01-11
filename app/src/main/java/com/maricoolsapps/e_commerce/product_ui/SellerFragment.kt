package com.maricoolsapps.e_commerce.product_ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.adapters.ProductListAdapter
import com.maricoolsapps.e_commerce.databinding.FragmentSellerBinding
import com.maricoolsapps.e_commerce.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.maricoolsapps.e_commerce.interfaces.OnItemClickListener
import com.maricoolsapps.e_commerce.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SellerFragment : Fragment(R.layout.fragment_seller), OnItemClickListener<Product>,
    AdapterView.OnItemSelectedListener {

    private var _binding: FragmentSellerBinding? = null
    private val binding get() = _binding!!
    var isFollowed = false
    private val args: SellerFragmentArgs by navArgs()
    private val model: SellerViewModel by viewModels()
    lateinit var carBrands: Array<String>

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var adapter: ProductListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSellerBinding.bind(view)

        binding.follow.setOnClickListener {
            buttonClickFollow()
        }

        initSpinnerAndAdapter()
        updateFollowButton()
        updateUI()

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        binding.toolbar.title = args.ownerId

    }

    override fun onStart() {
        super.onStart()
        adapter.setOnItemClickListener(this)
       binding.spinnerCategory.onItemSelectedListener = this
    }

    private fun buttonClickFollow() {
        if (isFollowed){
            model.unfollowUser(auth.currentUser?.displayName.toString(), args.ownerId)
            sellerUnfollowed()
        }else{
            model.followUser(auth.currentUser?.displayName.toString(), args.ownerId)
            sellerFollowed()
        }
    }

    private fun updateFollowButton() {
        model.isUserFollowed(auth.currentUser?.displayName.toString(), args.ownerId).observe(viewLifecycleOwner, {
            when(it.status) {
                Status.SUCCESS -> {
                  sellerFollowed()
                }
                Status.ERROR -> {
                    sellerUnfollowed()
                }
                Status.LOADING -> {

                }
            }
        })
    }

    private fun sellerFollowed(){
        isFollowed = true
        binding.follow.background = ResourcesCompat.getDrawable(resources, R.drawable.blue_solid, null)
        binding.follow.setTextColor(resources.getColor(R.color.white, null))
        binding.follow.text = resources.getString(R.string.following)
    }

    private fun sellerUnfollowed(){
        isFollowed = false
        binding.follow.background = ResourcesCompat.getDrawable(resources, R.drawable.blue_border, null)
        binding.follow.setTextColor(resources.getColor(R.color.blue, null))
        binding.follow.text = resources.getString(R.string.follow)
    }

    private fun initSpinnerAndAdapter() {
         carBrands = resources.getStringArray(R.array.brands)
        val spinnerAdapter = ArrayAdapter(requireActivity(),
            R.layout.support_simple_spinner_dropdown_item,
            carBrands)

        binding.spinnerCategory.adapter = spinnerAdapter
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        binding.recyclerView.adapter = adapter

    }

    private fun updateUI(){
        model.sellerProfile(args.ownerId).observe(viewLifecycleOwner, {
            when(it.status){
                Status.SUCCESS -> {
                        val seller = it.data
                        binding.apply {
                            name.text = seller?.name
                            location.text = seller?.businessLocation
                            Glide.with(requireActivity())
                                .load(seller?.image)
                                .circleCrop()
                                .into(image)
                        }
                    }
                Status.ERROR -> {
                    binding.error.visibility = View.VISIBLE
                    model.showSnackBar(binding.recyclerView, it.message.toString(), requireActivity())
                }
                Status.LOADING -> TODO()
            }
        })
        model.getFollowers(args.ownerId).observe(viewLifecycleOwner, {
            when(it.status){
                Status.SUCCESS -> {
                    binding.followers.text = it.data.toString()
                }
                Status.ERROR -> {
                    model.showSnackBar(binding.recyclerView, it.message!!, requireActivity())
                }
                Status.LOADING -> TODO()
            }
        })
        model.getFollowing(args.ownerId).observe(viewLifecycleOwner, {
            when(it.status){
                Status.SUCCESS -> {
                    binding.following.text = it.data.toString()
                }
                Status.ERROR -> {
                    model.showSnackBar(binding.recyclerView, it.message!!, requireActivity())
                }
                Status.LOADING -> TODO()
            }
        })
        getSellerCars(args.ownerId, carBrands[0])

    }

    private fun getSellerCars(name: String, brand: String){
        adapter.getProducts(listOf())
        model.getCarsFromSeller(name, brand).observe(viewLifecycleOwner,{
            when(it.status){
                Status.SUCCESS -> {
                    if (it.data == null || it.data.isEmpty()){
                        binding.recyclerView.visibility = View.GONE
                        binding.error.visibility = View.VISIBLE
                    }else{
                        binding.recyclerView.visibility = View.VISIBLE
                        binding.error.visibility = View.GONE
                        adapter.getProducts(it.data)
                    }
                }
                Status.ERROR -> {
                    model.showSnackBar(binding.recyclerView, it.message.toString(), requireActivity())
                }
                Status.LOADING -> TODO()
            }
        })
    }

    override fun onItemClick(t: Product) {
        val action = SellerFragmentDirections.actionSellerFragmentToProductDetailFragment(t)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val value = parent?.getItemAtPosition(position).toString()
        getSellerCars(args.ownerId, value)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

}