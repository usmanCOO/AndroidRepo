package com.example.dealdoc.AdapterClasses

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.cardview.widget.CardView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.dealdoc.Models.dealStatus
import com.example.dealdoc.RetrofitInstance
import com.example.dealdoc.Utils.changeDateFormat
import com.example.dealdoc.fragment_questions
import com.example.dealdoc.fragments.fragmentQuestionCategoriesListInDeals
import com.example.dealdoc.fragments.fragmentTabActive
import com.example.dealdoc.fragments.fragment_tab_lost
import com.example.dealdoc.fragments.fragment_tab_lost.Companion.adapter
import com.example.dealdoc.fragments.fragment_tab_lost.Companion.data
import com.example.dealdoc.fragments.fragment_tab_won
import com.medpicc.dealdoc.ModelClassForDeal
import com.medpicc.dealdoc.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class AdapterLostDeals(private val dealList: ArrayList<ModelClassForDeal>) :
    RecyclerView.Adapter<AdapterLostDeals.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.deals_recycle_view, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = dealList[position]
        var deal_id = ItemsViewModel.id
        val Backendcolor = ItemsViewModel.color
        val ismenu = ItemsViewModel.isDraft

        holder.tvDealName.text = ItemsViewModel.name
        val formattedAmount = NumberFormat.getNumberInstance().format(ItemsViewModel.price.toDouble())
        holder.tvDealPrice.text = "$$formattedAmount"
        holder.tvDealDate.text = changeDateFormat(ItemsViewModel.date)
        holder.tvDealClosedDate.text = changeDateFormat(ItemsViewModel.closedDate)
        if (Backendcolor.equals("null")) {
        } else {
            if (!ismenu) {
                holder.IVMenu.visibility = View.VISIBLE
            }
            val color = Color.parseColor(Backendcolor)
            val backgroundTint = ColorStateList.valueOf(
                Color.argb(
                    100,
                    Color.red(color),
                    Color.green(color),
                    Color.blue(color)
                )
            )
            holder.ActiveDealsRecycleView.backgroundTintList = backgroundTint
        }
        holder.ActiveDealsRecycleView.setOnClickListener {
            val bundle = Bundle().apply {
                putString("deal_id", deal_id.toString())
                putString("deal_name", ItemsViewModel.name.toString())
                putString("deal_price", ItemsViewModel.price.toString())
                putString("deal_status", ItemsViewModel.status.toString())
                putString("deal_color", ItemsViewModel.color.toString())
                putString("deal_closeDate", changeDateFormat(ItemsViewModel.closedDate))
            }
            val appCompatActivity = it.context as AppCompatActivity
            loadFragment(fragmentQuestionCategoriesListInDeals(), bundle, appCompatActivity)
        }
        holder.IVMenu.setOnClickListener {
            val popupMenu = PopupMenu(holder.itemView.context, holder.itemView)
            popupMenu.gravity = Gravity.END
            popupMenu.menuInflater.inflate(R.menu.dropdown_menu, popupMenu.menu)
            val menuItem = popupMenu.menu.findItem(R.id.action_lost)
            menuItem.isVisible = false
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_win -> {
                        // Handle click on

                        UpdateDealStatus(deal_id.toInt(),"Won",holder.adapterPosition, holder.itemView.context)
                        true
                    }
                    R.id.action_active -> {
                        // Handle click on
                        UpdateDealStatus(deal_id.toInt(),"Active",holder.adapterPosition ,holder.itemView.context)
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return dealList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val tvDealName: TextView = itemView.findViewById(R.id.dealNameRV)
        val IVMenu: ImageView = itemView.findViewById(R.id.dotedMenu)
        val tvDealPrice: TextView = itemView.findViewById(R.id.DealPriceRV)
        val tvDealDate: TextView = itemView.findViewById(R.id.DealDateRV)
        val tvDealClosedDate: TextView = itemView.findViewById(R.id.DealClosedDateRV)
        val ActiveDealsRecycleView: CardView = itemView.findViewById(R.id.ActiveDealsRecycleView)
    }

    private fun loadFragment(fragment: Fragment, id: Bundle, context: Context) {
        val appCompatActivity = context as AppCompatActivity
        fragment.arguments = id
        val transaction = appCompatActivity.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.commit()
    }
    private fun UpdateDealStatus(dealId: Int,dealStatus:String,position: Int ,context: Context) {
        RetrofitInstance.apiInterface.UpdateDealStatus(dealId, dealStatus, fragmentTabActive.token.toString())
            .enqueue(object : Callback<dealStatus?> {
                override fun onResponse(
                    call: Call<dealStatus?>,
                    response: Response<dealStatus?>
                ) {
                    if (response.isSuccessful) {
                        deleteItem(position)
                        Toast.makeText(context, "Deal status updated successfully", Toast.LENGTH_SHORT).show()
                        Log.v("Update_response", "" + response.body().toString())
                    }
                }

                override fun onFailure(call: Call<dealStatus?>, t: Throwable) {
//                    Toast.makeText(context, "" + t.message, Toast.LENGTH_SHORT).show()
                }
            })
    }
    fun deleteItem(position: Int) {
        dealList.removeAt(position)
        adapter.notifyItemRemoved(position)
//        adapter.notifyItemRangeChanged(position, adapter.itemCount)
    }
}