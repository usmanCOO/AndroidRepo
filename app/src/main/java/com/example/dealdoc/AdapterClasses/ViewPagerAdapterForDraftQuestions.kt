package com.example.dealdoc.AdapterClasses


import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.viewpager.widget.PagerAdapter
import com.example.dealdoc.fragments.fragment_draft_questions_list
import com.example.dealdoc.fragments.fragment_draft_questions_list.Companion.DealId
import com.example.dealdoc.fragments.fragment_draft_questions_list.Companion.NextBtn
import com.example.dealdoc.fragments.fragment_draft_questions_list.Companion.QuestionSize
import com.example.dealdoc.fragments.fragment_draft_questions_list.Companion.TotalQuestion
import com.example.dealdoc.fragments.fragment_draft_questions_list.Companion.counter
import com.example.dealdoc.fragments.fragment_draft_questions_list.Companion.dataDraft
import com.example.dealdoc.fragments.fragment_draft_questions_list.Companion.deal_Data
import com.example.dealdoc.fragments.fragment_draft_questions_list.Companion.finishBtn
import com.example.dealdoc.fragments.fragment_draft_questions_list.Companion.previousBtn
import com.medpicc.dealdoc.*

class ViewPagerAdapterForDraftQuestions(
    private val mContext: Context,
    private val itemList: ArrayList<ModelClassForStatementsAndResponse>
) : PagerAdapter() {
    private var layoutInflater: LayoutInflater? = null
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = LayoutInflater.from(mContext)
        val view =
            layoutInflater!!.inflate(R.layout.question_list_draft_recycle_view, container, false)
        try {
            val ItemsViewModel = itemList[position]
            val questionId = ItemsViewModel.id
            val deal_status = ItemsViewModel.status
            val tvDealName: TextView = view.findViewById(R.id.DealsQuestionCategoryWise)
            val groupRadBtn: RadioGroup = view.findViewById(R.id.GroupYesNoRBtn)
            val RadBtnYes: RadioButton = view.findViewById(R.id.radioButton)
            val RadBtnNo: RadioButton = view.findViewById(R.id.radioButton2)
            RadBtnYes.isClickable = false
            RadBtnNo.isClickable = false
            tvDealName.text = ItemsViewModel.statement
            counter =1
            TotalQuestion.setText("$counter/$QuestionSize")
            Log.v("dataDealAfter", "$counter" + dataDraft)
            var radioData = ItemsViewModel.response
            if (radioData != null) {
                Log.v("radio", radioData)
                try {
                    when (radioData) {
                        "[{response=0}]" -> {
                            RadBtnNo.isChecked = true
                            if (counter == QuestionSize.toInt()){
                                NextBtn.visibility = View.INVISIBLE
                            }else {
                                NextBtn.visibility = View.VISIBLE
                                finishBtn.visibility = View.INVISIBLE
                            }
                        }
                        "[{response=1}]" -> {
                            RadBtnYes.isChecked = true
                            if (counter == QuestionSize.toInt()){
                                NextBtn.visibility = View.INVISIBLE
                            }else {
                                NextBtn.visibility = View.VISIBLE
                                finishBtn.visibility = View.INVISIBLE
                            }
                        }
                        "[]" -> {
//                                    NextBtn.visibility = View.GONE
                        }
                    }
//                else {
//                    Log.v("index", "error")
//                }
                }catch (e: Exception){
                   e.printStackTrace()
                }
            } else {
                Log.v("error", "no data found")
            }
            when (counter) {
                1 -> {
                    previousBtn.visibility = View.GONE
                }
                QuestionSize.toInt() -> {
                    NextBtn.visibility = View.GONE
                }
                else -> {

                }
            }
            NextBtn.setOnClickListener {
                previousBtn.visibility = View.VISIBLE
                counter++
                if (counter == QuestionSize.toInt()) {
                    NextBtn.visibility = View.INVISIBLE
                    if (deal_status == "Active") {
                        finishBtn.visibility = View.VISIBLE
                    }else{
                        finishBtn.visibility = View.INVISIBLE
                    }
                }
                fragment_draft_questions_list.PagerViewer.currentItem =
                    fragment_draft_questions_list.PagerViewer.currentItem + 1
                TotalQuestion.text = "$counter/$QuestionSize"

            }
            previousBtn.setOnClickListener {
                counter--
                if (counter == 1) {
                    previousBtn.visibility = View.GONE
                }
                NextBtn.visibility = View.VISIBLE
                finishBtn.visibility = View.INVISIBLE
                fragment_draft_questions_list.PagerViewer.currentItem =
                    fragment_draft_questions_list.PagerViewer.currentItem - 1
                TotalQuestion.text = "$counter/$QuestionSize"
            }
            container.addView(view, position)
            if(fragment_draft_questions_list.DealStatus.equals("Active")){
                groupRadBtn.isClickable = true
                RadBtnYes.isClickable = true
                RadBtnNo.isClickable = true
            groupRadBtn.setOnCheckedChangeListener { group, checkedId ->
                    RadBtnYes.isClickable = false
                    RadBtnNo.isClickable = false
                val radioButton: RadioButton = group.findViewById(checkedId)
                android.os.Handler().postDelayed({
                fragment_draft_questions_list.PagerViewer.currentItem =
                    fragment_draft_questions_list.PagerViewer.currentItem + 1
                RadBtnYes.isClickable = true
                RadBtnNo.isClickable = true
                }, 500)
                val response: Boolean = if (radioButton.text.equals("Yes"))  true else false
                var dataDeal = ModelClassForDraftDeal(questionId, response)
                if (counter <= QuestionSize.toInt()) {
                    dataDraft.add(dataDeal)
                    deal_Data = Deal_Data(
                        dealId = DealId.toInt(),
                        data = dataDraft)
                    Log.v("dataDealAfter", "$count" + dataDraft)
                    if(counter == QuestionSize.toInt()){
                        TotalQuestion.text = "$counter/$QuestionSize"
                        NextBtn.visibility = View.INVISIBLE
                        finishBtn.visibility = View.VISIBLE
                        counter++
                    }else{
                        counter++
                        TotalQuestion.text = "$counter/$QuestionSize"
                        if (counter == QuestionSize.toInt()){
                            NextBtn.visibility = View.INVISIBLE
                            finishBtn.visibility = View.VISIBLE
                        }
                    }
                } else {
                    NextBtn.visibility = View.INVISIBLE
                    Log.v("Errors","Already Selected")
                }
            }
        }else{

        }
        } catch (e: Exception) {
            Log.v("error", "" + e.message)
        }
        return view
    }

    override fun getCount(): Int {
        return itemList.size
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view == obj
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val view = `object` as View
        container.removeView(view)
    }
}