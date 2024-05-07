package com.example.dealdoc.fragments

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.*
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.res.ColorStateList
import android.graphics.Color
import android.icu.util.Calendar
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.IBinder
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.dealdoc.AdapterClasses.adapterClassForQuestionDraftList
import com.example.dealdoc.AdapterClasses.adapterClassForQuestionDraftList.Companion.array
import com.example.dealdoc.Models.ModelClassForSharedDeal
import com.example.dealdoc.Models.ModelClassForSubmitDeal
import com.example.dealdoc.Models.dealUpdateData
import com.example.dealdoc.RetrofitInstance
import com.example.dealdoc.Utils
import com.example.dealdoc.Utils.changeDateFormat
import com.example.dealdoc.Utils.updateDealData
import com.example.dealdoc.activities.HomePage
import com.example.dealdoc.fragments.BottomSheetFragment.Companion.globalView
import com.medpicc.dealdoc.ModelClassForQuestionNames
import com.medpicc.dealdoc.R
import com.medpicc.dealdoc.questionModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class fragmentQuestionCategoriesListInDeals : Fragment() {

    private lateinit var recyclerview: RecyclerView
    private lateinit var DealNamebyDraft: TextView
    private var progressBar: ProgressBar? = null
    private lateinit var DealInfo: ImageView
    private lateinit var DealComments: ImageView
    private lateinit var backBtn: ImageView
    private lateinit var DealNameCV: CardView
    private lateinit var adapterDraft: adapterClassForQuestionDraftList
    private val data = ArrayList<ModelClassForQuestionNames>()
    private lateinit var globalView: View
    private var token = ""
    private var urlForDeals = ""
    private var dealId = ""
    private var dealStatus = ""


    companion object {
        lateinit var shareBtn: Button
        lateinit var materialCoachingBtn: Button
        var dealName = ""
        var dealPrice = ""
        var dealCloseDate = ""
        var dealUpdatedDate = ""
        var dealSharedByDate = ""
        var dealSharedWithDate = ""
        var dealColor = ""
        var dealDescription = ""
        var dealNameUpdated = ""
        var dealPriceUpdated = ""
        var dealCloseDateUpdated = ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(
            R.layout.fragment_question_categories_list_in_deals,
            container,
            false
        )
        view.setOnClickListener {
        }
        try {
            globalView = view
            init()
            val args = this.arguments
            dealStatus = args?.get("deal_status") as String
            if(dealStatus == "shared") {
//                Toast.makeText(context,"Shared Color "+ dealColor,Toast.LENGTH_SHORT).show()
                dealId = args?.get("deal_id") as String
                dealName = args?.get("deal_name") as String
                dealPrice = args?.get("deal_price") as String
                dealColor = args?.get("deal_color") as String
                dealCloseDate = args?.get("deal_closeDate") as String
                dealUpdatedDate = args?.get("deal_updated") as String
                dealSharedByDate = args?.get("deal_creator") as String
                dealSharedWithDate = args?.get("deal_shared") as String
                dealDescription = args?.get("deal_description") as String
            }else{
                dealId = args?.get("deal_id") as String
                dealName = args?.get("deal_name") as String
                dealPrice = args?.get("deal_price") as String
                dealColor = args?.get("deal_color") as String
                dealCloseDate = args?.get("deal_closeDate") as String
            }
//            Log.v("data___",""+dealId+ dealName+dealStatus+ dealPrice+ dealColor+ dealCloseDate)

            if (dealColor.equals("null")) {
                Log.v("color",dealColor)
            } else {
                val color = Color.parseColor(dealColor.toString())
                val backgroundTint = ColorStateList.valueOf(
                    Color.argb(
                        100,
                        Color.red(color),
                        Color.green(color),
                        Color.blue(color)
                    )
                )
                DealNameCV.backgroundTintList = backgroundTint
            }
            if (dealNameUpdated == ""){
                DealNamebyDraft.text = dealName.toString()
            }else {
                DealNamebyDraft.text = dealNameUpdated.toString()
            }
            urlForDeals = "api/app/deals/${dealId.toString()}/responsev2"
            val sharedPreferences: SharedPreferences =
                (this.activity?.getSharedPreferences("prefs", 0) ?: "") as SharedPreferences
            token = "Bearer " + sharedPreferences.getString("token", "").toString()
            actionListeners()
            val swipeRefreshLayout: SwipeRefreshLayout = view.findViewById(R.id.RefreshrecyclerQuestionCategoriesDeals)
            swipeRefreshLayout.setColorSchemeColors(Color.RED)
            swipeRefreshLayout.setProgressBackgroundColorSchemeColor(Color.TRANSPARENT)
            swipeRefreshLayout.setOnRefreshListener {
                getAllDealData()
                swipeRefreshLayout.isRefreshing = false
            }
                getAllDealData()
                recyclerview.adapter = adapterDraft
                try {
                progressBar!!.visibility = View.GONE
                }catch (e: ExceptionInInitializerError){
                    e.printStackTrace()
                }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if (dealStatus.equals("shared")) {
                    val transaction = parentFragmentManager.beginTransaction()
                    transaction.replace(R.id.container, fragmentSharedDeals())
                    transaction.commit()
                }
                else{
                    val intent = Intent(activity, HomePage::class.java)
                    startActivity(intent)
                    activity?.finish()
                }
            }
        })
    }
    private fun init() {
        recyclerview = globalView.findViewById(R.id.deal_Questions_Category_RV)
        DealNamebyDraft = globalView.findViewById(R.id.DealNameByDraft)
        recyclerview.layoutManager = LinearLayoutManager(context)
        progressBar = globalView.findViewById(R.id.progress_bar_question_Category_list)
        DealInfo = globalView.findViewById(R.id.DealInfo)
        DealComments = globalView.findViewById(R.id.MessengerIV)
        backBtn = globalView.findViewById(R.id.IVBackBtnCategory)
        shareBtn = globalView.findViewById(R.id.SharedDealbtn)
        materialCoachingBtn = globalView.findViewById(R.id.MaterialDealbtn)
        DealNameCV = globalView.findViewById(R.id.DealNameCV)
        progressBar!!.visibility = View.VISIBLE
    }

    private fun actionListeners() {
        backBtn.setOnClickListener {
           requireActivity().onBackPressed()
            dealColorUpdate()
        }
        shareBtn.setOnClickListener {
            SharedSubmitDealData()
        }
        materialCoachingBtn.setOnClickListener {
            CoachingMaterialData()
        }
        if (dealStatus.equals("Active")) {
            DealInfo.setOnClickListener {
                val customDialog = context?.let { it1 -> CustomDialog(it1) }
                customDialog?.setOnPositiveButtonClick { firstText, secondText, ThirdText ->
                    // Handle positive button click with the two text fields
                    val amount = secondText.replace(",", "")
                    UpdateDealData(firstText, amount.toInt(), ThirdText)
                }
                if(dealNameUpdated == "" || dealPriceUpdated == "" || dealCloseDateUpdated == ""){
                    val formattedAmount = NumberFormat.getNumberInstance().format(dealPrice.toDouble())
                    customDialog?.setText(dealName.toString(), "$formattedAmount".toString(), dealCloseDate.toString())
                }else{
                    val formattedAmount = NumberFormat.getNumberInstance().format(dealPriceUpdated.toDouble())
                    val closedDateFormat = changeDateFormat(dealCloseDateUpdated.toString())
                    customDialog?.setText(dealNameUpdated.toString(), "$formattedAmount".toString(), closedDateFormat)
                }
                customDialog?.show()
            }
        } else if (dealStatus.equals("shared")) {
            DealComments.visibility = View.VISIBLE
            shareBtn.visibility = View.GONE
            materialCoachingBtn.visibility = View.GONE
            DealInfo.setOnClickListener {
//                Log.v("sharedDealData", "$dealName $dealPrice $dealUpdatedDate $dealCloseDate $dealSharedByDate $dealSharedWithDate")
                val customDialog = context?.let { it1 -> CustomSharedDealData(it1) }
                customDialog?.setText(dealName.toString(), dealPrice.toString(), dealUpdatedDate, dealCloseDate.toString(),
                     dealSharedByDate, dealSharedWithDate)
                customDialog?.show()
            }
            DealComments.setOnClickListener {
//                Toast.makeText(context,"Check", Toast.LENGTH_SHORT).show()
                val bundle = Bundle().apply {
                    putString("deal_id", dealId.toString())
                    putString("deal_description", dealDescription.toString())
                }
                val appCompatActivity = context as AppCompatActivity
                loadFragment(BottomSheetFragment(),bundle,appCompatActivity)
            }
        } else {

        }
    }

    private fun SharedSubmitDealData() {

        RetrofitInstance.apiInterface.submitDeal(
            dealId.toInt(),
            adapterClassForQuestionDraftList.hexaColor.toString(),
            token.toString()
        )
            .enqueue(object : Callback<ModelClassForSubmitDeal?> {
                override fun onResponse(
                    call: Call<ModelClassForSubmitDeal?>,
                    response: Response<ModelClassForSubmitDeal?>
                ) {
                    if (response.isSuccessful) {
                        val customDialog = context?.let { it1 -> CustomDialogSharedDeal(it1) }
                        customDialog?.setOnPositiveButtonClick { firstText, secondText ->
                            // Handle positive button click with the two text fields
                            if (firstText.isNullOrEmpty()){
                                Toast.makeText(context, "Please Enter Email", Toast.LENGTH_SHORT).show()
                            }else if(secondText.isNullOrEmpty()){
                                Toast.makeText(context, "Please Enter Comment", Toast.LENGTH_SHORT).show()
                            }else {
                                SharedDealData(firstText, secondText)
                                customDialog.dismiss()
                            }
                        }
                        customDialog?.show()
                    }
                    Log.v("BodyResponse", "" + response.body().toString())
                }

                override fun onFailure(call: Call<ModelClassForSubmitDeal?>, t: Throwable) {
//                    Toast.makeText(context, "" + t.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun CoachingMaterialData() {
        RetrofitInstance.apiInterface.submitDeal(
            dealId.toInt(),
            adapterClassForQuestionDraftList.hexaColor.toString(),
            token.toString()
        )
            .enqueue(object : Callback<ModelClassForSubmitDeal?> {
                override fun onResponse(
                    call: Call<ModelClassForSubmitDeal?>,
                    response: Response<ModelClassForSubmitDeal?>
                ) {
                    if (response.isSuccessful) {
                        val bundle = Bundle().apply {
                            putString("deal_id", dealId.toString())
                        }
                        val appCompatActivity = context as AppCompatActivity
                        loadFragment(fragmentCoachingMaterial(), bundle, appCompatActivity)
                    }
                    Log.v("BodyResponse", "" + response.body().toString())
                }

                override fun onFailure(call: Call<ModelClassForSubmitDeal?>, t: Throwable) {
                    Toast.makeText(context, "" + t.message, Toast.LENGTH_SHORT).show()
                }
            })
    }
    private fun dealColorUpdate() {
        if (adapterClassForQuestionDraftList.isFinish == adapterClassForQuestionDraftList.sizeOfList) {
            RetrofitInstance.apiInterface.submitDeal(
                dealId.toInt(),
                adapterClassForQuestionDraftList.hexaColor.toString(),
                token.toString()
            )
                .enqueue(object : Callback<ModelClassForSubmitDeal?> {
                    override fun onResponse(
                        call: Call<ModelClassForSubmitDeal?>,
                        response: Response<ModelClassForSubmitDeal?>
                    ) {
                        if (response.isSuccessful) {

                        }
                    }

                    override fun onFailure(call: Call<ModelClassForSubmitDeal?>, t: Throwable) {
                        Toast.makeText(context, "" + t.message, Toast.LENGTH_SHORT).show()
                    }
                })
        } else {

        }

    }

    private fun getAllDealData() {
        RetrofitInstance.apiInterface.GetAllDeal(urlForDeals, token.toString())
            .enqueue(object : Callback<questionModel?> {
                override fun onResponse(
                    call: Call<questionModel?>,
                    response: Response<questionModel?>
                ) {
//                    Log.v("response", "" + response.body().toString())
                    if(response.isSuccessful){
                        data.clear()
                        array.clear()
                        var size_of_QuestionName =
                            Integer.parseInt(response.body()?.data?.size.toString())
                        for (i in 0 until size_of_QuestionName) {
                            response.body()?.data?.get(i)?.order?.let {
                                response.body()?.data?.get(i)?.name?.let { it1 ->
                                    response.body()?.data?.get(i)?.Questions.let { it2 ->
                                        ModelClassForQuestionNames(
                                            it.toString(), it1, it2, dealId.toInt()!!,
                                            dealStatus
                                        )
                                    }
                                }
                            }?.let { data.add(it) }
                        }

                            // This will pass the ArrayList to our Adapter
                            adapterDraft = adapterClassForQuestionDraftList(data)
                            recyclerview.adapter = adapterDraft
                            adapterClassForQuestionDraftList.redCount = 0
                            adapterClassForQuestionDraftList.orangeCount = 0
                            adapterClassForQuestionDraftList.yellowCount = 0
                            adapterClassForQuestionDraftList.lightGreenCount = 0
                            adapterClassForQuestionDraftList.greenCount = 0
                            adapterClassForQuestionDraftList.isFinish = 0
                            adapterClassForQuestionDraftList.hexaColor = ""
                            adapterClassForQuestionDraftList.sizeOfList = 0
                            progressBar!!.visibility = View.GONE
                    }
                }

                override fun onFailure(call: Call<questionModel?>, t: Throwable) {
                    Toast.makeText(context, "Check Internet & Refresh page", Toast.LENGTH_SHORT).show()
//                    Toast.makeText(context, "" + t.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun UpdateDealData(dealName: String, deal_investment: Int, dealClosedDate: String) {
//        Log.v("DealUpdateddd",""+deal_investment.toString())
        var urlForUpdateDeals = "$updateDealData/$dealId"
        RetrofitInstance.apiInterface.UpdateDeal(
            urlForUpdateDeals,
            dealName,
            deal_investment,
            dealClosedDate,
            token.toString()
        )
            .enqueue(object : Callback<dealUpdateData?> {
                override fun onResponse(
                    call: Call<dealUpdateData?>,
                    response: Response<dealUpdateData?>
                ) {
                    if(response.isSuccessful) {
                        DealNamebyDraft.text = response.body()?.data?.deal_name
                        dealNameUpdated = response.body()?.data?.deal_name.toString()
                        dealPriceUpdated = response.body()?.data?.investment_size.toString()
                        var closeddealdate =
                            response.body()?.data?.closed_date.toString()
                        val stringArrayDate: List<String> = closeddealdate!!.split("T")
                        dealCloseDateUpdated = stringArrayDate[0]
                        Toast.makeText(context,"Update Successfully",Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(context,"Try Again",Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<dealUpdateData?>, t: Throwable) {
                    Toast.makeText(context, "" + t.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun SharedDealData(email: String, message: String) {
        Log.v("dealId", "" + dealId + email + message)
        RetrofitInstance.apiInterface.sharedDeal(dealId.toInt(), email, message, token.toString())
            .enqueue(object : Callback<ModelClassForSharedDeal?> {
                override fun onResponse(
                    call: Call<ModelClassForSharedDeal?>,
                    response: Response<ModelClassForSharedDeal?>
                ) {
                    if (response.isSuccessful) {
                        Log.v("SharedDataaa",response.body().toString())
                        if (response.body()?.status == true) {
                            Toast.makeText(context, "Shared Deal Successfully", Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(context, "User not Exist", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Log.v("error", "error")
                    }
                }

                override fun onFailure(call: Call<ModelClassForSharedDeal?>, t: Throwable) {
                    Toast.makeText(context, "" + t.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun loadFragment(fragment: Fragment, id: Bundle, context: Context) {
        val appCompatActivity = context as AppCompatActivity
        fragment.arguments = id
        val transaction = appCompatActivity.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.commit()
    }
}

//class CustomDialog(context: Context) : Dialog(context) , DatePickerDialog.OnDateSetListener {
//
//    // Views
//    var firstEditText: EditText? = null
//    var secondEditText: EditText? = null
//    var editTextDealCloseValue: TextView? = null
//    var FirstText: String? = null
//    var SecondText: String? = null
//    var ThirdText: String? = null
//
//    // Callbacks
//    private var onPositiveButtonClick: ((String, String, String) -> Unit)? = null
//    private var onNegativeButtonClick: (() -> Unit)? = null
//    private val calendar: Calendar = Calendar.getInstance()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.custom_dialog_layout)
//        window?.setLayout(
//            WindowManager.LayoutParams.MATCH_PARENT,
//            WindowManager.LayoutParams.WRAP_CONTENT
//        )
//
//        // Initialize views
//        firstEditText = findViewById(R.id.first_edit_text)
//        secondEditText = findViewById(R.id.second_edit_text)
//        editTextDealCloseValue = findViewById(R.id.editTextDealCloseValue_dialog)
//        firstEditText?.setText(FirstText)
//        secondEditText?.setText(SecondText)
//        editTextDealCloseValue?.text = ThirdText
//
//        //set change Listeners
//        secondEditText?.let { editText ->
//            editText.addTextChangedListener(object : TextWatcher {
//                override fun afterTextChanged(s: Editable?) {
//                    // Do nothing
//                }
//
//                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                    // Do nothing
//                }
//
//                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                    if (s?.length!! > 3) {
//                        val amount = s.toString().replace(",", "")
//                        try {
//                            val parsedAmount = amount.toDouble()
//                            val formattedAmount =
//                                NumberFormat.getNumberInstance().format(parsedAmount)
//                            editText.removeTextChangedListener(this)
//                            editText.setText(formattedAmount)
//                            editText.setSelection(formattedAmount.length)
//                            editText.addTextChangedListener(this)
//                        }catch (e: NumberFormatException){
//                            e.printStackTrace()
//                        }
//                    }
//                }
//            })
//        }
//
//        // Set click listeners for buttons
//        findViewById<Button>(R.id.positive_button).setOnClickListener {
//            onPositiveButtonClick?.invoke(
//                firstEditText?.text.toString(),
//                secondEditText?.text.toString(),
//                editTextDealCloseValue?.text.toString()
//            )
//            dismiss()
//        }
//        findViewById<ImageView>(R.id.negative_button).setOnClickListener {
//            onNegativeButtonClick?.invoke()
//            dismiss()
//        }
//        findViewById<TextView>(R.id.editTextDealCloseValue_dialog).setOnClickListener {
//            val inputMethodManager = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
//            if (inputMethodManager.isAcceptingText) {
//                inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
//            } else {
//            }
//            showDatePickerDialog()
////            val datePickerDialog = context?.let { it1 ->
////                DatePickerDialog(
////                    it1,
////                    this,
////                    calendar.get(Calendar.YEAR),
////                    calendar.get(Calendar.MONTH),
////                    calendar.get(Calendar.DAY_OF_MONTH)
////                )
////            }
////            datePickerDialog?.show()
//
//        }
//        findViewById<LinearLayout>(R.id.dealDialogLayout).setOnClickListener {
//            val inputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
//            if (inputMethodManager.isAcceptingText) {
//                inputMethodManager.hideSoftInputFromWindow((currentFocus?.windowToken ?: "") as IBinder?, 0)
//            } else {
//
//            }
//        }
//
//    }
//
//    // Public methods to set callbacks
//    fun setOnPositiveButtonClick(listener: (String, String, String) -> Unit) {
//        onPositiveButtonClick = listener
//    }
//
//    fun setText(text: String, text2: String, text3: String) {
//        FirstText = text
//        SecondText = text2
//        ThirdText = text3
//    }
//
//    fun setOnNegativeButtonClick(listener: () -> Unit) {
//        onNegativeButtonClick = listener
//    }
//
//    private fun showDatePickerDialog() {
//        try {
//            val year = calendar.get(Calendar.YEAR)
//            val month = calendar.get(Calendar.MONTH)
//            val day = calendar.get(Calendar.DAY_OF_MONTH)
//
//            // Parse the date from the TextView to initialize the DatePickerDialog
//            val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
//            val date = editTextDealCloseValue?.text.toString()
//            val dateObj = dateFormat.parse(date)
//            dateObj?.let {
//                calendar.time = it
//            }
//
//            DatePickerDialog(context, this, year, month, day).show()
//        }catch (e: Exception){
//            Log.v("DateErrorMessage",""+e.message)
//        }
//    }
//
//    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
//        calendar.set(Calendar.YEAR, year)
//        calendar.set(Calendar.MONTH, month)
//        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
//        updateDateTextView()
//    }
//
//    private fun updateDateTextView() {
//        val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
//        val selectedDate = dateFormat.format(calendar.time)
//        editTextDealCloseValue?.text = selectedDate
//    }
//
////    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
////        calendar.set(Calendar.YEAR, year)
////        calendar.set(Calendar.MONTH, month)
////        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
////        updateLabel()
////    }
////    private fun updateLabel() {
////        val myFormat = "MM/dd/yyyy" // change date format as you want
////        val sdf = java.text.SimpleDateFormat(myFormat, Locale.US)
////        editTextDealCloseValue?.text = sdf.format(calendar.time)
////    }
//}
class CustomDialog(context: Context) : Dialog(context), DatePickerDialog.OnDateSetListener {

    // Views
    private var firstEditText: EditText? = null
    private var secondEditText: EditText? = null
    private var editTextDealCloseValue: TextView? = null
    private var FirstText: String? = null
    private var SecondText: String? = null
    private var ThirdText: String? = null

    // Callbacks
    private var onPositiveButtonClick: ((String, String, String) -> Unit)? = null
    private var onNegativeButtonClick: (() -> Unit)? = null
    private val calendar: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_dialog_layout)
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        // Initialize views
        firstEditText = findViewById(R.id.first_edit_text)
        secondEditText = findViewById(R.id.second_edit_text)
        editTextDealCloseValue = findViewById(R.id.editTextDealCloseValue_dialog)
        firstEditText?.setText(FirstText)
        secondEditText?.setText(SecondText)
        editTextDealCloseValue?.text = ThirdText

        // Set change Listeners
        secondEditText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Do nothing
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length!! > 3) {
                    val amount = s.toString().replace(",", "")
                    try {
                        val parsedAmount = amount.toDouble()
                        val formattedAmount =
                        NumberFormat.getNumberInstance().format(parsedAmount)
                        secondEditText?.removeTextChangedListener(this)
                        secondEditText?.setText(formattedAmount)
                        secondEditText?.setSelection(formattedAmount.length)
                        secondEditText?.addTextChangedListener(this)
                    } catch (e: NumberFormatException) {
                        e.printStackTrace()
                    }
                }
            }
        })

        // Set click listeners for buttons
        findViewById<Button>(R.id.positive_button).setOnClickListener {
            onPositiveButtonClick?.invoke(
                firstEditText?.text.toString(),
                secondEditText?.text.toString(),
                editTextDealCloseValue?.text.toString()
            )
            dismiss()
        }
        findViewById<ImageView>(R.id.negative_button).setOnClickListener {
            onNegativeButtonClick?.invoke()
            dismiss()
        }
        findViewById<TextView>(R.id.editTextDealCloseValue_dialog).setOnClickListener {
            val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            // Hide the keyboard
            currentFocus?.let { view ->
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            }
            showDatePickerDialog()
        }
        findViewById<LinearLayout>(R.id.dealDialogLayout).setOnClickListener {
            val inputMethodManager =
                context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
            if (inputMethodManager.isAcceptingText) {
                inputMethodManager.hideSoftInputFromWindow(
                    (currentFocus?.windowToken ?: "") as IBinder?,
                    0
                )
            }
        }
    }

    // Public methods to set callbacks
    fun setOnPositiveButtonClick(listener: (String, String, String) -> Unit) {
        onPositiveButtonClick = listener
    }

    fun setText(text: String, text2: String, text3: String) {
        FirstText = text
        SecondText = text2
        ThirdText = text3
    }

    fun setOnNegativeButtonClick(listener: () -> Unit) {
        onNegativeButtonClick = listener
    }

    private fun showDatePickerDialog() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Parse the date from the TextView to initialize the DatePickerDialog
        val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        val date = editTextDealCloseValue?.text.toString()
        val dateObj = dateFormat.parse(date)
        dateObj?.let {
            calendar.time = it
        }

        val datePickerDialog = DatePickerDialog(
            context,
            this,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        updateDateTextView()
    }

    private fun updateDateTextView() {
        val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        val selectedDate = dateFormat.format(calendar.time)
        editTextDealCloseValue?.text = selectedDate
    }
}

class CustomDialogSharedDeal(context: Context) : Dialog(context) {

    // Views
    var firstEditText: EditText? = null
    var secondEditText: EditText? = null

    // Callbacks
    private var onPositiveButtonClick: ((String, String) -> Unit)? = null
    private var onNegativeButtonClick: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_dialogbox_shared_deal)
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        // Initialize views
        firstEditText = findViewById(R.id.email_edit_text)
        secondEditText = findViewById(R.id.message_edit_text)
        // Set click listeners for buttons
        findViewById<Button>(R.id.positive_button).setOnClickListener {
            onPositiveButtonClick?.invoke(
                firstEditText?.text.toString(),
                secondEditText?.text.toString()
            )
        }
        findViewById<ImageView>(R.id.negative_button).setOnClickListener {
            onNegativeButtonClick?.invoke()
            dismiss()
        }
        findViewById<LinearLayout>(R.id.custom_shareBox_layout).setOnClickListener {
            val inputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            if (inputMethodManager.isAcceptingText) {
                inputMethodManager.hideSoftInputFromWindow((currentFocus?.windowToken ?: "") as IBinder?, 0)
            } else {
            }
        }
    }

    // Public methods to set callbacks
    fun setOnPositiveButtonClick(listener: (String, String) -> Unit) {
        onPositiveButtonClick = listener
    }

    fun setOnNegativeButtonClick(listener: () -> Unit) {
        onNegativeButtonClick = listener
    }
}
class CustomSharedDealData(context: Context) : Dialog(context) {

    // Views
    private var firstEditText: TextView? = null
    private var secondEditText: TextView? = null
    private var thirdEditText: TextView? = null
    private var forthEditText: TextView? = null
    private var fifthEditText: TextView? = null
    private var sixEditText: TextView? = null

    //Variables
    var FirstSharedText: String? = null
    var SecondSharedText: String? = null
    var ThirdSharedText: String? = null
    var ForthSharedText: String? = null
    var FifthSharedText: String? = null
    var SixthSharedText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_shared_dialog_layout)
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        // Initialize views
        firstEditText = findViewById(R.id.first_TV)
        secondEditText = findViewById(R.id.second_TV)
        thirdEditText = findViewById(R.id.third_TV)
        forthEditText = findViewById(R.id.forth_TV)
        fifthEditText = findViewById(R.id.fifth_TV)
        sixEditText = findViewById(R.id.sixth_TV)

        findViewById<ImageView>(R.id.negative_Shared_button).setOnClickListener {
//            onNegativeButtonClick?.invoke()
            dismiss()
        }
        //Set The Values
        firstEditText?.text = FirstSharedText
        val formattedAmount = NumberFormat.getNumberInstance().format(SecondSharedText?.toDouble())
        secondEditText?.text = formattedAmount
        thirdEditText?.text = "Updated Date: $ThirdSharedText"
        forthEditText?.text = "Closed Date: $ForthSharedText"
        fifthEditText?.text = "Deal Shared By: $FifthSharedText"
        sixEditText?.text = "Deal Shared With: $SixthSharedText"
    }
    fun setText(text: String, text2: String, text3: String, text4: String, text5: String, text6: String) {
        FirstSharedText = text
        SecondSharedText = text2
        ThirdSharedText = text3
        ForthSharedText = text4
        FifthSharedText = text5
        SixthSharedText = text6
    }

}