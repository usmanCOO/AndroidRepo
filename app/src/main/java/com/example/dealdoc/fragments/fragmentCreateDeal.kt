package com.example.dealdoc.fragments

import android.app.Activity
import android.app.DatePickerDialog
import android.content.*
import android.content.ContentValues.TAG
import android.icu.util.Calendar
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import com.example.dealdoc.Models.ModelCLassForCreateDeal
import com.example.dealdoc.Models.ModelClassForGetAllDeal
import com.example.dealdoc.Models.checkFirstDeal
import com.example.dealdoc.Models.getsubscription
import com.example.dealdoc.NetworkUtils
import com.example.dealdoc.NetworkUtils.isNetworkConnected
import com.example.dealdoc.NetworkUtils.showSnackbar
import com.example.dealdoc.RetrofitInstance
import com.example.dealdoc.Utils.getUserSubscription
import com.medpicc.dealdoc.R
import com.example.dealdoc.fragment_subscription
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.*

class fragmentCreateDeal : Fragment(), DatePickerDialog.OnDateSetListener {
    private companion object {
        lateinit var globalView: View
        lateinit var create_deal_name: EditText
        lateinit var create_deal_price: EditText
        lateinit var create_deal_CloseDate: TextView
        lateinit var create_deal_Btn: Button
        private var progressBar: ProgressBar? = null
    }

