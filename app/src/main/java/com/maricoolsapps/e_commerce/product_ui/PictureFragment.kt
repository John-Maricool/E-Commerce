package com.maricoolsapps.e_commerce.product_ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.databinding.FragmentPictureBinding
import dagger.hilt.android.AndroidEntryPoint
import android.os.Environment

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import androidx.core.content.ContextCompat

import androidx.core.content.ContextCompat.getSystemService
import java.io.File
import java.lang.Exception
import java.text.SimpleDateFormat
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
        Log.d("akamd", args.pictures.toString())
        binding.image.setFactory {
            ImageView(activity)
        }
        Glide.with(requireActivity())
            .load(args.pictures[position])
            .into(binding.image.currentView as ImageView)

    }

    override fun onStart() {
        super.onStart()
        binding.next.setOnClickListener {
            if (position != args.pictures.size -1){
                position++
                Glide.with(requireActivity())
                    .load(args.pictures[position])
                    .into(binding.image.currentView as ImageView)
            }else{
                Toast.makeText(activity, "Last Image", Toast.LENGTH_SHORT).show()
            }
        }

        binding.prev.setOnClickListener {
            if (position > 0){
                position--
                Glide.with(requireActivity())
                    .load(args.pictures[position])
                    .into(binding.image.currentView as ImageView)
            }else{
                Toast.makeText(activity, "First Image", Toast.LENGTH_SHORT).show()
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