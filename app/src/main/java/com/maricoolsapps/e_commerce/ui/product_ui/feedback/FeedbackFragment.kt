package com.maricoolsapps.e_commerce.ui.product_ui.feedback

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.data.model.Feedback
import com.maricoolsapps.e_commerce.databinding.FragmentFeedbactBinding
import com.maricoolsapps.e_commerce.utils.displaySnack
import com.maricoolsapps.e_commerce.utils.showToast
import com.maricoolsapps.e_commerce.utils.toggleVisibility
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class FeedbackFragment : Fragment(R.layout.fragment_feedbact) {

    private var _binding: FragmentFeedbactBinding? = null
    private val binding: FragmentFeedbactBinding get() = _binding!!
    private val model: FeedBackViewModel by viewModels()
    private val args: FeedbackFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFeedbactBinding.bind(view)
        toolbarInit()
        buttonClickListener()
        observeLiveData()
    }

    private fun observeLiveData() {
        model.result.observe(viewLifecycleOwner){
            if (it != null){
                activity?.showToast(it)
                activity?.onBackPressed()
            }
        }
        model.defaultRepo.resultError.observe(viewLifecycleOwner){
            binding.progressBar.displaySnack(it)
        }
        model.defaultRepo.dataLoading.observe(viewLifecycleOwner){
            binding.progressBar.toggleVisibility(it)
        }
    }

    private fun buttonClickListener() {
        binding.send.setOnClickListener {
            val desc = binding.feedback.text.toString().trim()
            val exp = binding.radioGroup.checkedRadioButtonId
            val emoji = activity?.findViewById<RadioButton>(exp)?.text.toString()
            val shortDesc = binding.spinner.selectedItem as String
            if (desc.isEmpty() || desc.length < 20
                || binding.spinner.selectedItemPosition == 0){
                binding.feedback.displaySnack("Your Entries is not complete")
                return@setOnClickListener
            }
            val feedback = Feedback(args.productId, emoji, desc, shortDesc, model.userId.toString(), Calendar.getInstance().timeInMillis)
            Log.d("sak", feedback.toString())
            model.addToDb(args.brand, feedback)
        }
    }

    private fun toolbarInit() {
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        binding.toolbar.title = resources.getString(R.string.write_feedback)
        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}