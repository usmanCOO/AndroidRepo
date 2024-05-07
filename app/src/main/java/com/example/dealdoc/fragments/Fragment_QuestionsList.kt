package com.example.dealdoc.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dealdoc.AdapterClasses.adapterClassForQuestionsList
import com.example.dealdoc.RetrofitInstance
import com.example.dealdoc.fragment_questions
import com.medpicc.dealdoc.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class fragment_QuestionsList : Fragment() {
    companion object {
        private lateinit var globalView: View
        private var urlForDeals = ""
        private var progressBar: ProgressBar? = null
        private lateinit var recyclerview: RecyclerView
        private lateinit var token: String
        private lateinit var dealId: String
        lateinit var adapter: adapterClassForQuestionsList
    }
    lateinit var Image_View: ImageView
    val data = ArrayList<ModelClassForQuestionNames>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment__questions_list, container, false)
        view.setOnClickListener {
        }
        globalView = view
        val args = this.arguments
        dealId = args?.get("Deal_id") as String
        token = args?.get("token") as String
        urlForDeals = "api/app/deals/${dealId.toString()}/responsev2"
//        urlForDeals = "api/app/deals/578/responsev2"
        recyclerview = view.findViewById(R.id.deal_Questions_RV)
        Image_View = view.findViewById(R.id.IV_BackBtn)
        Image_View.setOnClickListener {
            requireActivity()?.onBackPressed()
        }
        recyclerview.layoutManager = LinearLayoutManager(context)
        progressBar = globalView.findViewById(R.id.progress_bar_question_list)
        progressBar!!.visibility = View.VISIBLE
        getAllDealData()
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                loadFragment(fragmentCreateDeal())
            }
        })
    }
    private fun loadFragment(fragment: Fragment) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.commit()
    }
    private fun getAllDealData() {
        RetrofitInstance.apiInterface.GetAllDeal(urlForDeals, token.toString())
            .enqueue(object : Callback<questionModel?> {
                override fun onResponse(
                    call: Call<questionModel?>,
                    response: Response<questionModel?>
                ) {
                    var size_of_QuestionName =
                        Integer.parseInt(response.body()?.data?.size.toString())
                    if (size_of_QuestionName != null) {
                        for (i in 0 until size_of_QuestionName) {
                            response.body()?.data?.get(i)?.order?.let {
                                response.body()?.data?.get(i)?.name?.let { it1 ->
                                    response.body()?.data?.get(i)?.Questions.let { it2 ->
                                        ModelClassForQuestionNames(
                                            it.toString(), it1, it2, dealId.toInt()!!,""
                                        )
                                    }
                                }
                            }?.let { data.add(it) }
                        }
                    }
                    // This will pass the ArrayList to our Adapter
                    if (recyclerview.adapter == null) {
                        adapter = adapterClassForQuestionsList(data)
                        recyclerview.adapter = adapter
                        progressBar!!.visibility = View.GONE
                    }else{

                    }
                }

                override fun onFailure(call: Call<questionModel?>, t: Throwable) {
                    Toast.makeText(context, "" + t.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

}
