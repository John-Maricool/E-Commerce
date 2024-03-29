package com.maricoolsapps.e_commerce.ui.product_ui.chat_list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.data.adapters.ChatListAdapter
import com.maricoolsapps.e_commerce.data.interfaces.OnItemClickListener
import com.maricoolsapps.e_commerce.data.model.ChatChannel
import com.maricoolsapps.e_commerce.databinding.ChatListFragmentBinding
import com.maricoolsapps.e_commerce.ui.user_authentication_ui.MainActivity
import com.maricoolsapps.e_commerce.utils.displaySnack
import com.maricoolsapps.e_commerce.utils.toggleVisibility
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChatListFragment : Fragment(R.layout.chat_list_fragment), OnItemClickListener<ChatChannel> {

    private var _binding: ChatListFragmentBinding? = null
    private val binding get() = _binding!!
    private val model: ChatListViewModel by viewModels()

    @Inject
    lateinit var adapter: ChatListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val action = ChatListFragmentDirections.actionChatListFragmentToMainFragment()
                    findNavController().navigate(action)
                }
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = ChatListFragmentBinding.bind(view)
        toolbarInit()
        observeLiveData()
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        adapter.setOnItemClickListener(this)
    }

    private fun observeLiveData() {
        model.done.observe(viewLifecycleOwner){
            if (it != null && it.isNotEmpty()){
                adapter.getNewChatList(it)
            }
        }
        model.defaultRepo.dataLoading.observe(viewLifecycleOwner){
            (activity as MainActivity).progressBar.toggleVisibility(it)
        }
        model.defaultRepo.resultError.observe(viewLifecycleOwner){
            binding.textError.toggleVisibility(true)
            binding.textError.displaySnack(it)
        }

        model.click.observe(viewLifecycleOwner){
            if (it != null) {
                val action = ChatListFragmentDirections.actionChatListFragmentToChatFragment(it)
                findNavController().navigate(action)
            }
        }
    }

    private fun toolbarInit() {
        (activity as MainActivity).toolbar.title = "Messages"
        (activity as MainActivity).toolbar.setBackgroundColor(resources.getColor(R.color.grey, null))
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onItemClick(t: Any, p: Any?) {
        model.getChatChannel(t as String)
    }
}