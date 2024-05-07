package com.example.dealdoc.fragments

import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.telephony.mbms.MbmsErrors.InitializationErrors
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.ViewPager
import com.example.dealdoc.AdapterClasses.adapterClassForQuestionDraftList
import com.example.dealdoc.AdapterClasses.adapterClassForQuestionDraftList.Companion.adapter_draft_QuestionsList
import com.example.dealdoc.AdapterClasses.adapterClassForQuestionDraftList.Companion.adapter_draft_QuestionsListOff
import com.example.dealdoc.AdapterClasses.adapterClassForQuestionDraftList.Companion.array
import com.example.dealdoc.AdapterClasses.adapterClassForQuestionsList
import com.example.dealdoc.AdapterClasses.adapterClassForQuestionsList.Companion.adapter_QuestionsList
import com.example.dealdoc.Models.dealStatus
import com.example.dealdoc.RetrofitInstance
import com.example.dealdoc.fragment_questions
import com.medpicc.dealdoc.Deal_Data
import com.medpicc.dealdoc.ModelClassForDraftDeal
import com.medpicc.dealdoc.R
import com.medpicc.dealdoc.draftResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class fragment_draft_questions_list : Fragment() {

    companion object {
        lateinit var PagerViewer: ViewPager
        lateinit var DealId: String
        lateinit var DealStatus: String
        lateinit var token: String
        lateinit var QuestionSize: String
//        lateinit var deal_Data: Deal_Data
        var deal_Data: Deal_Data? = null
        val dataDraft = ArrayList<ModelClassForDraftDeal>()
        lateinit var TotalQuestion: TextView
        lateinit var NextBtn: Button
        lateinit var previousBtn: Button
        lateinit var finishBtn: ImageButton
        var counter = 1
    }

    lateinit var DealName: String
    lateinit var DealPrice: String
    lateinit var DealColor: String
    lateinit var DealCloseDate: String
    lateinit var dealUpdatedDate: String
    lateinit var dealSharedByDate: String
    lateinit var dealSharedWithDate: String
    lateinit var dealDescription: String
    private lateinit var ImageView: ImageView
    private lateinit var CategoryName: TextView
    private lateinit var globalView: View
    private lateinit var saveDealBtn: Button
    private lateinit var switchBtn: Switch

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_draft_questions_list, container, false)
        view.setOnClickListener {
        }
        val sharedPreferences: SharedPreferences =
            (this.activity?.getSharedPreferences("prefs", 0) ?: "") as SharedPreferences
        token = "Bearer " + sharedPreferences.getString("token", "").toString()
        globalView = view
        init()
        PagerViewer.beginFakeDrag()
        val args = this.arguments
        val Category_Name = args?.getString("CategoryName")
        DealStatus = args?.getString("deal_status").toString()
        if (DealStatus == "shared") {
            QuestionSize = args?.getString("QuestionLenght").toString()
            DealId = args?.getString("deal_id").toString()
            DealName = args?.getString("deal_Name").toString()
            DealPrice = args?.getString("deal_Price").toString()
            DealColor = args?.getString("deal_Color").toString()
            DealCloseDate = args?.getString("deal_closeDate").toString()
            dealUpdatedDate = args?.getString("deal_updated").toString()
            dealSharedByDate = args?.getString("deal_creator").toString()
            dealSharedWithDate = args?.getString("deal_shared").toString()
            dealDescription = args?.getString("deal_description").toString()
        }else{
            QuestionSize = args?.getString("QuestionLenght").toString()
            DealId = args?.getString("deal_id").toString()
            DealName = args?.getString("deal_Name").toString()
            DealPrice = args?.getString("deal_Price").toString()
            DealColor = args?.getString("deal_Color").toString()
            DealCloseDate = args?.getString("deal_closeDate").toString()
        }
//        Log.v("deal___Data", "Status $DealName $DealPrice $DealColor $QuestionSize")
        CategoryName.text = Category_Name.toString()
        PagerViewer.offscreenPageLimit = 10
        QuestionSize = adapterClassForQuestionDraftList.array.size.toString()
        PagerViewer.adapter = adapter_draft_QuestionsList
        actionListeners()
        return view

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
//                Handler().postDelayed({
                    try {
                        val bundle = Bundle().apply {
                            if (DealStatus == "shared") {
                                putString("deal_id", DealId.toString())
                                putString("deal_name", DealName.toString())
                                putString("deal_price", DealPrice.toString())
                                putString("deal_status", DealStatus.toString())
                                putString("deal_color", DealColor.toString())
                                putString("deal_closeDate", DealCloseDate.toString())
                                putString("deal_updated",dealUpdatedDate)
                                putString("deal_creator",dealSharedByDate)
                                putString("deal_shared",dealSharedWithDate)
                                putString("deal_description",dealDescription)
                            }else{
                                putString("deal_id", DealId.toString())
                                putString("deal_name", DealName.toString())
                                putString("deal_price", DealPrice.toString())
                                putString("deal_status", DealStatus.toString())
                                putString("deal_color", DealColor.toString())
                                putString("deal_closeDate", DealCloseDate.toString())
                            }
                        }
                        val appCompatActivity = context as AppCompatActivity
                        loadFragment(
                            fragmentQuestionCategoriesListInDeals(),
                            bundle,
                            appCompatActivity
                        )
                    }catch (e: NullPointerException){
                        e.printStackTrace()
                    }
