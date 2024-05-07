package com.example.dealdoc.fragments

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.util.Linkify
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.dealdoc.AdapterClasses.adapterClassForComment
import com.example.dealdoc.Models.*
import com.example.dealdoc.RetrofitInstance
import com.example.dealdoc.Utils
import com.example.dealdoc.Utils.CalendlybaseUrl
import com.example.dealdoc.Utils.baseUrl
import com.example.dealdoc.Utils.convertIsoToLocalTime
import com.example.dealdoc.Utils.convertTime24To12
import com.example.dealdoc.Utils.getUserComments
import com.example.dealdoc.Utils.getUserReadComments
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.medpicc.dealdoc.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class BottomSheetFragment : BottomSheetDialogFragment() {
   lateinit var btn1: Button
   private var token = ""
    lateinit var toolbar: Toolbar
    lateinit var imageview: ImageView
    lateinit var roundedImageViewMessage: ImageView
    lateinit var send_icon: ImageView
    lateinit var commentTV: TextView
    lateinit var commenterNameTV: TextView
    lateinit var commentEmailTV: TextView
    lateinit var commentPhoneTV: TextView
    lateinit var dealId: String
    lateinit var recycleview: RecyclerView
    lateinit var progressBarComment: ProgressBar
    var dialIntent: Intent? = null
   companion object {
       lateinit var commentET:EditText
       lateinit var globalView: View
       var dealCommentId : Int = 0
       var checkReplyBtn : Boolean = false
       private const val REQUEST_CALL_PHONE_PERMISSION = 1
   }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.bottomsheet_fragment, container,false)
        view.setOnClickListener {
            checkReplyBtn = false
            commentET.hint = "Type a message..."
        }
        globalView = view
        try {
            val sharedPreferences: SharedPreferences =
                (this.activity?.getSharedPreferences("prefs",0) ?: "") as SharedPreferences
            token = "Bearer "+sharedPreferences.getString("token","").toString()
            Log.v(ContentValues.TAG, "Google_id" + sharedPreferences.getString("token",""));
            val args = this.arguments
           dealId = args?.get("deal_id") as String
            var dealDescription = args?.get("deal_description")
            var dealCreator = args?.get("deal_creator")
            var dealCreatorprofile = args?.get("deal_creator_profile")

            init()
            actionListeners()
            commentTV.text = dealDescription.toString()
            commenterNameTV.text = dealCreator.toString()
            if (dealCreatorprofile == "null"){
                Log.v("TestingTheURL","check1 "+dealCreatorprofile)
            }else{
            var creatorProfileUrl = CalendlybaseUrl+dealCreatorprofile
            Log.v("TestingTheURL","check2 "+creatorProfileUrl)
            imageview?.let { it1 ->
                try {
                    context?.let {
                        Glide.with(it)
                            .load(creatorProfileUrl)
                            .placeholder(R.drawable.profiles)
                            .into(it1)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            }
            recycleview.layoutManager = LinearLayoutManager(context)
            getData()
            getCommentData()
            getReadData()
            val swipeRefreshLayout: SwipeRefreshLayout = view.findViewById(R.id.RefreshrecyclerViewcomments)
            swipeRefreshLayout.setColorSchemeColors(Color.RED)
            swipeRefreshLayout.setProgressBackgroundColorSchemeColor(Color.TRANSPARENT)
            swipeRefreshLayout.setOnRefreshListener {
                checkReplyBtn = false
                commentET.hint = "Type a message..."
                getData()
                getCommentData()
            swipeRefreshLayout.isRefreshing = false
            }
        }catch (e: Exception){
            Toast.makeText(context,""+e.message, Toast.LENGTH_SHORT).show()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                val transaction = parentFragmentManager.beginTransaction()
                transaction.replace(R.id.container, fragmentSharedDeals())
                transaction.commit()
            }
        })
    }

    private fun actionListeners() {
        toolbar.setNavigationOnClickListener{
            requireActivity().onBackPressed()
        }
        send_icon.setOnClickListener {
            if(commentET.text.isNullOrEmpty()){
                Toast.makeText(context,"First Add Comment",Toast.LENGTH_SHORT).show()
            }else{
//                Toast.makeText(context,"Comment"+ commentET.hint,Toast.LENGTH_SHORT).show()
                if(!checkReplyBtn){
                    progressBarComment.visibility = View.VISIBLE
            sendComment()
                }else{
                    sendReplyComment()
                }
            }
        }
//        commentEmailTV.setOnClickListener {
//            Toast.makeText(context,"check",Toast.LENGTH_SHORT).show()
//            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
//                data = Uri.parse("mailto:")  // Use the "mailto" scheme to open the email client
//                putExtra(Intent.EXTRA_EMAIL, arrayOf(commentEmailTV.text.toString())) // Specify the email address
//            }
//
//            if (emailIntent.resolveActivity(requireActivity().packageManager) != null) {
//                startActivity(emailIntent)
//            }
//        }
        commentEmailTV.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:recipient@example.com")

            // Get the text from the TextView
            val text = commentEmailTV.text.toString()

            // Set the subject of the email
            intent.putExtra(Intent.EXTRA_SUBJECT, "Subject")

            // Set the body of the email
            intent.putExtra(Intent.EXTRA_TEXT, text)

            startActivity(intent)
        }
