package com.maricoolsapps.e_commerce.ui.product_ui.chat

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.data.adapters.ChatMessagesAdapter
import com.maricoolsapps.e_commerce.data.model.MessageType
import com.maricoolsapps.e_commerce.data.model.Messages
import com.maricoolsapps.e_commerce.databinding.ChatFragmentBinding
import com.maricoolsapps.e_commerce.ui.user_authentication_ui.MainActivity
import com.maricoolsapps.e_commerce.utils.displaySnack
import com.maricoolsapps.e_commerce.utils.setResourceCenterCrop
import com.maricoolsapps.e_commerce.utils.toTimeAgo
import com.maricoolsapps.e_commerce.utils.toggleVisibility
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ChatFragment : Fragment(R.layout.chat_fragment) {

    private var _binding: ChatFragmentBinding? = null
    private val binding get() = _binding!!
    private val model: ChatViewModel by viewModels()
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private val args: ChatFragmentArgs by navArgs()
    lateinit var userno: String

    @Inject
    lateinit var adapter: ChatMessagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val imageUri = result.data?.data.toString()
                    val message =
                        Messages(imageUri, Date().time, model.userID, false, MessageType.IMG)
                    model.sendMessage(args.chatChannel, message)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = ChatFragmentBinding.bind(view)
        toolBarInit()
        model.fillViews(args.chatChannel)
        val bot = (activity as MainActivity).findViewById<BottomNavigationView>(R.id.bottom_nav)
        bot.toggleVisibility(false)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = adapter
        observeLiveData()
        clickListeners()

         activity?.onBackPressedDispatcher?.addCallback(
             viewLifecycleOwner,
             object : OnBackPressedCallback(true) {
                 override fun handleOnBackPressed() {
                    findNavController().popBackStack(R.id.chatListFragment, true)
                    // findNavController().popBackStack(R.id.chatFragment, true)
                 }
             })
    }

    private fun toolBarInit() {
        (activity as MainActivity).setSupportActionBar(binding.toolbar)
        val actionBar = (activity as MainActivity).supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun clickListeners() {
        binding.sendMessage.setOnClickListener {
            val messageText = binding.editText.text.toString().trim()
            if (messageText.isEmpty()) {
                return@setOnClickListener
            } else {
                val message =
                    Messages(messageText, Date().time, model.userID, false, MessageType.TEXT)
                model.sendMessage(args.chatChannel, message)
                binding.editText.text?.clear()
            }
        }
        binding.call.setOnClickListener {
            if (::userno.isInitialized && userno.isNotEmpty()) {
                startActivity(model.callChat(userno))
            } else {
                binding.status.displaySnack("Error calling user")
            }
        }

        binding.openGallery.setOnClickListener {
            resultLauncher.launch(model.goToGallery())
        }
    }

    private fun observeLiveData() {
        model.chat.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.image.setResourceCenterCrop(it.image!!)
                binding.name.text = it.name
                userno = it.phoneNumber
            }
        }

        model.status.observe(viewLifecycleOwner) {
            if (it != null) {
                if (it.online) {
                    binding.status.text = "Online"
                } else {
                    binding.status.text = it.lastSeen.toTimeAgo()
                }
            }
        }

        model.messages.observe(viewLifecycleOwner) {
            if (it != null) {
                adapter.getMessages(it)
                binding.recyclerView.scrollToPosition(it.size - 1)
            }
        }
        model.defaultRepo.resultError.observe(viewLifecycleOwner) {
            binding.recyclerView.displaySnack(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

