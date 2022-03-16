package com.maricoolsapps.e_commerce.data.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.maricoolsapps.e_commerce.R
import com.maricoolsapps.e_commerce.databinding.RecyclerSingleItemBinding
import com.maricoolsapps.e_commerce.data.interfaces.OnItemClickListener
import com.maricoolsapps.e_commerce.data.model.FeedbackWithUser
import com.maricoolsapps.e_commerce.data.model.ProductModel
import com.maricoolsapps.e_commerce.data.repositories.ProductDetailRepo
import com.maricoolsapps.e_commerce.databinding.FeedbackListItemBinding
import com.maricoolsapps.e_commerce.room_db.FavoriteProductEntity
import com.maricoolsapps.e_commerce.utils.setResource
import com.maricoolsapps.e_commerce.utils.setResourceCenterCrop
import com.maricoolsapps.e_commerce.utils.toTimeAgo
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class FeedbackListAdapter
@Inject constructor(@ApplicationContext val context: Context) :
    RecyclerView.Adapter<FeedbackListAdapter.RecyclerViewHolder>() {

    private var feedbacks: List<FeedbackWithUser> = listOf()
    private var limit: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val binding = FeedbackListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RecyclerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val feedback = feedbacks[position]
        holder.binding.apply {
            image.setResourceCenterCrop(feedback.user.image.toString())
            name.text = feedback.user.name
            time.text = feedback.feedback.time.toTimeAgo()
            feedbackDesc.text = feedback.feedback.review
            if (feedback.feedback.emoji == "Happy"){
                feedbackImage.setBackgroundResource(R.drawable.ic_happy)
            }else if(feedback.feedback.emoji == "Sad"){
                feedbackImage.setBackgroundResource(R.drawable.ic_sad)
            }else{
                feedbackImage.setBackgroundResource(R.drawable.ic_neutral)
            }
        }
    }

    fun getFeedbacks(newFeedbacks: List<FeedbackWithUser>, mlimit: Int = newFeedbacks.size) {
        feedbacks = newFeedbacks
        limit = mlimit
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        if (feedbacks.size > limit){
            return limit
        }else{
            return feedbacks.size
        }
    }

    inner class RecyclerViewHolder(var binding: FeedbackListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}