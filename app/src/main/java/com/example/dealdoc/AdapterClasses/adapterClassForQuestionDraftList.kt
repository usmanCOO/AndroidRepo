package com.example.dealdoc.AdapterClasses

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.dealdoc.fragments.fragmentQuestionCategoriesListInDeals
import com.example.dealdoc.fragments.fragmentQuestionCategoriesListInDeals.Companion.dealCloseDateUpdated
import com.example.dealdoc.fragments.fragmentQuestionCategoriesListInDeals.Companion.dealColor
import com.example.dealdoc.fragments.fragmentQuestionCategoriesListInDeals.Companion.dealDescription
import com.example.dealdoc.fragments.fragmentQuestionCategoriesListInDeals.Companion.dealName
import com.example.dealdoc.fragments.fragmentQuestionCategoriesListInDeals.Companion.dealPrice
import com.example.dealdoc.fragments.fragmentQuestionCategoriesListInDeals.Companion.dealSharedByDate
import com.example.dealdoc.fragments.fragmentQuestionCategoriesListInDeals.Companion.dealSharedWithDate
import com.example.dealdoc.fragments.fragmentQuestionCategoriesListInDeals.Companion.dealUpdatedDate
import com.example.dealdoc.fragments.fragmentQuestionCategoriesListInDeals.Companion.materialCoachingBtn
import com.example.dealdoc.fragments.fragmentQuestionCategoriesListInDeals.Companion.shareBtn
import com.example.dealdoc.fragments.fragment_draft_questions_list
import com.medpicc.dealdoc.*
import java.util.concurrent.atomic.AtomicInteger

