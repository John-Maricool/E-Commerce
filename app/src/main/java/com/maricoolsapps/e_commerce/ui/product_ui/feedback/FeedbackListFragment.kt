package com.maricoolsapps.e_commerce.ui.product_ui.feedback

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.data.adapters.FeedbackListAdapter
import com.maricoolsapps.e_commerce.databinding.FragmentFeedbackListBinding
import com.maricoolsapps.e_commerce.ui.user_authentication_ui.MainActivity
import com.maricoolsapps.e_commerce.utils.displaySnack
import com.maricoolsapps.e_commerce.utils.toggleVisibility
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FeedbackListFragment : Fragment(R.layout.fragment_feedback_list) {

    private var _binding: FragmentFeedbackListBinding? = null
    private val binding get() = _binding!!
    private val model: FeedbackListViewModel by viewModels()
    private val args: FeedbackListFragmentArgs by navArgs()

    @Inject
    lateinit var feedbackAdapter: FeedbackListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFeedbackListBinding.bind(view)
        toolbarInit()
        binding.recyclerView.setHasFixedSize(false)
        binding.recyclerView.adapter = feedbackAdapter
        model.getFeedbackWithUser(args.brand, args.id)
        observeLiveData()
    }

    private fun observeLiveData() {
        model.done.observe(viewLifecycleOwner) {
            if (it != null) {
                feedbackAdapter.getFeedbacks(it)
                val happy = it.filter {
                    it.feedback.emoji == "Happy"
                }
                val sad = it.filter {
                    it.feedback.emoji == "Sad"
                }
                val neutral = it.filter {
                    it.feedback.emoji == "Neutral"
                }
                binding.positiveValue.text = happy.size.toString()
                binding.negativeValue.text = sad.size.toString()
                binding.neutralValue.text = neutral.size.toString()
            }
        }
        model.defaultRepo.dataLoading.observe(viewLifecycleOwner) {
            (activity as MainActivity).progressBar.toggleVisibility(it)
        }
        model.defaultRepo.resultError.observe(viewLifecycleOwner) {
            (activity as MainActivity).progressBar.displaySnack(it)
        }
        binding.leaveFeedback.setOnClickListener {
            val action =
                FeedbackListFragmentDirections.actionFeedbackListFragmentToFeedbackFragment(
                    args.id,
                    args.brand
                )
            findNavController().navigate(action)
        }
    }

    private fun toolbarInit() {
        binding.toolbar.title = "Feedback for ${args.brand} car"
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}