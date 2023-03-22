package com.maricoolsapps.e_commerce.ui.product_ui.report

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.data.model.Report
import com.maricoolsapps.e_commerce.databinding.FragmentReportBinding
import com.maricoolsapps.e_commerce.ui.user_authentication_ui.MainActivity
import com.maricoolsapps.e_commerce.utils.displaySnack
import com.maricoolsapps.e_commerce.utils.setResource
import com.maricoolsapps.e_commerce.utils.showToast
import com.maricoolsapps.e_commerce.utils.toggleVisibility
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReportFragment : Fragment(R.layout.fragment_report) {

    private var _binding: FragmentReportBinding? = null
    private val args: ReportFragmentArgs by navArgs()
    private val model: ReportViewModel by viewModels()

    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentReportBinding.bind(view)
        fillView()
        buttonClickListener()
        toolbarInit()
        observeLiveData()
    }

    private fun toolbarInit() {
        (activity as MainActivity).apply {
            toolbar.title = "Report"
            toolbar.setBackgroundColor(resources.getColor(R.color.red, null))
        }
    }

    private fun observeLiveData() {
        model.result.observe(viewLifecycleOwner) {
            if (it != null) {
                activity?.showToast(it)
            }
        }
        model.defaultRepo.dataLoading.observe(viewLifecycleOwner) {
            (activity as MainActivity).progressBar.toggleVisibility(it)

        }
        model.defaultRepo.resultError.observe(viewLifecycleOwner) {
            (activity as MainActivity).progressBar.displaySnack(it)
        }
    }

    private fun fillView() {
        binding.apply {
            productDetail.text = "${args.product.brand} ${args.product.type}"
            carImage.setResource(args.product.photos[0])
        }
    }

    private fun buttonClickListener() {

        binding.report.setOnClickListener {
            val desc = binding.issue.text.toString().trim()
            val shortDesc = binding.spinner.selectedItem as String
            if (desc.isEmpty() || desc.length < 20 || binding.spinner.selectedItemPosition == 0) {
                (activity as MainActivity).progressBar.displaySnack("Error in your entries, your description must be greater than 20 letters")
                return@setOnClickListener
            }
            val report = Report(model.userId.toString(), shortDesc, desc)
            model.report(report, args.product)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}