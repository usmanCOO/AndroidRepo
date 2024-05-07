package com.example.dealdoc.fragments

import android.content.*
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.dealdoc.AdapterClasses.AdapterListDeals
import com.example.dealdoc.AdapterClasses.AdapterLostDeals
import com.example.dealdoc.AdapterClasses.AdapterWonDeals
import com.example.dealdoc.Models.ModelClassForGetAllDeal
import com.example.dealdoc.NetworkUtils
import com.example.dealdoc.NetworkUtils.showSnackbar
import com.example.dealdoc.RetrofitInstance
import com.example.dealdoc.Utils
import com.example.dealdoc.Utils.deleteUserDeal
import com.example.dealdoc.Utils.getuserDeals
import com.example.dealdoc.fragments.fragment_tab_lost.Companion.data
import com.example.dealdoc.fragments.fragment_tab_lost.Companion.token
import com.medpicc.dealdoc.ModelClassForDeal
import com.medpicc.dealdoc.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class fragment_tab_lost : Fragment() {
    private var isConnected = false
    lateinit var linearLayoutManager:LinearLayoutManager
    lateinit var recyclerview: RecyclerView
    lateinit var progressbar: ProgressBar
    var selectItem: String = "all"
    companion object{
    var token = ""
        val data = ArrayList<ModelClassForDeal>()
        lateinit var adapter: AdapterLostDeals
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tab_lost, container, false)
        view.setOnClickListener {
        }
        isConnected = context?.let { isNetworkConnected(it) } == true
        val sharedPreferences: SharedPreferences =
            (this.activity?.getSharedPreferences("prefs", 0) ?: "") as SharedPreferences
        token = "Bearer " + sharedPreferences.getString("token", "").toString()
        recyclerview = view.findViewById(R.id.LostDeals_RV)
        progressbar = view.findViewById(R.id.progressBar_LostDeals)
        recyclerview.layoutManager = LinearLayoutManager(context)
        val swipeRefreshLayout: SwipeRefreshLayout = view.findViewById(R.id.RefreshrecyclerLostDeals)
        swipeRefreshLayout.setColorSchemeColors(Color.RED)
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(Color.TRANSPARENT)
        swipeRefreshLayout.setOnRefreshListener {
            getData()
            swipeRefreshLayout.isRefreshing = false
        }
        HomeFragment.autocompleteTV.setOnItemClickListener { parent, view, position, id ->
           val selectedValue = parent.getItemAtPosition(position) as String
            // Do something with the selected value
            selectItem = SelectedFilter(selectedValue)
            Log.v("item", "Selected item: $selectItem")
            progressbar.visibility = View.VISIBLE
            getData()
        }
        if (isConnected) {
            getData()
        } else {
            try {
                showSnackbar("Check Internet", activity)
                val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
                val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {
                        if (isAdded && context != null) { // check if fragment is added to an activity
                            if (isNetworkConnected(context)) {
                                getData()
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
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        })
    }
    private fun isNetworkConnected(context: Context): Boolean {
        val connectivityManager =
            requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
    private fun getData() {
        try {
            if (context?.let { NetworkUtils.isInternetAvailable(it) } == true) {
                RetrofitInstance.apiInterface.GetAllDeals(getuserDeals +selectItem, token)
                    .enqueue(object : Callback<ModelClassForGetAllDeal?> {
                        override fun onResponse(
                            call: Call<ModelClassForGetAllDeal?>,
                            response: Response<ModelClassForGetAllDeal?>
                        ) {
//                        Log.v("Deal_data", "" + response.body().toString())
                            data.clear()
                            if (response.isSuccessful) {
                                val sizeOfArray = response.body()?.deal?.size
                                if (sizeOfArray != null) {
                                    for (i in 0 until sizeOfArray) {
                                        var TimeAndDate =
                                            response.body()?.deal?.get(i)?.updatedAt
                                        val stringArray: List<String> = TimeAndDate!!.split("T")
                                        var closeddealdate =
                                            response.body()?.deal?.get(i)?.closed_date
                                        val stringArrayDate: List<String> =
                                            closeddealdate!!.split("T")
                                        var status = response.body()?.deal?.get(i)?.status
                                        if (status == "Lost") {
                                            response.body()?.deal?.get(i)?.is_draft?.let {
                                                ModelClassForDeal(
                                                    (response.body()?.deal?.get(i)?.id).toString(),
                                                    (response.body()?.deal?.get(i)?.deal_name).toString(),
                                                    (response.body()?.deal?.get(i)?.investment_size).toString(),
                                                    stringArray[0],
                                                    (response.body()?.deal?.get(i)?.color).toString(),
                                                    it,
                                                    response.body()?.deal?.get(i)?.status.toString(),
                                                    stringArrayDate[0],
                                                )
                                            }?.let {
                                                data.add(
                                                    it
                                                )
                                            }
                                        } else {

                                        }
                                    }
                                }
                                adapter = AdapterLostDeals(data)
                                progressbar.visibility = View.GONE
                                linearLayoutManager = LinearLayoutManager(context)
                                linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
                                recyclerview.layoutManager = linearLayoutManager
                                recyclerview.adapter = adapter
                                val itemTouchHelper =
                                    ItemTouchHelper(SwipeToDeleteLostDeal(adapter, "#FF0000"))
                                itemTouchHelper.attachToRecyclerView(recyclerview)
                            }else{
                                showSnackbar("Internal Server Error", activity)
                            }
                        }
                        override fun onFailure(call: Call<ModelClassForGetAllDeal?>, t: Throwable) {
                            if (t.message == "Unable to resolve host \"api.davidweisssales.com\": No address associated with hostname") {
                                showSnackbar("Server issue try Again Later", activity)
                            } else {
                                showSnackbar("Check Internet & Refresh page", activity)
                            }
                        }
                    })
            }else{
                showSnackbar("Check Internet & Refresh page", activity)
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun SelectedFilter(selectedFilter: String): String {
        selectItem = when (selectedFilter) {
            "Color" -> {
                "color"
            }
            "Size" -> {
                "investment_size"
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
}
class SwipeToDeleteLostDeal(private val adapter: AdapterLostDeals, private val backgroundColor: String) :
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
            deleteUserDeal +dataa.id,
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
                        adapter.deleteItem(position)
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