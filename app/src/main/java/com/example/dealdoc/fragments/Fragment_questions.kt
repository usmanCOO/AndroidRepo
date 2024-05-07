package com.example.dealdoc

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.dealdoc.AdapterClasses.ViewPagerAdapter
import com.example.dealdoc.AdapterClasses.adapterClassForQuestionsList
import com.example.dealdoc.AdapterClasses.adapterClassForQuestionsList.Companion.adapter_QuestionsList
import com.example.dealdoc.fragments.*
import com.medpicc.dealdoc.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class fragment_questions : Fragment() {

    companion object {
        lateinit var PagerViewer: ViewPager
        lateinit var DealId: String
        lateinit var token: String
        lateinit var QuestionSize: String
        lateinit var deal__Data: Deal_Data
        lateinit var NextBtn: Button
        lateinit var previousBtn: Button
        val dataDraft = ArrayList<ModelClassForDraftDeal>()
        lateinit var TotalQuestion: TextView
    }

    lateinit var ImageView: ImageView
    lateinit var CategoryName: TextView
    lateinit var globalView: View
    lateinit var saveDealBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val sharedPreferences: SharedPreferences =
            (this.activity?.getSharedPreferences("prefs", 0) ?: "") as SharedPreferences
        token = "Bearer " + sharedPreferences.getString("token", "").toString()
        var view = inflater.inflate(R.layout.fragment_questions, container, false)
        view.setOnClickListener {
        }
        globalView = view
        init()
        PagerViewer.beginFakeDrag();
        val args = this.arguments
        val Category_Name = args?.get("CategoryName")
        QuestionSize = args?.get("QuestionLenght").toString()
        DealId = args?.get("deal_id").toString()
        Log.v("Dealid", DealId)
        CategoryName.setText(Category_Name.toString())
        PagerViewer.offscreenPageLimit = 10
        PagerViewer.adapter = adapter_QuestionsList
        actionListeners()
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                val bundle = Bundle().apply {
                    putString("token", token)
                    putString("Deal_id", DealId.toString())
                }

                val appCompatActivity = context as AppCompatActivity
                loadFragment(fragment_QuestionsList(), bundle, appCompatActivity)
            }
        })
    }
    private fun init() {
        PagerViewer = globalView.findViewById(R.id.deal_Questions_Category_PV)
        ImageView = globalView.findViewById(R.id.IVBackBtn)
        CategoryName = globalView.findViewById(R.id.DealQuestionCategoryName)
        TotalQuestion = globalView.findViewById(R.id.DealQuestionCounter)
        saveDealBtn = globalView.findViewById(R.id.SaveDealbtn)
        NextBtn = globalView.findViewById(R.id.nextbtn)
        previousBtn = globalView.findViewById(R.id.previousbtn)
    }

    private fun actionListeners() {
        ImageView.setOnClickListener {
            requireActivity()?.onBackPressed()
        }
        saveDealBtn.setOnClickListener {
            saveDraftDeal()
//            val transaction = parentFragmentManager.beginTransaction()
//            transaction.replace(R.id.tab_Layout, fragmentTabActive())
//            transaction.commit()
        }
    }

    private fun loadFragment(fragment: Fragment, id: Bundle, context: Context) {
        val appCompatActivity = context as AppCompatActivity
        fragment.arguments = id
        val transaction = appCompatActivity.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.commit()
    }

    private fun saveDraftDeal() {
        Log.v("test_",""+deal__Data.data.toString())
        RetrofitInstance.apiInterface.draftDeal(deal__Data, token)
            .enqueue(object : Callback<draftResponse?> {
                override fun onResponse(call: Call<draftResponse?>, response: Response<draftResponse?>) {
                    if (response.isSuccessful){
                        Log.v("apiResponse", "" + response.body())
                        dataDraft.clear()
                        Toast.makeText(context,"Deal added",Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Log.v("error", "" + response.message().toString())
                    }
                }
                override fun onFailure(call: Call<draftResponse?>, t: Throwable) {
                    Toast.makeText(
                        context,
                        "${t.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

}