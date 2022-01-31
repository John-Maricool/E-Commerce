package com.maricoolsapps.e_commerce.product_ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
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
    private val args: FollowersFragmentArgs by navArgs()

    @Inject
    lateinit var adapterFollowers: FollowersListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFollowersBinding.bind(view)
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
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
        if (args.id == null) {
            followersListeners(model.user)
        } else {
            followersListeners(args.id!!)
        }
        binding.tabs.addOnTabSelectedListener(this)
        adapterFollowers.setOnItemClickListener(this)
    }

    private fun followersListeners(id: String) {
        binding.textError.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
        model.getFollowers(id).observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.progressBar.visibility = View.GONE
                    Log.d("dff", it.data.toString())
                    Log.d("dff", it.data?.size.toString())
                    adapterFollowers.getUsers(it.data!!)
                    binding.recyclerView.adapter = adapterFollowers
                }
                Status.ERROR -> {
                    binding.textError.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                    binding.textError.text = it.message
                }
                Status.LOADING -> TODO()
            }
        })
    }

    private fun followingListeners(id: String) {
        binding.textError.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
        model.getFollowing(id).observe(viewLifecycleOwner, {
            when (it.status) {
                Status.SUCCESS -> {
                    Log.d("dff", it.data.toString())
                    Log.d("dff", it.data?.size.toString())
                    binding.progressBar.visibility = View.GONE
                    adapterFollowers.getUsers(it.data!!)
                    binding.recyclerView.adapter = adapterFollowers
                }
                Status.ERROR -> {
                    binding.textError.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                    binding.textError.text = it.message
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
        val action =
            FollowersFragmentDirections.actionFollowersFragmentToSellerFragment(t as String)
        findNavController().navigate(action)
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        val value = tab?.text.toString()
        if (args.id == null) {
            if (value == "Followers") {
                followersListeners(model.user)
            } else {
                followingListeners(model.user)
            }
        } else {
            if (value == "Followers") {
                followersListeners(args.id!!)
            } else {
                followingListeners(args.id!!)
            }
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {

    }

    override fun onTabReselected(tab: TabLayout.Tab?) {

    }
}


