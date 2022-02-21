package com.maricoolsapps.e_commerce.ui.product_ui.picture

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ImageView
import androidx.navigation.fragment.navArgs
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.databinding.FragmentPictureBinding
import com.maricoolsapps.e_commerce.utils.setResource
import com.maricoolsapps.e_commerce.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

import java.util.*


@AndroidEntryPoint
class PictureFragment : Fragment(R.layout.fragment_picture) {

    private var _binding: FragmentPictureBinding? = null
    private val binding get() = _binding!!

    private var position = 0
    private val args: PictureFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPictureBinding.bind(view)
        binding.image.setFactory {
            ImageView(activity)
        }
        (binding.image.currentView as ImageView).setResource(args.pictures[position])
    }

    override fun onStart() {
        super.onStart()
        binding.next.setOnClickListener {
            if (position != args.pictures.size -1){
                position++
                (binding.image.currentView as ImageView).setResource(args.pictures[position])
            }else{
                activity?.showToast("Last Image")
            }
        }

        binding.prev.setOnClickListener {
            if (position > 0){
                position--
                (binding.image.currentView as ImageView).setResource(args.pictures[position])
            }else{
                activity?.showToast("First Image")
            }
        }

        binding.download.setOnClickListener {
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}