class adapterClassForQuestionDraftList(private val dealQuestionList: ArrayList<ModelClassForQuestionNames>) :
    RecyclerView.Adapter<adapterClassForQuestionDraftList.ViewHolder>() {
    companion object {
        var adapter_draft_QuestionsList: ViewPagerAdapterForDraftQuestions? = null
        var adapter_draft_QuestionsListOff: ViewPagerAdapterForDraftQuestionOff? = null
        var sizeOfQuestions: Int = 0
        var redCount: Int = 0
        var orangeCount: Int = 0
        var yellowCount: Int = 0
        var lightGreenCount: Int = 0
        var greenCount: Int = 0
        var isFinish: Int = 0
        var hexaColor: String = ""
        var sizeOfList = 0
    var array: ArrayList<ModelClassForStatementsAndResponse> = ArrayList()
    var array1: ArrayList<ModelClassForStatementsAndResponseOff> = ArrayList()
    }


    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.question_names_draft_deal_recycle_view, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = dealQuestionList[position]
        val deal_id = ItemsViewModel.deal_id
        val deal_status = ItemsViewModel.deal_Status
//        Log.v("status",""+deal_status)
        sizeOfList = itemCount
        holder.tvDealName.text = ItemsViewModel.QuestionTitle
        sizeOfQuestions = ItemsViewModel.Questions?.size!!
        holder.DealQuestionCategory.setOnClickListener {
            array.clear()
            array1.clear()
            sizeOfQuestions = ItemsViewModel.Questions?.size!!
            for (i in 0 until sizeOfQuestions!!) {
                ItemsViewModel.Questions?.get(i)?.statement?.let { it1 ->
                    ItemsViewModel.Questions?.get(i)?.id?.let { it2 ->
                        ItemsViewModel.Questions?.get(i)?.QuestionResponses?.let { it3 ->
//                            Log.v("TestingDealisAdded",""+ItemsViewModel.Questions?.get(i)?.QuestionResponses.toString())
                            var responseOff = ItemsViewModel.Questions?.get(i)?.QuestionResponses.toString()
//                            Log.v("TestingDealisAdded","${responseOff.toString()}")
                            if ( responseOff == "[{response=0}]") {
//                                Log.v("TestingDealisAdded","done")
                                array1.add(
                                    ModelClassForStatementsAndResponseOff(
                                        it1, it2, it3.toString(), deal_status
                                    )
                                )
                            }else {

                            }
                            array.add(
                                ModelClassForStatementsAndResponse(
                                    it1, it2, it3.toString(), deal_status
                                )
                            )
                        }
                    }
                }
            }
            val bundle = Bundle().apply {
                if(deal_status == "shared") {
                    Log.v("Dealllll","Shared")
                    putString("CategoryName", holder.tvDealName.text.toString())
                    putString("QuestionLenght", sizeOfQuestions.toString())
                    putString("deal_id", deal_id.toString())
                    putString("deal_status", deal_status)
                    putString("deal_Name", dealName)
                    putString("deal_Price", dealPrice)
                    putString("deal_Color", dealColor)
                    putString("deal_closeDate", dealCloseDateUpdated)
                    putString("deal_updated", dealUpdatedDate)
                    putString("deal_creator", dealSharedByDate)
                    putString("deal_shared", dealSharedWithDate)
                    putString("deal_description", dealDescription)
                }else{
                    Log.v("Dealllll","Active")
                    putString("CategoryName", holder.tvDealName.text.toString())
                    putString("QuestionLenght", sizeOfQuestions.toString())
                    putString("deal_id", deal_id.toString())
                    putString("deal_status", deal_status)
                    putString("deal_Name", dealName)
                    putString("deal_Price", dealPrice)
                    putString("deal_Color", dealColor)
                    putString("deal_closeDate", dealCloseDateUpdated)
                }
            }
            adapter_draft_QuestionsList =
                ViewPagerAdapterForDraftQuestions(holder.itemView.context, array)
            adapter_draft_QuestionsListOff =
                ViewPagerAdapterForDraftQuestionOff(holder.itemView.context, array1)
            val appCompatActivity = it.context as AppCompatActivity
            loadFragment(fragment_draft_questions_list(), bundle, appCompatActivity)
        }
        var isCategoryAllQuestionFilled: Boolean
        val trueCount = AtomicInteger(0)
        val falseCount = AtomicInteger(0)
        var yesPercent = 0.0
        for (i in 0 until sizeOfQuestions!!) {
            var res = ItemsViewModel.Questions?.get(i)?.QuestionResponses.toString()
            if (res != null) {
                if (res.equals("[{response=1}]")) {
                    trueCount.incrementAndGet()
                } else if (res.equals("[{response=0}]")) {
                    falseCount.incrementAndGet()
                } else if (res.equals("[]")) {
                }
            }
        }
        try {
        if (sizeOfQuestions == trueCount.get() + falseCount.get()) {
            isCategoryAllQuestionFilled = true
            yesPercent = (trueCount.get().toDouble() / sizeOfQuestions.toDouble() ) * 100
        } else {
            isCategoryAllQuestionFilled = false
        }
            //            <40% Red
            //            40 - 60% Orange
            //            60 - 85% Yellow
            //            85% - 95% Light Green
            //            95 - 100% Green
            when {
                sizeOfQuestions > 0 && isCategoryAllQuestionFilled -> {
                    when {
                        yesPercent < 40.0 -> {
                            val color = Color.parseColor("#FF0000")
                            val backgroundTint = ColorStateList.valueOf(Color.argb(100, Color.red(color), Color.green(color), Color.blue(color)))
                            holder.DealQuestionCategory.backgroundTintList = backgroundTint
                            holder.ImageViewColor.visibility = View.VISIBLE
                            holder.ImageViewColor.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
                            //                    Log.v("Color", "red")
                            //                    statusFinish(isFinished = true)
                            isFinish +=1
                            redCount += 1
                        }
                        yesPercent in 40.0..60.0 -> {
                            val color = Color.parseColor("#FFA500")
                            val backgroundTint = ColorStateList.valueOf(Color.argb(100, Color.red(color), Color.green(color), Color.blue(color)))
                            holder.DealQuestionCategory.backgroundTintList = backgroundTint
                            holder.ImageViewColor.visibility = View.VISIBLE
                            holder.ImageViewColor.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
                            isFinish +=1
                            orangeCount += 1
                        }
                        yesPercent in 60.0..85.0 -> {
                            val color = Color.parseColor("#FFFF00")
                            val backgroundTint = ColorStateList.valueOf(Color.argb(100, Color.red(color), Color.green(color), Color.blue(color)))
                            holder.DealQuestionCategory.backgroundTintList = backgroundTint
                            holder.ImageViewColor.visibility = View.VISIBLE
                            holder.ImageViewColor.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
                            isFinish +=1
                            yellowCount += 1
                        }
                        yesPercent in 85.0..95.0 -> {
                            val color = Color.parseColor("#90EE90")
                            val backgroundTint = ColorStateList.valueOf(Color.argb(100, Color.red(color), Color.green(color), Color.blue(color)))
                            holder.DealQuestionCategory.backgroundTintList = backgroundTint
                            holder.ImageViewColor.visibility = View.VISIBLE
                            holder.ImageViewColor.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
                            isFinish +=1
                            lightGreenCount += 1
                        }
                        yesPercent in 95.0..100.0 -> {
                            val color = Color.parseColor("#00FF00")
                            val backgroundTint = ColorStateList.valueOf(Color.argb(100, Color.red(color), Color.green(color), Color.blue(color)))
                            holder.DealQuestionCategory.backgroundTintList = backgroundTint
                            holder.ImageViewColor.visibility = View.VISIBLE
                            holder.ImageViewColor.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
                            isFinish +=1
                            greenCount += 1
                        }
                        else -> {
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.v("error", e.message.toString())
        }
        try {
//            var hexaColor = "";
            // If Red is Greater
            if (redCount > greenCount && redCount > lightGreenCount && redCount > yellowCount && redCount > orangeCount) {
                System.out.println("red is dominant");
                hexaColor = "#FF0000";
            }
            // If Green is Greater
            else if (greenCount > redCount && greenCount > yellowCount && greenCount > lightGreenCount && greenCount > orangeCount) {
                if ((greenCount == 5 && redCount == 2) || (greenCount == 5 && redCount == 3) || (greenCount == 6 && redCount == 2) || (greenCount == 7 && redCount == 1) || (greenCount == 6 && redCount == 1) || (greenCount == 4 && redCount == 2) || (greenCount == 3 && redCount == 2) || (greenCount == 5 && redCount == 1) || (greenCount == 4 && redCount == 3)) {
                    System.out.println("yellow is dominant");
                    hexaColor = "#FFFF00";
                } else if ((greenCount == 6 && yellowCount == 2) || (greenCount == 7 && yellowCount == 1) || (greenCount == 5 && yellowCount == 3) || (greenCount == 5 && yellowCount == 1) || (greenCount == 5 && yellowCount == 2)) {
                    System.out.println("Light is dominant");
                    hexaColor = "#91EE92";
                } else {
                    System.out.println("Green is dominant");
                    hexaColor = "#00FF00";
                }
            }
            // If Yellow is Greater
//            else if (yellowCount > redCount && yellowCount > greenCount && yellowCount > lightGreenCount && yellowCount > orangeCount) {
//                System.out.println("yellow is dominant");
//                hexaColor = "#FFFF00";
//            }
            // If lightGreen is Greater
            else if (lightGreenCount > redCount && lightGreenCount > greenCount && lightGreenCount > yellowCount && lightGreenCount > orangeCount) {
                System.out.println("Light is dominant");
                hexaColor = "#91EE92";
            }
            // If Orange is Greater
            else if (orangeCount > redCount && orangeCount > greenCount && orangeCount > yellowCount && orangeCount > lightGreenCount) {
                System.out.println("orange is dominant");
                hexaColor = "#FFA500";
            }
            // Equality Comparison
            // if Red and Green are equal
            else if (redCount > lightGreenCount && redCount > yellowCount && redCount > orangeCount && greenCount > yellowCount && greenCount > lightGreenCount && greenCount > orangeCount && redCount == greenCount) {
                System.out.println("yellow is dominant");
                hexaColor = "#FFFF00";
            }
//             if Red and yellow are equal
            else if (redCount > lightGreenCount && redCount > orangeCount && redCount > greenCount && yellowCount > greenCount && yellowCount > lightGreenCount && yellowCount > orangeCount && redCount == yellowCount) {
                System.out.println("yellow is dominant");
                hexaColor = "#FFFF00";
            }
            // if Green and yellow are equal
            else if (greenCount > redCount && greenCount > lightGreenCount && greenCount > orangeCount && yellowCount > redCount && yellowCount > lightGreenCount && yellowCount > orangeCount && yellowCount == greenCount) {
                System.out.println("orange is dominant");
                hexaColor = "#FFA500";
            }
            // if green and lightgreen are equal
            else if (greenCount > redCount && greenCount > yellowCount && greenCount > orangeCount && lightGreenCount > redCount && lightGreenCount > yellowCount && lightGreenCount > orangeCount && greenCount == lightGreenCount) {
                System.out.println("Light is dominant");
                hexaColor = "#91EE92";
            }
            else if (greenCount == 2 && redCount == 2 && yellowCount == 2 && lightGreenCount == 2 && orangeCount == 2) {
                System.out.println("yellow is dominant");
                hexaColor = "#FFFF00";
            }
            else if (greenCount == 2 && redCount == 2 && lightGreenCount == 2 && orangeCount == 2) {
                System.out.println("yellow is dominant");
                hexaColor = "#FFFF00";
            } else if (greenCount == 2 && redCount == 2 && yellowCount == 2 && lightGreenCount == 2) {
                System.out.println("yellow is dominant");
                hexaColor = "#FFFF00";
            } else if (redCount == 2 && yellowCount == 2 && lightGreenCount == 2 && orangeCount == 2) {
                System.out.println("yellow is dominant");
                hexaColor = "#FFFF00";
            } else if (greenCount == 2 && redCount == 2 && yellowCount == 2 && orangeCount == 2) {
                System.out.println("yellow is dominant");
                hexaColor = "#FFFF00";
            } else if (greenCount == 2 && yellowCount == 2 && lightGreenCount == 2 && orangeCount == 2) {
                System.out.println("yellow is dominant");
                hexaColor = "#FFFF00";
            }
//&&&&&
            else if (greenCount == 2 && yellowCount == 2 && lightGreenCount == 1 && orangeCount == 1 && redCount == 1) {
                System.out.println("yellow is dominant");
                hexaColor = "#FFFF00";
            } else if (greenCount == 2 && yellowCount == 1 && lightGreenCount == 1 && orangeCount == 2 && redCount == 2) {
                System.out.println("yellow is dominant");
                hexaColor = "#FFFF00";
            } else if (greenCount == 1 && yellowCount == 2 && lightGreenCount == 2 && orangeCount == 1 && redCount == 1) {
                System.out.println("yellow is dominant");
                hexaColor = "#FFFF00";
            } else if (greenCount == 1 && yellowCount == 1 && lightGreenCount == 2 && orangeCount == 1 && redCount == 2) {
                System.out.println("yellow is dominant");
                hexaColor = "#FFFF00";
            } else if (greenCount == 1 && yellowCount == 2 && lightGreenCount == 2 && orangeCount == 1 && redCount == 2) {
                System.out.println("yellow is dominant");
                hexaColor = "#FFFF00";
            } else if (lightGreenCount == 3 && greenCount == 3 && yellowCount == 2 || lightGreenCount == 3 && greenCount == 3 && redCount == 2 || lightGreenCount == 3 && greenCount == 3 && yellowCount == 1 && redCount == 1) {
                System.out.println("Light is dominant");
                hexaColor = "#91EE92";
            } else {
                System.out.println("yellow is dominant");
                hexaColor = "#FFFF00";
            }

//            when {
//                redCount > orangeCount && redCount > yellowCount && redCount > lightGreenCount && redCount > greenCount -> {
//                    Log.v("selectedColor"," red is dominant")
//                    hexaColor = "#FF0000"
//                }
//                orangeCount > redCount && orangeCount > yellowCount && orangeCount > lightGreenCount && orangeCount > greenCount -> {
//                    Log.v("selectedColor"," orange is dominant")
//                    hexaColor = "#FFA500"
//                }
//                yellowCount > redCount && yellowCount > orangeCount && yellowCount > lightGreenCount && yellowCount > greenCount -> {
//                    Log.v("selectedColor"," yellow is dominant")
//                    hexaColor = "#FFFF00"
//                }
//                lightGreenCount > redCount && lightGreenCount > yellowCount && lightGreenCount > orangeCount && lightGreenCount > greenCount -> {
//                    Log.v("selectedColor"," Light is dominant")
//                    hexaColor = "#90EE90"
//                }
//                greenCount > redCount && greenCount > yellowCount && greenCount > orangeCount && greenCount > lightGreenCount -> {
//                    Log.v("selectedColor"," Green is dominant")
//                    hexaColor = "#00FF00"
//                }
//                greenCount >= yellowCount && greenCount >= orangeCount && greenCount >= redCount && lightGreenCount == greenCount -> {
//                    Log.v("selectedColor"," Green is dominant")
//                    hexaColor = "#00FF00"
//                }
//                greenCount >= lightGreenCount && greenCount >= orangeCount && greenCount >= redCount && yellowCount == greenCount -> {
//                    Log.v("selectedColor"," Green is dominant")
//                    hexaColor = "#00FF00"
//                }
//                lightGreenCount >= greenCount && lightGreenCount >= orangeCount && lightGreenCount >= redCount && yellowCount == lightGreenCount -> {
//                    Log.v("selectedColor"," Light is dominant")
//                    hexaColor = "#90EE90"
//                }
//                yellowCount >= greenCount && yellowCount >= lightGreenCount && yellowCount >= redCount && yellowCount == orangeCount -> {
//                    Log.v("selectedColor"," yellow is dominant")
//                    hexaColor = "#FFFF00"
//                }
//                greenCount >= lightGreenCount && greenCount >= yellowCount && greenCount >= redCount && orangeCount == greenCount -> {
//                    Log.v("selectedColor"," Green is dominant")
//                    hexaColor = "#00FF00"
//                }
//                lightGreenCount >= greenCount && lightGreenCount >= yellowCount && lightGreenCount >= redCount && orangeCount == lightGreenCount -> {
//                    Log.v("selectedColor"," Light is dominant")
//                    hexaColor = "#90EE90"
//                }
//                greenCount >= lightGreenCount && greenCount >= yellowCount && greenCount >= orangeCount && redCount == greenCount -> {
//                    Log.v("selectedColor"," Green is dominant")
//                    hexaColor = "#00FF00"
//                }
//                lightGreenCount >= greenCount && lightGreenCount >= yellowCount && lightGreenCount >= orangeCount && redCount == lightGreenCount -> {
//                    Log.v("selectedColor"," Light is dominant")
//                    hexaColor = "#90EE90"
//                }
//                yellowCount >= greenCount && yellowCount >= lightGreenCount && yellowCount >= orangeCount && redCount == yellowCount -> {
//                    Log.v("selectedColor"," yellow is dominant")
//                    hexaColor = "#FFFF00"
//                }
//                orangeCount >= greenCount && orangeCount >= yellowCount && orangeCount >= lightGreenCount && redCount == orangeCount -> {
//                    Log.v("selectedColor"," orange is dominant")
//                    hexaColor = "#FFA500"
//                }
//                else -> {
//                    Log.v("selectedColor"," red is dominant")
//                    hexaColor = "#FF0000"
//                }
//            }

            Log.v("Colors",
                "RedColor $redCount Orange color $orangeCount Yellow color $yellowCount Light Green color $lightGreenCount Green color $greenCount"
            )

            if(deal_status.equals("shared")) {
                Log.v("status",""+deal_status)
            }else{
                if (isFinish == sizeOfList) {
                    shareBtn.visibility = View.VISIBLE
                    materialCoachingBtn.visibility = View.VISIBLE

                } else {

                }
            }
        }catch (e: Exception){
            Log.v("error", e.message.toString())
        }
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return dealQuestionList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val tvDealName: TextView = itemView.findViewById(R.id.QuestionsNamedraftListtV)
        val ImageViewColor: ImageView = itemView.findViewById(R.id.imageViewColor)
        val DealQuestionCategory: CardView =
            itemView.findViewById(R.id.QuestionCategoryDraftLayoutRV)
    }
private fun loadFragment(fragment: Fragment, id: Bundle, context: Context) {
    val appCompatActivity = context as AppCompatActivity
    fragment.arguments = id
    val transaction = appCompatActivity.supportFragmentManager.beginTransaction()
    transaction.replace(R.id.container, fragment)
    transaction.commit()
}

}