//                }, 1000)
            }
        })
    }

    private fun init() {
        PagerViewer = globalView.findViewById(R.id.deal_Questions_Category_PVDraft)
        ImageView = globalView.findViewById(R.id.IVBackBtnDraft)
        CategoryName = globalView.findViewById(R.id.DealQuestionCategoryNameDraft)
        TotalQuestion = globalView.findViewById(R.id.DealQuestionCounterDraft)
        saveDealBtn = globalView.findViewById(R.id.SaveDealbtnDraft)
        NextBtn = globalView.findViewById(R.id.Nextbtn)
        previousBtn = globalView.findViewById(R.id.Previousbtn)
        finishBtn = globalView.findViewById(R.id.Finishbtn)
        switchBtn = globalView.findViewById(R.id.switch1Draft)
    }

    @Suppress("DEPRECATION")
    private fun actionListeners() {
        switchBtn.setOnCheckedChangeListener { _, isChecked ->

            if (isChecked) {
                val thumbTint = ColorStateList.valueOf(Color.GREEN)
                switchBtn.thumbTintList = thumbTint
                if (adapterClassForQuestionDraftList.array1.isNullOrEmpty()){
                    TotalQuestion.text = "0/0"
                    NextBtn.visibility = View.INVISIBLE
                    previousBtn.visibility = View.INVISIBLE
                    PagerViewer.adapter = adapter_draft_QuestionsListOff
                }else {
                    QuestionSize = adapterClassForQuestionDraftList.array1.size.toString()
//                    Log.v("deal___Data", "Status $DealName $DealPrice $DealColor $QuestionSize")
                    PagerViewer.adapter = adapter_draft_QuestionsListOff
                }
                } else {
                val thumbTint = ColorStateList.valueOf(Color.WHITE)
                switchBtn.thumbTintList = thumbTint
                QuestionSize = array.size.toString()
                PagerViewer.adapter = adapter_draft_QuestionsList
            }
        }
        ImageView.setOnClickListener {
            dataDraft.clear()
//            array.clear()
            requireActivity()?.onBackPressed()
        }
        if (DealStatus.equals("shared")){
            saveDealBtn.visibility = View.GONE
        }
        else {
            saveDealBtn.setOnClickListener {
                saveDraftDeal()
//                dataDraft.clear()
//                array.clear()

            }
            finishBtn.setOnClickListener {
                saveDraftDeal()
//                dataDraft.clear()
//                array.clear()

            }
        }
    }

    private fun saveDraftDeal() {
        try {
            if (dataDraft.isNullOrEmpty()) {
//                Toast.makeText(context,"Check null",Toast.LENGTH_SHORT).show()
                dataDraft.clear()
//                array.clear()
                requireActivity()?.onBackPressed()
            } else {
//                Toast.makeText(context,"Check Not null",Toast.LENGTH_SHORT).show()
                try {
                    deal_Data?.let {
                        RetrofitInstance.apiInterface.draftDeal(it, token)
                            .enqueue(object : Callback<draftResponse?> {
                                override fun onResponse(
                                    call: Call<draftResponse?>,
                                    response: Response<draftResponse?>
                                ) {
                                    if (response.isSuccessful) {
                                        Log.v("apiResponse", "" + response.body()?.data)
                                        dataDraft.clear()
//                                        array.clear()
                                        activity?.onBackPressed()

                                    } else {
                                        Toast.makeText(
                                            requireContext(),
                                            "Check Internet",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        Log.v("errorsssss", "" + response.message().toString())
                                    }
                                }

                                override fun onFailure(call: Call<draftResponse?>, t: Throwable) {
                                    if (isAdded) {
                                        //                            Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            })
                    }
                } catch (e: Exception) {
//            activity?.onBackPressed()
                    Log.v("errorrrrr", "" + e.message)
                }
            }
        } catch (e: Exception) {
//            activity?.onBackPressed()
            Log.v("errorrsss", "" + e.message)
        }
    }
    private fun loadFragment(fragment: Fragment, id: Bundle, context: Context) {
        val appCompatActivity = context as AppCompatActivity
        fragment.arguments = id
        val transaction = appCompatActivity.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.commit()
    }

}