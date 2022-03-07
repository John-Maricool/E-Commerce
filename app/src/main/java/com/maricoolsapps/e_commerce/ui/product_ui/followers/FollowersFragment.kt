package com.maricoolsapps.e_commerce.ui.product_ui.followers

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.data.adapters.FollowersListAdapter
import com.maricoolsapps.e_commerce.data.interfaces.OnItemClickListener
import com.maricoolsapps.e_commerce.data.model.CarBuyerOrSeller
import com.maricoolsapps.e_commerce.databinding.FragmentFollowersBinding
import com.maricoolsapps.e_commerce.utils.Status
import com.maricoolsapps.e_commerce.utils.displaySnack
import com.maricoolsapps.e_commerce.utils.toggleVisibility
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FollowersFragment : Fragment(R.layout.fragment_followers),
    OnItemClickListener<CarBuyerOrSeller>, TabLayout.OnTabSelectedListener {
    private var _binding: FragmentFollowersBinding? = null
    private val binding get() = _binding!!
    private val model: FollowersViewModel by viewModels()
    private val args: FollowersFragmentArgs by navArgs()

    @Inject
    lateinit var adapterFollowers: FollowersListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFollowersBinding.bind(view)
        binding.recyclerView.apply {
            setHasFixedSize(true)
        }

        initTabs()
    }

    private fun initTabs() {
        val oneTab: TabLayout.Tab = binding.tabs.newTab()
        oneTab.text = "Followers"
        binding.tabs.addTab(oneTab)
        val twoTab: TabLayout.Tab = binding.tabs.newTab()
        twoTab.text = "Following"
        binding.tabs.addTab(twoTab)
    }

    override fun onStart() {
        super.onStart()
        model.getFollowers(args.ownerId)
        observeLiveData()
        binding.tabs.addOnTabSelectedListener(this)
        adapterFollowers.setOnItemClickListener(this)
    }

    private fun observeLiveData() {
        model.result.observe(viewLifecycleOwner) {
            if (it != null) {
                adapterFollowers.getUsers(it)
                binding.textError.toggleVisibility(false)
                binding.recyclerView.adapter = adapterFollowers
            }
        }
        model.defaultRepo.dataLoading.observe(viewLifecycleOwner) {
            binding.progressBar.toggleVisibility(it)
        }
        model.defaultRepo.resultError.observe(viewLifecycleOwner) {
            binding.textError.text = it
            binding.textError.toggleVisibility(true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onItemClick(t: Any, p: Any?) {
        val action =
            FollowersFragmentDirections.actionFollowersFragmentToSellerFragment(t as String)
        findNavController().navigate(action)
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        val value = tab?.text.toString()
        if (value == "Followers") {
            model.getFollowers(args.ownerId)
        } else {
            model.getFollowing(args.ownerId)
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        adapterFollowers.getUsers(listOf())
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
        val value = tab?.text.toString()
        if (value == "Followers") {
            model.getFollowers(args.ownerId)
        } else {
            model.getFollowing(args.ownerId)
        }
    }
}


