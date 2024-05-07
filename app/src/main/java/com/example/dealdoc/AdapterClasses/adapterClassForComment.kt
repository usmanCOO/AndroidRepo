package com.example.dealdoc.AdapterClasses


import android.app.Activity
import android.content.ClipData.Item
import android.content.Context
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dealdoc.Models.comment_data
import com.example.dealdoc.Utils.CalendlybaseUrl
import com.example.dealdoc.Utils.baseUrl
import com.example.dealdoc.Utils.changeDateFormat
import com.example.dealdoc.Utils.convertIsoToLocalTime
import com.example.dealdoc.Utils.convertTime24To12
import com.example.dealdoc.fragments.BottomSheetFragment
import com.example.dealdoc.fragments.BottomSheetFragment.Companion.checkReplyBtn
import com.example.dealdoc.fragments.BottomSheetFragment.Companion.commentET
import com.example.dealdoc.fragments.BottomSheetFragment.Companion.dealCommentId
import com.medpicc.dealdoc.R
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class adapterClassForComment(private val dealList: ArrayList<comment_data>) :
    RecyclerView.Adapter<adapterClassForComment.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.comment_recycleview, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = dealList[position]
        try {
            holder.tvprofileName.text = ItemsViewModel.fullName
            holder.tvDealDate.text = changeDateFormat(ItemsViewModel.date)
            holder.tvDealTime.text = ItemsViewModel.time
            holder.tvcomment.text = ItemsViewModel.statement
//            Log.v("profile", ItemsViewModel.profile)
            if(ItemsViewModel.profile.toString() == "null"){
                holder.itemView.context?.let {

                }
            }else {
                val url = CalendlybaseUrl+ItemsViewModel.profile.toString()
                holder.itemView.context?.let {
                    holder.IVProfile?.let { it1 ->
                        try {
                        Glide.with(it.applicationContext)
                            .load(url)
                            .into(it1)
                        }catch (e: RuntimeException){
                            e.printStackTrace()
                        }
                    }
                }
            }
            if(ItemsViewModel.Replies.isEmpty()){
                Log.v("Replies__","Empty")
                holder.ReplyLayout.visibility = View.GONE
            }else if(ItemsViewModel.Replies[0].User == null){
                holder.ReplyLayout.visibility = View.GONE
            }
            else{
                Log.v("Replies","${ItemsViewModel.Replies.toString()}")
                holder.ReplyLayout.visibility = View.VISIBLE
                var sizeOfReplies = ItemsViewModel.Replies.size
                for (i in 0 until sizeOfReplies!!) {
                    var TimeAndDate = ItemsViewModel.Replies[i].createdAt
                    var localTime = convertIsoToLocalTime(TimeAndDate).toString()
                    val stringArray: List<String> = localTime.split("T")
                    var time = convertTime24To12(stringArray.get(1))
                    holder.tvcomment_reply.text = ItemsViewModel.Replies[i].statement
                    holder.tvprofileName_reply.text = ItemsViewModel.Replies[i].User.fullName
                    holder.tvDealDate_reply.text = changeDateFormat(stringArray[0])
                    holder.tvDealTime_reply.text = time
                    if(ItemsViewModel.Replies[i].User.profilePhoto == null){
                        holder.itemView.context?.let {

                        }
                    }else {
                        val url = CalendlybaseUrl+ItemsViewModel.Replies[i].User.profilePhoto
                        holder.itemView.context?.let {
                            holder.IVProfile_reply?.let { it1 ->
                                try {
                                Glide.with(it.applicationContext)
                                    .load(url)
                                    .into(it1)
                                }catch (e: RuntimeException){
                                    e.printStackTrace()
                                }
                            }
                        }
                    }
                }
                Log.v("Reply__",ItemsViewModel.Replies.toString())
            }
            holder.btncommentReply.setOnClickListener {
                commentET.hint = "Reply to ${ItemsViewModel.fullName}"
                checkReplyBtn = true
                dealCommentId = ItemsViewModel.CommentId
            }
            holder.CommentLayout.setOnClickListener {
                checkReplyBtn = false
                commentET.hint = "Type a message..."
                val inputMethodManager = holder.itemView.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                if (inputMethodManager.isAcceptingText) {
                    inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                } else {
                }
            }
        }catch (e: Exception){
            Log.v("error",e.printStackTrace().toString())
        }
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return dealList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val tvprofileName: TextView = itemView.findViewById(R.id.commentprofileNameRV)
        val tvDealDate: TextView = itemView.findViewById(R.id.commentDatetextViewRV)
        val tvDealTime: TextView = itemView.findViewById(R.id.commentTimeTextViewRV)
        val tvcomment: TextView = itemView.findViewById(R.id.commentTVRV)
        val IVProfile: ImageView = itemView.findViewById(R.id.roundedImagecommentRV)
        val tvprofileName_reply: TextView = itemView.findViewById(R.id.commentprofileNameRV_reply)
        val tvDealDate_reply: TextView = itemView.findViewById(R.id.commentDatetextViewRV_reply)
        val tvDealTime_reply: TextView = itemView.findViewById(R.id.commentTimeTextViewRV_reply)
        val tvcomment_reply: TextView = itemView.findViewById(R.id.commentTVRV_reply)
        val IVProfile_reply: ImageView = itemView.findViewById(R.id.roundedImagecommentRV_reply)
        val btncommentReply: Button = itemView.findViewById(R.id.commentReply)
        val ReplyLayout: LinearLayout = itemView.findViewById(R.id.comment_RecycleView_reply)
        val CommentLayout: LinearLayout = itemView.findViewById(R.id.comment_RecycleView)
    }
}