//        commentPhoneTV.autoLinkMask = Linkify.PHONE_NUMBERS
        commentPhoneTV.setOnClickListener {
            val phone = commentPhoneTV.text.toString()
            val intent = Intent(Intent.ACTION_DIAL)
            if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                intent.data = Uri.parse("tel:$phone")
                startActivity(intent)
            } else {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CALL_PHONE), REQUEST_CALL_PHONE_PERMISSION)
            }
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CALL_PHONE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, call the number
                val phone = commentPhoneTV.text.toString()
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:$phone")
                startActivity(intent)
            } else {
                Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun sendComment() {
                    var comment_ = commentET.text.toString()
        Log.v("comments",comment_+dealId+token)
        RetrofitInstance.apiInterface.sentComment(dealId.toInt(),comment_,token)
            .enqueue(object : Callback<sent_comment?> {
                override fun onResponse(
                    call: Call<sent_comment?>,
                    response: Response<sent_comment?>
                ) {
                    if (response.isSuccessful) {
                        val inputMethodManager = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                        if (inputMethodManager.isAcceptingText) {
                            inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        } else {
                        }
                        commentET.text = null
                        progressBarComment.visibility = View.INVISIBLE
                        Log.v("comment", response.body().toString())
                    }else{
                        Log.v("error", response.body().toString())
                    }
                }
                override fun onFailure(call: Call<sent_comment?>, t: Throwable) {
                    Toast.makeText(context, ""+ t.message, Toast.LENGTH_SHORT).show()
                }
            })
    }
    private fun sendReplyComment() {
                    var comment_ = commentET.text.toString()
        Log.v("comments",comment_+dealId+token)
        RetrofitInstance.apiInterface.sentReplyComment(dealId.toInt(),comment_,dealCommentId,token)
            .enqueue(object : Callback<sent_comment?> {
                override fun onResponse(
                    call: Call<sent_comment?>,
                    response: Response<sent_comment?>
                ) {
                    if (response.isSuccessful) {
                        val inputMethodManager = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                        if (inputMethodManager.isAcceptingText) {
                            inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        } else {
                        }
                        commentET.text = null
                        Log.v("comment", response.body().toString())
                    }else{
                        Log.v("error", response.body().toString())
                    }
                }
                override fun onFailure(call: Call<sent_comment?>, t: Throwable) {
                    Toast.makeText(context, ""+ t.message, Toast.LENGTH_SHORT).show()
                }
            })
    }
    private fun init() {
        toolbar = globalView.findViewById(R.id.toolbar)
        imageview = globalView.findViewById(R.id.roundedImageView)
        roundedImageViewMessage = globalView.findViewById(R.id.roundedImageViewMessage)
        commentTV = globalView.findViewById(R.id.commentTV)
        commenterNameTV = globalView.findViewById(R.id.commenterNameTV)
        commentEmailTV = globalView.findViewById(R.id.commentEmailTV)
        commentPhoneTV = globalView.findViewById(R.id.commentPhoneTV)
        recycleview = globalView.findViewById(R.id.recyclerViewcomments)
        send_icon = globalView.findViewById(R.id.send_icon)
        commentET = globalView.findViewById(R.id.message_text_field)
        progressBarComment = globalView.findViewById(R.id.progressBar_comment)
    }
    private fun getData() {
        RetrofitInstance.apiInterface.GetUserProfile(token)
            .enqueue(object : Callback<ModelClassForUserProfile?> {
                override fun onResponse(
                    call: Call<ModelClassForUserProfile?>,
                    response: Response<ModelClassForUserProfile?>
                ) {
                   if(response.isSuccessful){
                       Log.v("profileData",response.body().toString())
                       if (response.body()?.data == null){

                       }else {
                           commentEmailTV.text = response.body()?.data?.email
                           commentPhoneTV.text = response.body()?.data?.phone_no
                           val url =
                               CalendlybaseUrl +response.body()?.data?.profilePhoto.toString()
                           context?.let {
//                               imageview?.let { it1 ->
//                                   try {
//                                       Glide.with(it.applicationContext)
//                                           .load(url)
//                                           .placeholder(R.drawable.profiles)
//                                           .into(it1)
//                                   } catch (e: Exception) {
//                                       e.printStackTrace()
//                                   }
//                               }
                               roundedImageViewMessage?.let { it1 ->
                                   try {
                                       Glide.with(it.applicationContext)
                                           .load(url)
                                           .into(it1)
                                   } catch (e: RuntimeException) {
                                       e.printStackTrace()
                                   }
                               }
                           }
                       }
                   }else{
                       Log.v("Image Error", "No Profile Found")
                   }
                }
                override fun onFailure(call: Call<ModelClassForUserProfile?>, t: Throwable) {
                    Toast.makeText(context, ""+ t.message, Toast.LENGTH_SHORT).show()
                }
            })
    }
    private fun getCommentData() {
        var url = getUserComments+dealId
        RetrofitInstance.apiInterface.GetCommentsData(url,token)
            .enqueue(object : Callback<commentdata?> {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(
                    call: Call<commentdata?>,
                    response: Response<commentdata?>
                ) {
                    if(response.isSuccessful){
                        Log.v("CommentData",response.body()?.data.toString())
                        val sizeOfArray = response.body()?.data?.size
                        val data = ArrayList<comment_data>()
                        for (i in 0 until sizeOfArray!!) {
                            try {
                                var TimeAndDate = response.body()!!.data.get(i).createdAt
                                var localTime = convertIsoToLocalTime(TimeAndDate).toString()
                                val stringArray: List<String> = localTime.split("T")
                                var time = convertTime24To12(stringArray[1])
//                                Log.v("Profile", response.body()!!.data[i].User.profilePhoto)
                                var image = ""
                                image = if (response.body()!!.data[i].User.profilePhoto == null) {
                                    "null"
                                } else {
                                    response.body()!!.data[i].User.profilePhoto
                                }
                                response.body()?.data?.get(i)?.User?.fullName?.let {
                                    data.add(
                                        comment_data(
                                            it,
                                            stringArray[0],
                                            time,
                                            response.body()!!.data[i].statement,
                                            image,
                                            response.body()!!.data[i].Replies,
                                            response.body()!!.data[i].id,
                                            response.body()!!.data[i].deal_id,
                                        )
                                    )
                                }
                            }catch (e: NullPointerException){
                                e.printStackTrace()
                            }
                        }
                        // This will pass the ArrayList to our Adapter
                        val adapter = adapterClassForComment(data)
//                        progressbar.visibility = View.GONE
                        //Setting the Adapter with the recyclerview
                        recycleview.adapter = adapter
                    }else{
                        Log.v("Error","error")
                    }
                }
                override fun onFailure(call: Call<commentdata?>, t: Throwable) {
                    Toast.makeText(context, ""+ t.message, Toast.LENGTH_SHORT).show()
                }
            })
    }
    private fun getReadData() {
        var custom_Url = getUserReadComments+dealId
        RetrofitInstance.apiInterface.GetUnReadCommentsData(custom_Url,token)
            .enqueue(object : Callback<unreadMessages> {
                override fun onResponse(
                    call: Call<unreadMessages?>,
                    response: Response<unreadMessages?>
                ) {
                    if(response.isSuccessful){
//                        Toast.makeText(context,"Success",Toast.LENGTH_SHORT).show()
                    }else{
                        Log.v("Error","error")
                    }
                }

                override fun onFailure(call: Call<unreadMessages>, t: Throwable) {

                }
            })
    }
}
