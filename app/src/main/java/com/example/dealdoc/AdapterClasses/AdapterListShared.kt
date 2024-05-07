package com.example.dealdoc.AdapterClasses

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.dealdoc.Utils.changeDateFormat
import com.example.dealdoc.fragments.fragmentQuestionCategoriesListInDeals
import com.example.dealdoc.fragments.fragment_shared_by_me.Companion.SharedByMedata
import com.example.dealdoc.fragments.fragment_shared_by_me.Companion.SharedWithMeadapter
import com.example.dealdoc.fragments.fragment_shared_with_me.Companion.adapter
import com.example.dealdoc.fragments.fragment_shared_with_me.Companion.data
import com.medpicc.dealdoc.ModelClassForSharedWith
import com.medpicc.dealdoc.R
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class AdapterListShared(private val dealList: ArrayList<ModelClassForSharedWith>) :
    RecyclerView.Adapter<AdapterListShared.ViewHolder>() {

    private var mListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }


    interface OnItemClickListener {
        fun onMessengerClick(position: Int, dealId: Int, dealDescription: String, dealCreator: String, dealCreatorProfile: String)
    }

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.shared_with_recycle_view, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = dealList[position]
        var deal_id = ItemsViewModel.id
        var dealDescription = ItemsViewModel.DealDescription
        var dealCreator = ItemsViewModel.DealCreator
        var unReadMessege = ItemsViewModel.UnreadMessages
        var dealSharedStatus = ItemsViewModel.sharedDealStatus
        var dealCreatorProfile = ItemsViewModel.CreatorProfile
        if(unReadMessege > 0){
            holder.UnReadCommentRV.visibility = View.VISIBLE
            holder.UnReadMessagesTV.text = unReadMessege.toString()
        }else{
            holder.UnReadCommentRV.visibility = View.INVISIBLE
        }
        Log.v("data",dealDescription)
        if (dealSharedStatus.equals("sharedBy")){
            holder.tvDealSharedBy.visibility = View.VISIBLE
            holder.tvDealSharedBy1.visibility = View.VISIBLE
            holder.tvDealSharedWith.visibility =View.INVISIBLE
            holder.tvDealSharedWith1.visibility =View.INVISIBLE
        }else{
            holder.tvDealSharedWith.visibility =View.VISIBLE
            holder.tvDealSharedWith1.visibility =View.VISIBLE
            holder.tvDealSharedBy.visibility = View.INVISIBLE
            holder.tvDealSharedBy1.visibility = View.INVISIBLE
        }
        holder.tvDealName.text = ItemsViewModel.SharedName
        val formattedAmount = NumberFormat.getNumberInstance().format(ItemsViewModel.investmentSize.toDouble())
        holder.tvDealPrice.text = "$$formattedAmount"
        holder.tvDealSharedBy.text = ItemsViewModel.DealCreator
        holder.tvDealSharedWith.text = ItemsViewModel.DealShared
        holder.tvDealUpdated.text = changeDateFormat(ItemsViewModel.DealUpdateDate)
        holder.tvDealClosedDate.text = changeDateFormat(ItemsViewModel.DealClosedDate)
        val color = Color.parseColor(ItemsViewModel.DealColor.toString())
        val backgroundTint = ColorStateList.valueOf(Color.argb(100, Color.red(color), Color.green(color), Color.blue(color)))
        holder.SharedCardView.backgroundTintList = backgroundTint
        holder.SharedCardView.setOnClickListener {
            val bundle = Bundle().apply {
                putString("deal_id", deal_id.toString())
                putString("deal_name", ItemsViewModel.SharedName.toString())
                putString("deal_price", ItemsViewModel.investmentSize.toString())
                putString("deal_status", "shared")
                putString("deal_color", ItemsViewModel.DealColor.toString())
                putString("deal_closeDate", changeDateFormat(ItemsViewModel.DealClosedDate.toString()))
                putString("deal_updated", changeDateFormat(ItemsViewModel.DealUpdateDate.toString()))
                putString("deal_creator", ItemsViewModel.DealCreator.toString())
                putString("deal_shared", ItemsViewModel.DealShared.toString())
                putString("deal_description", dealDescription.toString())
            }
            val appCompatActivity = it.context as AppCompatActivity
            loadFragment(fragmentQuestionCategoriesListInDeals(), bundle, appCompatActivity)
        }
        holder.IvMessager.setOnClickListener {
            val position =  position
            if (position != RecyclerView.NO_POSITION) {
                mListener?.onMessengerClick(position, deal_id, dealDescription, dealCreator, dealCreatorProfile)
            }
        }
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return dealList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val tvDealName: TextView = itemView.findViewById(R.id.SharedWithNametV)
        val tvDealPrice: TextView = itemView.findViewById(R.id.SharedWithPricetV)
        val tvDealSharedBy: TextView = itemView.findViewById(R.id.SharedBytV)
        val tvDealSharedBy1: TextView = itemView.findViewById(R.id.SharedBytV1)
        val tvDealSharedWith: TextView = itemView.findViewById(R.id.SharedWithMetV)
        val tvDealSharedWith1: TextView = itemView.findViewById(R.id.SharedWithMetV1)
        val tvDealUpdated: TextView = itemView.findViewById(R.id.SharedUpDatetV)
        val tvDealClosedDate: TextView = itemView.findViewById(R.id.SharedClosedDatetV)
        val UnReadMessagesTV: TextView = itemView.findViewById(R.id.UnReadMessagesTV)
        val IvMessager: ImageView = itemView.findViewById(R.id.MessengerIV)
        val SharedCardView: CardView = itemView.findViewById(R.id.SharedCardView)
        val UnReadCommentRV: CardView = itemView.findViewById(R.id.UnReadCommentRV)
    }
    private fun loadFragment(fragment: Fragment, id: Bundle, context: Context) {
        val appCompatActivity = context as AppCompatActivity
        fragment.arguments = id
        val transaction = appCompatActivity.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.commit()
    }

    fun deleteItem(position: Int, check: String) {
        Log.v("class",check)
        if(check.equals("with_me")) {
            data.removeAt(position)
            adapter.notifyItemRemoved(position)
        }else{
            SharedByMedata.removeAt(position)
            SharedWithMeadapter.notifyItemRemoved(position)
        }
    }
}