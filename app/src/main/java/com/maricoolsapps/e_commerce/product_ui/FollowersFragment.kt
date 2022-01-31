package com.maricoolsapps.e_commerce.product_ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.adapters.FollowersListAdapter
import com.maricoolsapps.e_commerce.databinding.FragmentFollowersBinding
import com.maricoolsapps.e_commerce.interfaces.OnItemClickListener
import com.maricoolsapps.e_commerce.model.CarBuyerOrSeller
import com.maricoolsapps.e_commerce.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FollowersFragment : Fragment(R.layout.fragment_followers),
    OnItemClickListener<CarBuyerOrSeller>, TabLayout.OnTabSelectedListener {
    private var _binding: FragmentFollowersBinding? = null
    private val binding get() = _binding!!
    private val model: FollowersViewModel by viewModels()

    @Inject
    lateinit var adapterFollowers: FollowersListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFollowersBinding.bind(view)
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
        }

        val oneTab: TabLayout.Tab = binding.tabs.newTab()
        oneTab.text = "Followers"
        binding.tabs.addTab(oneTab)
        val twoTab: TabLayout.Tab = binding.tabs.newTab()
        twoTab.text = "Following"
        binding.tabs.addTab(twoTab)
    }

    override fun onStart() {
        super.onStart()
        adapterFollowers.setOnItemClickListener(this)
    }

    private fun followersListeners() {
            model.getFollowers().observe(viewLifecycleOwner, {
                when (it.status) {
                    Status.SUCCESS -> {
                        adapterFollowers.getUsers(it.data!!)
                        binding.recyclerView.adapter = adapterFollowers
                    }
                    Status.ERROR -> {

                    }
                    Status.LOADING -> TODO()
                }
            })
    }

    private fun followingListeners() {
            model.getFollowing().observe(viewLifecycleOwner, {
                when (it.status) {
                    Status.SUCCESS -> {
                        adapterFollowers.getUsers(it.data!!)
                        binding.recyclerView.adapter = adapterFollowers
                    }
                    Status.ERROR -> {

                    }
                    Status.LOADING -> TODO()
                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onItemClick(t: Any, p: Any?) {
        val action = FollowersFragmentDirections.actionFollowersFragmentToSellerFragment(t as String)
        findNavController().navigate(action)
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        val value = tab?.text.toString()
        if (value == "Followers"){
            followersListeners()
        }else{
            followingListeners()
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {

    }
}


