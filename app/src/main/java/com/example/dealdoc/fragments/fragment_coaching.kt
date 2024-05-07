package com.example.dealdoc.fragments

import android.content.*
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.dealdoc.AdapterClasses.AdapterCoachingVideoList
import com.example.dealdoc.NetworkUtils
import com.example.dealdoc.NetworkUtils.isNetworkConnected
import com.example.dealdoc.NetworkUtils.showSnackbar
import com.example.dealdoc.RetrofitInstance
import com.medpicc.dealdoc.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class fragment_coaching : Fragment() {
    private var isConnected = false
    private companion object {
        lateinit var globalView: View
        lateinit var recyclerview: RecyclerView
        lateinit var progressbar: ProgressBar
    }

    private var token = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_coaching, container, false)
        try {
        isConnected = context?.let { isNetworkConnected(it) } == true
            val sharedPreferences: SharedPreferences =
                (this.activity?.getSharedPreferences("prefs", 0) ?: "") as SharedPreferences
            token = "Bearer " + sharedPreferences.getString("token", "").toString()
            globalView = view
            recyclerview = view.findViewById(R.id.Coaching_RV)
            progressbar = view.findViewById(R.id.progressBar_coaching)
            recyclerview.layoutManager = LinearLayoutManager(context)
            getData()
            val swipeRefreshLayout: SwipeRefreshLayout = view.findViewById(R.id.RefreshrecyclerCoaching)
            swipeRefreshLayout.setColorSchemeColors(Color.RED)
            swipeRefreshLayout.setProgressBackgroundColorSchemeColor(Color.TRANSPARENT)
            swipeRefreshLayout.setOnRefreshListener {
                getData()
                swipeRefreshLayout.isRefreshing = false
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
        } catch (e: Exception) {
            Toast.makeText(context, "" + e.message, Toast.LENGTH_SHORT).show()
        }
        return view
    }

    private fun getData() {
        try{
        if (context?.let { NetworkUtils.isInternetAvailable(it) } == true) {
            RetrofitInstance.apiInterface.GetVideoDeal(token)
                .enqueue(object : Callback<ModelClassForCoachingVideos?> {
                    override fun onResponse(
                        call: Call<ModelClassForCoachingVideos?>,
                        response: Response<ModelClassForCoachingVideos?>
                    ) {
                        if (response.isSuccessful) {
                            val sizeOfArray = response.body()?.video_data?.size
                            val data = ArrayList<ModelClassForCoaching>()
                            // the image with the count of view
                            if (sizeOfArray != null) {
                                for (i in 0 until sizeOfArray) {
                                    response.body()?.video_data?.get(i)?.name?.let {
                                        response.body()?.video_data?.get(i)?.url?.let { it1 ->
                                            response.body()?.video_data?.get(i)?.thumbnail?.let { it2 ->
                                                ModelClassForCoaching(
                                                    it, it1, it2
                                                )
                                            }
                                        }
                                    }?.let { data.add(it) }
                                }
                            }
                            // This will pass the ArrayList to our Adapter
                            val adapter = AdapterCoachingVideoList(data)
                            progressbar.visibility = View.GONE
                            //Setting the Adapter with the recyclerview
                            recyclerview.adapter = adapter
                        }else{
                            showSnackbar("Internal Server Error", activity)
                        }
                    }
                    override fun onFailure(call: Call<ModelClassForCoachingVideos?>, t: Throwable) {
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
}