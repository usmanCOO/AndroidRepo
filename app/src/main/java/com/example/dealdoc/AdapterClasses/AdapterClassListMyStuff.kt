package com.example.dealdoc.AdapterClasses

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.medpicc.dealdoc.ModelClassForStuffMeetingLink
import com.medpicc.dealdoc.R

class AdapterClassListMyStuff(private val dealList: ArrayList<ModelClassForStuffMeetingLink>) :
    RecyclerView.Adapter<AdapterClassListMyStuff.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.mystuff_recycle_view, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = dealList[position]
        var url = ItemsViewModel.StuffMeetingLink.toString()
        holder.tvDealDate.text = ItemsViewModel.StuffMeetingDate
        holder.tvDealTime.text = ItemsViewModel.StuffMeetingTime
        holder.tvDealMeetingLink.text = ItemsViewModel.StuffMeetingLink
        holder.tvDealMeetingLink.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            holder.itemView.context.startActivity(browserIntent)
        }
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return dealList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val tvDealDate: TextView = itemView.findViewById(R.id.MeetingDatetextView)
        val tvDealTime: TextView = itemView.findViewById(R.id.MeetingTimeTextView)
        val tvDealMeetingLink: TextView = itemView.findViewById(R.id.MeetingLinkTextView)
    }
}