    var token = ""
    var isStatus = true
    private var isConnected = false
    private val calendar: Calendar = Calendar.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_create_deal, container, false)
        view.setOnClickListener {
            val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            // Hide the keyboard
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
        globalView = view
        try {
            isConnected = context?.let { isNetworkConnected(it) } == true
            val sharedPreferences: SharedPreferences =
                (this.activity?.getSharedPreferences("prefs", 0) ?: "") as SharedPreferences
            token = "Bearer " + sharedPreferences.getString("token", "").toString()
            Log.v(TAG, "Google_id" + sharedPreferences.getString("token", ""));
            init()
            actionListeners()
        } catch (e: Exception) {
            Toast.makeText(context, "" + e.message, Toast.LENGTH_SHORT).show()
        }
        return view
    }

    private fun init() {
        create_deal_name = globalView.findViewById(R.id.editDealName)
        create_deal_price = globalView.findViewById(R.id.editTextDealValue)
        create_deal_Btn = globalView.findViewById(R.id.CreateDealbtn)
        create_deal_CloseDate = globalView.findViewById(R.id.editTextDealCloseValue)
        progressBar = globalView.findViewById(R.id.progress_bar_create_deal)
    }
    private fun getData() {
        try {
            RetrofitInstance.apiInterface.GetFirstDeals(token)
                .enqueue(object : Callback<checkFirstDeal?> {
                    override fun onResponse(
                        call: Call<checkFirstDeal?>,
                        response: Response<checkFirstDeal?>
                    ) {
                        if (response.isSuccessful) {
                            isStatus = response.body()?.success == true
                            getTextData()
                        } else {
                            Log.v("error", "error")
                        }
                    }

                    override fun onFailure(call: Call<checkFirstDeal?>, t: Throwable) {
                        if (t.message == "Unable to resolve host \"api.davidweisssales.com\": No address associated with hostname") {
                            showSnackbar("Server issue try Again Later", activity)
                        } else {
                            showSnackbar("Check Internet & Refresh page", activity)
                        }
//                    Toast.makeText(context, "" + t.message, Toast.LENGTH_SHORT).show()
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun actionListeners() {
        create_deal_price.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Do nothing
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length!! > 3) {
                    val amount = s.toString().replace(",", "")
                    val formattedAmount = NumberFormat.getNumberInstance().format(amount.toDouble())
                    create_deal_price.removeTextChangedListener(this)
                    create_deal_price.setText(formattedAmount)
                    create_deal_price.setSelection(formattedAmount.length)
                    create_deal_price.addTextChangedListener(this)
                }
            }
        })
        create_deal_CloseDate.setOnClickListener(View.OnClickListener {
            val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            // Hide the keyboard
            inputMethodManager.hideSoftInputFromWindow(globalView.windowToken, 0)
            val datePickerDialog = context?.let { it1 ->
                DatePickerDialog(
                    it1,
                    this,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
            }
            datePickerDialog?.show()
        })
        create_deal_Btn.setOnClickListener {
            create_deal_Btn.isClickable = false
            progressBar!!.visibility = View.VISIBLE
            if (isConnected) {
                if (TextUtils.isEmpty(create_deal_name.text.toString())) {
                    create_deal_Btn.isClickable = true
                    showSnackbar("Add Deal Name", activity)
                } else if (TextUtils.isEmpty(create_deal_price.text.toString())) {
                    create_deal_Btn.isClickable = true
                    showSnackbar("Add Investment Amount", activity)
                } else if (TextUtils.isEmpty(create_deal_CloseDate.text.toString())) {
                    create_deal_Btn.isClickable = true
                    showSnackbar("Add Close Date", activity)
                } else {
                    getData()
                }
            } else {
                try {
                    showSnackbar("Check Internet", activity)
                    val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
                    val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
                        override fun onReceive(context: Context?, intent: Intent?) {
                            if (isAdded && context != null) { // check if fragment is added to an activity
                                if (isNetworkConnected(context)) {
                                    if (TextUtils.isEmpty(create_deal_name.text.toString())) {
                                        create_deal_Btn.isClickable = true
                                        showSnackbar("Add Deal Name", activity)
                                    } else if (TextUtils.isEmpty(create_deal_price.text.toString())) {
                                        create_deal_Btn.isClickable = true
                                        showSnackbar("Add Investment Amount", activity)
                                    } else if (TextUtils.isEmpty(create_deal_CloseDate.text.toString())) {
                                        create_deal_Btn.isClickable = true
                                        showSnackbar("Add Close Date", activity)
                                    } else {
                                        getData()
                                    }
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
            progressBar!!.visibility = View.GONE
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        updateLabel()
    }

    private fun updateLabel() {
        val myFormat = "MM/dd/yyyy" // change date format as you want
        val sdf = java.text.SimpleDateFormat(myFormat, Locale.US)
        create_deal_CloseDate.text = sdf.format(calendar.time)
    }

    private fun getTextData() {

            var dlname = create_deal_name.text.toString()
            val amount = create_deal_price.text.toString().replace(",", "")
            var dlprice = Integer.parseInt(amount)
            var dlCloseDate = create_deal_CloseDate.text.toString()
            if (!isStatus) {
                RetrofitInstance.apiInterface.Getsubscription(getUserSubscription, token)
                    .enqueue(object : Callback<getsubscription?> {
                        override fun onResponse(
                            call: Call<getsubscription?>,
                            response: Response<getsubscription?>
                        ) {
                            if (response.isSuccessful) {
//                                if (response.body()?.message == "Subscription record not found") {
//                                    loadFragment(fragment_subscription())
//                                }
                                if (response.body()?.success == false) {
                                    loadFragment(fragment_subscription())
                                }
                                else {
                                    RetrofitInstance.apiInterface.CreateDeal(
                                        dlname,
                                        dlprice,
                                        dlCloseDate,
                                        "Active",
                                        token
                                    )
                                        .enqueue(object : Callback<ModelCLassForCreateDeal?> {
                                            override fun onResponse(
                                                call: Call<ModelCLassForCreateDeal?>,
                                                response: Response<ModelCLassForCreateDeal?>
                                            ) {
                                                if (response.isSuccessful) {
                                                    Log.v("DealData", response.body().toString())
                                                    loadFragment(HomeFragment())
                                                } else {
                                                    showSnackbar("Try Again", activity)
                                                }
                                            }

                                            override fun onFailure(
                                                call: Call<ModelCLassForCreateDeal?>,
                                                t: Throwable
                                            ) {
                                                Toast.makeText(
                                                    context,
                                                    "" + t.message,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        })
                                }
                            }else{
                                showSnackbar("Internal Server Error", activity)
                            }
                        }

                        override fun onFailure(call: Call<getsubscription?>, t: Throwable) {

                        }
                    })
            } else {
                RetrofitInstance.apiInterface.CreateDeal(
                    dlname,
                    dlprice,
                    dlCloseDate,
                    "Active",
                    token
                )
                    .enqueue(object : Callback<ModelCLassForCreateDeal?> {
                        override fun onResponse(
                            call: Call<ModelCLassForCreateDeal?>,
                            response: Response<ModelCLassForCreateDeal?>
                        ) {
                            if (response.isSuccessful) {
                                Log.v("DealData", response.body().toString())
                                loadFragment(HomeFragment())
                            } else {
                                showSnackbar("Try Again", activity)
                            }
                        }

                        override fun onFailure(call: Call<ModelCLassForCreateDeal?>, t: Throwable) {
                            Toast.makeText(context, "" + t.message, Toast.LENGTH_SHORT).show()
                        }
                    })
            }

    }
    private fun loadFragment(fragment: Fragment) {
        val appCompatActivity = context as AppCompatActivity
        val transaction = appCompatActivity.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.commit()
    }
}
