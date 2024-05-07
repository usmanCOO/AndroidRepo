package com.example.dealdoc.fragments

import android.content.*
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.dealdoc.AdapterClasses.AdapterListShared
import com.example.dealdoc.Models.ModelClassForSharedByMe
import com.example.dealdoc.Models.ModelClassForUserProfile
import com.example.dealdoc.NetworkUtils
import com.example.dealdoc.NetworkUtils.isInternetAvailable
import com.example.dealdoc.NetworkUtils.isNetworkConnected
import com.example.dealdoc.NetworkUtils.showSnackbar
import com.example.dealdoc.RetrofitInstance
import com.example.dealdoc.Utils.deleteSharedDeal
import com.example.dealdoc.Utils.getSharedWithMeDeals
import com.example.dealdoc.fragments.fragmentSharedDeals.Companion.spinner
import com.example.dealdoc.fragments.fragment_shared_with_me.Companion.data
import com.example.dealdoc.fragments.fragment_shared_with_me.Companion.token
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.medpicc.dealdoc.ModelClassForSharedWith
import com.medpicc.dealdoc.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class fragment_shared_with_me : Fragment(), AdapterListShared.OnItemClickListener {
    lateinit var recyclerview: RecyclerView
    lateinit var progressbar: ProgressBar
    private var isConnected = false
    var selectItem: String = "all"
    companion object {
        var token = ""
        val data = ArrayList<ModelClassForSharedWith>()
        lateinit var adapter: AdapterListShared
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_shared_with_me, container, false)
        isConnected = context?.let { isNetworkConnected(it) } == true
        view.setOnClickListener {
        }
        val sharedPreferences: SharedPreferences =
            (this.activity?.getSharedPreferences("prefs", 0) ?: "") as SharedPreferences
        token = "Bearer " + sharedPreferences.getString("token", "").toString()
        progressbar = view.findViewById(R.id.progressBar_sharedWithMeDeals)
        recyclerview = view.findViewById(R.id.SharedWithMe_RV)
        recyclerview.layoutManager = LinearLayoutManager(context)
        val swipeRefreshLayout: SwipeRefreshLayout = view.findViewById(R.id.RefreshrecycleSharedWithMe)
        swipeRefreshLayout.setColorSchemeColors(Color.RED)
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(Color.TRANSPARENT)
        swipeRefreshLayout.setOnRefreshListener {
            getSharedwithDealData()
            swipeRefreshLayout.isRefreshing = false
        }
        spinner.setOnItemClickListener { parent, view, position, id ->
            val selectedValue = parent.getItemAtPosition(position) as String
            // Do something with the selected value
            selectItem = SelectedFilter(selectedValue)
            Log.v("item", "Selected item: $selectItem")
            progressbar.visibility = View.VISIBLE
            getSharedwithDealData()
        }
//        Log.v("check", "run$selectItem")
        try {
            if (isConnected) {
                getSharedwithDealData()
            } else {
                try {
                    showSnackbar("Check Internet", activity)
                    val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
                    val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
                        override fun onReceive(context: Context?, intent: Intent?) {
                            if (isAdded && context != null) { // check if fragment is added to an activity
                                if (isNetworkConnected(context)) {
                                    getSharedwithDealData()
                                } else {
                                    showSnackbar("Check Internet", activity)
                                }
                            }
                        }
                    }
                    requireActivity().registerReceiver(broadcastReceiver, filter)
                } catch (e: Exception) {
                    Toast.makeText(context, "${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

        } catch (e: Exception) {
            Log.v("error", "" + e.message)
        }
        return view
    }
    private fun SelectedFilter(selectedFilter: String): String {
        selectItem = when (selectedFilter) {
            "Color" -> {
                "color"
            }
            "Size" -> {
                "size"
            }
            "Deal Name" -> {
                "deal_name"
            }
            "Updated Date" -> {
                "updated_date"
            }
            "Close Date" -> {
                "closed_date"
            }
            else -> {
                "all"
            }
        }
        return selectItem
    }
    private fun getSharedwithDealData() {
        try {
            if (context?.let { isInternetAvailable(it) } == true) {
            var customUrl = getSharedWithMeDeals+selectItem
            RetrofitInstance.apiInterface.GetSharedWithMe(customUrl, token)
                .enqueue(object : Callback<ModelClassForSharedByMe?> {
                    override fun onResponse(
                        call: Call<ModelClassForSharedByMe?>,
                        response: Response<ModelClassForSharedByMe?>
                    ) {
                        if (response.isSuccessful) {
                            progressbar.visibility = View.GONE
                            var size = response.body()?.data?.size
                            if (size != null) {
                                data.clear()
                                for (i in 0 until size) {
                                    var UpdateDate =
                                        response.body()?.data?.get(i)?.Deal?.updatedAt
                                    var ClosedDate =
                                        response.body()?.data?.get(i)?.Deal?.closed_date
                                    val stringArray: List<String> = UpdateDate!!.split("T")
                                    val stringArrayclosedDate: List<String> = ClosedDate!!.split("T")
                                    var creatorname = ""
                                    var sharedName = ""
                                    var creatorprofileUrl = ""
                                    var sharedprofileUrl = ""
//                                    Log.v("dataaa",""+stringArray+"\n"+stringArrayclosedDate)
                                    response.body()?.data?.get(i)?.Deal?.let {
                                        response.body()?.data?.get(i)?.description?.let { it1 ->
                                            if(response.body()?.data?.get(i)?.creator?.fullName == null){
                                                creatorname = "NA"
                                            }else if(response.body()?.data?.get(i)?.shared_user?.fullName == null){
                                                sharedName = "NA"
                                            }else if(response.body()?.data?.get(i)?.creator?.profilePhoto == null){
                                                creatorprofileUrl =  "null"
                                            }else if(response.body()?.data?.get(i)?.shared_user?.profilePhoto == null){
                                                sharedprofileUrl = "null"
                                            }
                                            else{
                                                creatorname =  response.body()?.data?.get(i)?.creator?.fullName!!
                                                sharedName =  response.body()?.data?.get(i)?.shared_user?.fullName!!
                                                creatorprofileUrl =  response.body()?.data?.get(i)?.creator!!.profilePhoto!!
                                                sharedprofileUrl =  response.body()?.data?.get(i)?.shared_user!!.profilePhoto!!
                                            }
                                                    ModelClassForSharedWith(
                                                        it.id,
                                                        it.deal_name,
                                                        it.color,
                                                        it.investment_size,
                                                        it1,
                                                        stringArrayclosedDate[0],
                                                        stringArray[0],
                                                        creatorname,
                                                        sharedName,
                                                        response.body()?.data?.get(i)!!.unread,
                                                        "sharedWith",
                                                        sharedprofileUrl,
                                                        creatorprofileUrl
                                                    )
                                                }
                                            }?.let { data.add(it) }
                                }
                            }
                            // This will pass the ArrayList to our Adapter
                            adapter = AdapterListShared(data)
                            adapter.setOnItemClickListener(this@fragment_shared_with_me)
                            // Setting the Adapter with the recyclerview
                            recyclerview.adapter = adapter
                            val itemTouchHelper = ItemTouchHelper(SwipeToDelete(adapter, "#FF0000"))
                            itemTouchHelper.attachToRecyclerView(recyclerview)
                        }else{
                            showSnackbar("Internal Server Error", activity)
                        }
                        Log.v("Shared_Deal_data", "" + response.body()?.data.toString())
                    }

                    override fun onFailure(call: Call<ModelClassForSharedByMe?>, t: Throwable) {
                        if (t.message == "Unable to resolve host \"api.davidweisssales.com\": No address associated with hostname") {
                            showSnackbar("Server issue try Again Later", activity)
                        } else {
                            showSnackbar("Check Internet & Refresh page", activity)
                        }
//                        Toast.makeText(context, "" + t.message, Toast.LENGTH_SHORT).show()
                    }
                })
            }else{
                showSnackbar("Check Internet & Refresh page", activity)
            }
        }catch (e: Exception){
            Log.v("Error",e.message.toString())
            e.printStackTrace()
        }
    }

    override fun onMessengerClick(position: Int, dealId: Int, dealDescription: String, dealCreatorName: String, dealCreatorProfile: String) {
        Log.v("description",dealId.toString()+dealDescription)
        val bundle = Bundle().apply {
            putString("deal_id", dealId.toString())
            putString("deal_description", dealDescription.toString())
            putString("deal_creator", dealCreatorName.toString())
            putString("deal_creator_profile", dealCreatorProfile.toString())
        }
        val appCompatActivity = context as AppCompatActivity
        loadFragment(BottomSheetFragment(),bundle,appCompatActivity)
    }
    private fun loadFragment(fragment: Fragment, id: Bundle, context: Context) {
        val appCompatActivity = context as AppCompatActivity
        fragment.arguments = id
        val transaction = appCompatActivity.supportFragmentManager.beginTransaction()
        transaction.addToBackStack(null)
        transaction.add(R.id.container, fragment)
        transaction.commit()
    }
}

class SwipeToDelete(private val adapter: AdapterListShared, private val backgroundColor: String) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        // No action when moved
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        val dataa = data[position]
        deleteData(
            deleteSharedDeal + dataa.id,
            token,
            position,
            viewHolder.itemView.context
        )
    }

    private fun deleteData(Url: String, token: String, position: Int, context: Context) {

        try {
            RetrofitInstance.apiInterface.deleteUser(Url, token).enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    // Handle successful response
                    if (response.isSuccessful) {
                        adapter.deleteItem(position, "with_me")
                        Log.v("Success", response.message())
                        Toast.makeText(context, "Deal deleted successfully", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(context, "Try Again", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    // Handle error response
                    Log.v("error", t.message.toString())
                }
            })

        } catch (e: Exception) {
            Log.v("Error", e.message.toString())
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView

        // Draw the background color
        val color = Color.parseColor(backgroundColor)
        val background = ColorDrawable(color)
        background.setBounds(
            itemView.right + dX.toInt(),
            itemView.top,
            itemView.right,
            itemView.bottom
        )
        if(isCurrentlyActive){
            val paint = Paint()
            paint.color = Color.WHITE
            paint.textSize = 40F
            paint.textAlign = Paint.Align.RIGHT
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            val inbox = itemView.context.resources.getString(R.string.sendinbox)
            val textX = itemView.right - paint.measureText(inbox) - 12
            val textY = itemView.top + itemView.height / 2f + paint.textSize / 2f
            background.draw(c)
            c.drawText(inbox, textX.toFloat(), textY, paint)
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}


