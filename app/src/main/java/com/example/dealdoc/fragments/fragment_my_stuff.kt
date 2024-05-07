package com.example.dealdoc.fragments

import android.R.string
import android.content.*
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dealdoc.AdapterClasses.AdapterClassListMyStuff
import com.example.dealdoc.Models.ModelClassForMyStuff
import com.example.dealdoc.NetworkUtils
import com.example.dealdoc.NetworkUtils.isNetworkConnected
import com.example.dealdoc.NetworkUtils.showSnackbar
import com.example.dealdoc.RetrofitInstance
import com.example.dealdoc.Utils.convertTime24To12
import com.example.dealdoc.Utils.getUserSessions
import com.medpicc.dealdoc.ModelClassForStuffMeetingLink
import com.medpicc.dealdoc.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class fragment_my_stuff : Fragment() {

    lateinit var globalView: View
    lateinit var recyclerview: RecyclerView
    lateinit var progressbar: ProgressBar
    private var token = ""
    private var isConnected = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isConnected = context?.let { isNetworkConnected(it) } == true
        val sharedPreferences: SharedPreferences =
            (this.activity?.getSharedPreferences("prefs", 0) ?: "") as SharedPreferences
        token = "Bearer " + sharedPreferences.getString("token", "").toString()
        val view = inflater.inflate(R.layout.fragment_my_stuff, container, false)
        globalView = view
        recyclerview = view.findViewById(R.id.MyStuff_RV)
        progressbar = view.findViewById(R.id.progressBar_myStuff)
        recyclerview.layoutManager = LinearLayoutManager(context)
        getData()
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

    private fun getData() {
        RetrofitInstance.apiInterface.GetMyStuffDeal(
            getUserSessions,
            token
        )
            .enqueue(object : Callback<ModelClassForMyStuff?> {
                override fun onResponse(
                    call: Call<ModelClassForMyStuff?>,
                    response: Response<ModelClassForMyStuff?>
                ) {
                    if (response.isSuccessful) {
                        val sizeOfArray = response.body()?.user_sessions?.size
                        val data = ArrayList<ModelClassForStuffMeetingLink>()
                        // the image with the count of view
                        if (sizeOfArray != null) {
                            for (i in 0 until sizeOfArray) {
                                var TimeAndDate =
                                    response.body()?.user_sessions?.get(i)?.metadata?.start_time.toString()
                                val stringArray: List<String> = TimeAndDate.split("T")
                                var time = convertTime24To12(stringArray.get(1))
                                data.add(
                                    ModelClassForStuffMeetingLink(
                                        response.body()?.user_sessions?.get(i)?.id.toString(),
                                        stringArray.get(0),
                                        time,
                                        response.body()?.user_sessions?.get(i)?.session_url.toString()
                                    )
                                )
                            }
                        }
                        // This will pass the ArrayList to our Adapter
                        val adapter = AdapterClassListMyStuff(data)
                        progressbar.visibility = View.GONE
                        //Setting the Adapter with the recyclerview
                        recyclerview.adapter = adapter
                    }else{
                        showSnackbar("Internal Server Error", activity)
                    }
                }
                override fun onFailure(call: Call<ModelClassForMyStuff?>, t: Throwable) {
                    if (t.message == "Unable to resolve host \"api.davidweisssales.com\": No address associated with hostname") {
                        showSnackbar("Server issue try Again Later", activity)
                    } else {
                        showSnackbar("Check Internet & Refresh page", activity)
                    }
                }
            })
    }
}