package com.example.dealdoc.AdapterClasses

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.example.dealdoc.fragment_questions
import com.example.dealdoc.fragment_questions.Companion.QuestionSize
import com.example.dealdoc.fragment_questions.Companion.TotalQuestion
import com.example.dealdoc.fragment_questions.Companion.dataDraft
import com.example.dealdoc.fragment_questions.Companion.deal__Data
import com.medpicc.dealdoc.*

class ViewPagerAdapter(
    private val mContext: Context,
    private val itemList: ArrayList<ModelClassForStatements>
) : PagerAdapter() {
    private var layoutInflater: LayoutInflater? = null
    private var count = 1
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = LayoutInflater.from(mContext)
        val view =
            layoutInflater!!.inflate(R.layout.questions_list_recycle_view, container, false)
        try {
            val ItemsViewModel = itemList[position]
            val questionId = ItemsViewModel.id
            val tvDealName: TextView = view.findViewById(R.id.DealsQuestionCategoryWise)
            val groupRadBtn: RadioGroup = view.findViewById(R.id.GroupYesNoRBtn)
            tvDealName.text = ItemsViewModel.statement
            container.addView(view, position)
            TotalQuestion.text = "$count/$QuestionSize"
            groupRadBtn.setOnCheckedChangeListener { group, checkedId ->
                val radioButton: RadioButton = group.findViewById(checkedId)
                if(count == QuestionSize.toInt()){

                }else{
                count++
                }
                TotalQuestion.setText("$count/$QuestionSize")
                fragment_questions.PagerViewer.currentItem =
                    fragment_questions.PagerViewer.currentItem + 1

                val response: Boolean
                if (radioButton.text.equals("Yes")) {
                    response = true
                } else {
                    response = false
                }
                var dataDeal = ModelClassForDraftDeal(questionId, response)
                dataDraft.add(dataDeal)
                deal__Data = Deal_Data(
                    dealId = fragment_questions.DealId.toInt(),
                    data = dataDraft
                )
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