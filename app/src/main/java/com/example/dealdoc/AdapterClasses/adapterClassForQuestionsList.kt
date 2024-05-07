package com.example.dealdoc.AdapterClasses

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.dealdoc.fragment_questions
import com.medpicc.dealdoc.ModelClassForQuestionNames
import com.medpicc.dealdoc.ModelClassForStatements
import com.medpicc.dealdoc.R

class adapterClassForQuestionsList(private val dealQuestionList: ArrayList<ModelClassForQuestionNames>) :
    RecyclerView.Adapter<adapterClassForQuestionsList.ViewHolder>() {
    companion object {
        var adapter_QuestionsList: ViewPagerAdapter? = null
        var sizeOfQuestions: Int = 0
    }

    var array: ArrayList<ModelClassForStatements> = ArrayList()

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.questions_names_recycle_view, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = dealQuestionList[position]
        val deal_id = ItemsViewModel.deal_id
        holder.tvDealName.text = ItemsViewModel.QuestionTitle
        holder.DealQuestionCategory.setOnClickListener {
            sizeOfQuestions = ItemsViewModel.Questions?.size!!
            for (i in 0 until sizeOfQuestions!!) {
                ItemsViewModel.Questions?.get(i)?.statement?.let { it1 ->
                    ItemsViewModel.Questions?.get(i)?.id?.let { it2 ->
                            array.add(
                                ModelClassForStatements(
                                    it1, it2
                                )
                            )
                        }
                }
            }
            val bundle = Bundle().apply {
                putString("CategoryName", holder.tvDealName.text.toString())
                putString("QuestionLenght", sizeOfQuestions.toString())
                putString("deal_id", deal_id.toString())
            }
            adapter_QuestionsList = ViewPagerAdapter(holder.itemView.context, array)

            val appCompatActivity = it.context as AppCompatActivity
            loadFragment(fragment_questions(), bundle, appCompatActivity)
        }
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return dealQuestionList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val tvDealName: TextView = itemView.findViewById(R.id.QuestionsNameListtV)
        val DealQuestionCategory: CardView = itemView.findViewById(R.id.QuestionCategoryLayoutRV)
    }

    private fun loadFragment(fragment: Fragment, id: Bundle, context: Context) {
        val appCompatActivity = context as AppCompatActivity
        fragment.arguments = id
        val transaction = appCompatActivity.supportFragmentManager.beginTransaction()
        transaction.add(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

}