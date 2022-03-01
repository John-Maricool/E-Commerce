package com.maricoolsapps.e_commerce.ui.product_ui.seller

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.data.adapters.ProductListAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.findNavController
import com.maricoolsapps.e_commerce.data.interfaces.OnItemClickListener
import com.maricoolsapps.e_commerce.data.model.Follow
import com.maricoolsapps.e_commerce.data.model.ProductModel
import com.maricoolsapps.e_commerce.databinding.FragmentSellerBinding
import com.maricoolsapps.e_commerce.utils.displaySnack
import com.maricoolsapps.e_commerce.utils.setResourceCenterCrop
import com.maricoolsapps.e_commerce.utils.showToast
import com.maricoolsapps.e_commerce.utils.toggleVisibility

@AndroidEntryPoint
class SellerFragment : Fragment(R.layout.fragment_seller), OnItemClickListener<ProductModel>,
    AdapterView.OnItemSelectedListener {

    private var _binding: FragmentSellerBinding? = null
    private val binding get() = _binding!!
    var isFollowed = false
    private val args: SellerFragmentArgs by navArgs()
    private val model: SellerViewModel by viewModels()
    lateinit var carBrands: Array<String>

    @Inject
    lateinit var adapter: ProductListAdapter

    lateinit var userNo: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSellerBinding.bind(view)

        model.sellerProfile(userToFollow = args.ownerId)
        toolbarInit()
        buttonClickListeners()
        initSpinnerAndAdapter()
        updateUI()
        observeLiveData()
    }

    private fun buttonClickListeners() {
        binding.follow.setOnClickListener {
            buttonClickFollow()
        }
        binding.call.setOnClickListener {
            if (this::userNo.isInitialized) {
                startActivity(model.callUser(userNo))
            }
        }
        binding.message.setOnClickListener {
            model.createChatChannel(userToChat = args.ownerId)
        }
    }

    override fun onStart() {
        super.onStart()
        adapter.setOnItemClickListener(this)
        binding.spinnerCategory.onItemSelectedListener = this
        binding.followers.setOnClickListener {
            val action =
                SellerFragmentDirections.actionSellerFragmentToFollowersFragment(args.ownerId)
            findNavController().navigate(action)
        }
    }

    private fun buttonClickFollow() {
        if (model.userId != args.ownerId) {
            if (isFollowed) {
                model.unfollowUser(userToUnFollow = args.ownerId)
                sellerUnfollowed()
            } else {
                val user = Follow(model.userId.toString())
                val userToFollow = Follow(args.ownerId)
                model.followUser(user, userToFollow)
                sellerFollowed()
            }
        } else {
            binding.follow.isEnabled = false
            activity?.showToast("You can't follow yourself")
            return
        }
    }

    private fun sellerFollowed() {
        isFollowed = true
        binding.follow.background =
            ResourcesCompat.getDrawable(resources, R.drawable.pink, null)
        binding.follow.setTextColor(resources.getColor(R.color.white, null))
        binding.follow.text = resources.getString(R.string.following)
    }

    private fun sellerUnfollowed() {
        isFollowed = false
        binding.follow.background =
            ResourcesCompat.getDrawable(resources, R.drawable.blue_border, null)
        binding.follow.setTextColor(resources.getColor(R.color.blue, null))
        binding.follow.text = resources.getString(R.string.follow)
    }

    private fun updateUI() {
        model.result.observe(viewLifecycleOwner) {
            if (it != null) {
                val seller = it.seller
                userNo = seller!!.phoneNumber
                binding.apply {
                    if (it.isFollowing == true) {
                        sellerFollowed()
                    } else {
                        sellerUnfollowed()
                    }
                    name.text = seller.name
                    binding.toolbar.title = seller.name
                    location.text = seller.businessLocation
                    image.setResourceCenterCrop(seller.image.toString())
                    followers.text = it.followers.toString()
                    following.text = it.following.toString()
                }
            }
        }
    }

    private fun observeLiveData() {
        adapter.getProducts(listOf())
        model.cars.observe(viewLifecycleOwner) {
            if (it == null || it.isEmpty()) {
                binding.error.toggleVisibility(true)
            } else {
                binding.recyclerView.toggleVisibility(true)
                binding.error.toggleVisibility(false)
                adapter.getProducts(it)
            }
        }
        model.defaultRepo.dataLoading.observe(viewLifecycleOwner) {
            binding.progressBar.toggleVisibility(it)
        }
        model.defaultRepo.resultError.observe(viewLifecycleOwner) {
            binding.progressBar.displaySnack(it)
        }

        model.channelCreated.observe(viewLifecycleOwner) {
            if (it != null){
        val action = SellerFragmentDirections.actionSellerFragmentToChatFragment(it)
                findNavController().navigate(action)
    }
}
}

override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
}

private fun initSpinnerAndAdapter() {
    carBrands = resources.getStringArray(R.array.brands).drop(1).toTypedArray()
    val spinnerAdapter = ArrayAdapter(
        requireActivity(),
        R.layout.support_simple_spinner_dropdown_item,
        carBrands
    )

    binding.spinnerCategory.adapter = spinnerAdapter
    binding.recyclerView.setHasFixedSize(true)
    binding.recyclerView.adapter = adapter

}

override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    val value = parent?.getItemAtPosition(position).toString()
    model.getCarsFromSeller(args.ownerId, value)
}

private fun toolbarInit() {
    (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
    val actionBar = (activity as AppCompatActivity).supportActionBar
    actionBar?.setDisplayHomeAsUpEnabled(true)
}

override fun onNothingSelected(parent: AdapterView<*>?) {
    TODO("Not yet implemented")
}

override fun onItemClick(t: Any, p: Any?) {
    val action = SellerFragmentDirections.actionSellerFragmentToProductDetailFragment(
        t as String,
        p as String
    )
    findNavController().navigate(action)
}

}