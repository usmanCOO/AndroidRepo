package com.example.dealdoc.AdapterClasses

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dealdoc.Utils.ImagesUrl
import com.example.dealdoc.Utils.baseUrl
import com.medpicc.dealdoc.ModelClassForCoaching
import com.medpicc.dealdoc.R

class AdapterCoachingVideoList(val dealList: ArrayList<ModelClassForCoaching>) :
    RecyclerView.Adapter<AdapterCoachingVideoList.ViewHolder>() {
    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.coaching_recycle_view, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = dealList[position]
        val url = ImagesUrl+ItemsViewModel.image
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(ItemsViewModel.CoachingVideoUrl))
        holder.tvDealDate.text = ItemsViewModel.CoachingVideoName
        Glide.with(holder.itemView.context)
            .load(url)
            .into(holder.CoachingVideoIV)
        holder.GotoCoachingVideoLink.setOnClickListener {
            holder.itemView.context.startActivity(browserIntent)
        }
    }
    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return dealList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val tvDealDate: TextView = itemView.findViewById(R.id.CoachingVideoName)
        val CoachingVideoIV: ImageView = itemView.findViewById(R.id.CoachingVideoIV)
        val GotoCoachingVideoLink: LinearLayout = itemView.findViewById(R.id.DealsRecycleView)
    }
}
