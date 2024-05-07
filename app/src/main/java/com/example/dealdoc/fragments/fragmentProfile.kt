package com.example.dealdoc.fragments

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.dealdoc.Models.GetUserDataModelRequired
import com.example.dealdoc.Models.ModelClassForUserProfile
import com.example.dealdoc.NetworkUtils
import com.example.dealdoc.NetworkUtils.isNetworkConnected
import com.example.dealdoc.NetworkUtils.showSnackbar
import com.example.dealdoc.RetrofitInstance
import com.example.dealdoc.Utils.CalendlybaseUrl
import com.example.dealdoc.Utils.baseUrl
import com.example.dealdoc.activities.SignIn
import com.medpicc.dealdoc.ApiResponse
import com.medpicc.dealdoc.R
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


class fragmentProfile : Fragment() {
    private companion object {
        lateinit var globalView: View
        private var imageview: ImageView? = null
        private val GALLERY = 1
        private val CAMERA = 2
        private var name: TextView? = null
        private var phoneNumber: TextView? = null
//        private var phoneNumber: PhonemojiTextInputEditText? = null
        private var email: TextView? = null
        private var Companyname: TextView? = null
        private var editBtn: Button? =null
        private var updateBtn: Button? =null
        private var token =""
        private val IMAGE_DIRECTORY = "/demonuts"
    }
    private var nameTV : TextView? = null
    private var emailTV : TextView? = null
    private var isConnected = false
    private lateinit var progressBar : ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_profile, container, false)
        view.setOnClickListener {
        }
        try {

            isConnected = context?.let { isNetworkConnected(it) } == true
            val sharedPreferences: SharedPreferences =
                (this.activity?.getSharedPreferences("prefs", 0) ?: "") as SharedPreferences
            token = "Bearer " + sharedPreferences.getString("token", "").toString()
            globalView = view
            findViews()
            actionListeners()
            if (isConnected) {
                getData()
            } else {
                try {
                    showSnackbar("Check Internet", activity)
                    val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
                    val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
                        override fun onReceive(context: Context?, intent: Intent?) {
                            if (isAdded && context != null) { // check if fragment is added to an activity
                                if (isNetworkConnected(context)) {
                                    getData()
                                } else {
                                    showSnackbar("Check Internet", activity)
                                }
                            }
                        }
                    }
                    requireActivity().registerReceiver(broadcastReceiver, filter)
                } catch (e: Exception) {
                    Toast.makeText(context, "${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
        return view
    }

    private fun findViews() {
        imageview = globalView.findViewById<View>(R.id.roundedImageView) as ImageView
        name = globalView.findViewById(R.id.editTextName) as EditText
        phoneNumber = globalView.findViewById(R.id.editTextPhoneNumber)
        email = globalView.findViewById(R.id.editTextTextEmailAddress) as EditText
        Companyname = globalView.findViewById(R.id.editTextCompanyName) as EditText
        nameTV = globalView.findViewById(R.id.ProfileNameTv) as TextView
        emailTV = globalView.findViewById(R.id.ProfileMailTv) as TextView
        editBtn = globalView.findViewById(R.id.Editbutton) as Button
        updateBtn = globalView.findViewById(R.id.Updatebutton) as Button
        progressBar = globalView.findViewById(R.id.progressBar_profile) as ProgressBar
    }
    private fun actionListeners() {
        editBtn!!.setOnClickListener {editBtnClickListener()}
        updateBtn!!.setOnClickListener {
            if (isConnected) {
                progressBar.visibility = View.VISIBLE
                UpdateuserProfile()
            } else {
                try {
                    showSnackbar("Check Internet", activity)
                    val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
                    val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
                        override fun onReceive(context: Context?, intent: Intent?) {
                            if (isAdded && context != null) { // check if fragment is added to an activity
                                if (isNetworkConnected(context)) {
                                    progressBar.visibility = View.VISIBLE
                                    UpdateuserProfile()
                                } else {
                                    showSnackbar("Check Internet", activity)
                                }
                            }
                        }
                    }
                    requireActivity().registerReceiver(broadcastReceiver, filter)
                } catch (e: Exception) {
                    Toast.makeText(context, "${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun getData() {
        RetrofitInstance.apiInterface.GetUserProfile(token)
            .enqueue(object : Callback<ModelClassForUserProfile?> {
                override fun onResponse(
                    call: Call<ModelClassForUserProfile?>,
                    response: Response<ModelClassForUserProfile?>
                ) {
                    if (response.isSuccessful){
                        Log.v("ProfileData", ""+response.body().toString())
                            progressBar.visibility = View.INVISIBLE
                            if (response.body()?.data?.fullName.toString() == "null" && response.body()?.data?.company.toString() == "null") {
                                phoneNumber?.setText(response.body()?.data?.phone_no)
                                email?.setText(response.body()?.data?.email)
                                emailTV?.setText(response.body()?.data?.email)
                            } else {
                                name?.text = response.body()?.data?.fullName.toString()
                                nameTV?.text = response.body()?.data?.fullName.toString()
                                phoneNumber?.text = response.body()?.data?.phone_no
                                email?.text = response.body()?.data?.email
                                emailTV?.text = response.body()?.data?.email
                                Companyname?.text = response.body()?.data?.company.toString()
                            }

                        val url = CalendlybaseUrl+response.body()?.data?.profilePhoto.toString()
                            context?.let {
                                imageview?.let { it1 ->
                                    Glide.with(it.applicationContext)
                                        .load(url)
                                        .into(it1)
                                }
                            }
                    }else if(response.message() == "Not Found"){
                        val sharedPreferencesFileNames = listOf("com.google.android.gms.signin", "pref", "prefs")
                        for (sharedPreferencesFileName in sharedPreferencesFileNames) {
                            context?.getSharedPreferences(sharedPreferencesFileName, Context.MODE_PRIVATE)
                                ?.edit()
                                ?.clear()
                                ?.apply()
                        }
                            val myIntent = Intent(context, SignIn::class.java)
                            context?.startActivity(myIntent)
                    }else{
                        showSnackbar("Internal Server Error", activity)
                    }
                }
                override fun onFailure(call: Call<ModelClassForUserProfile?>, t: Throwable) {
//                    Toast.makeText(context, ""+ t.message, Toast.LENGTH_SHORT).show()
                }
            })
    }
    private fun editBtnClickListener() {
        if (!progressBar.isVisible) {
            imageview!!.setOnClickListener { showPictureDialog() }
            name?.isEnabled = true
            phoneNumber?.isEnabled = true
            Companyname?.isEnabled = true
            editBtn?.visibility = View.GONE
            updateBtn?.visibility = View.VISIBLE
        }else{
            showSnackbar("Wait for loading data", activity)
        }
    }
    private fun UpdateuserProfile() {
        try {
            var Username = name?.text.toString()
            var UserPhone = phoneNumber?.text.toString()
            var UserCompany = Companyname?.text.toString()

            if (name?.text.isNullOrEmpty()) {
                showSnackbar("Must Enter Profile Name", activity)
            }else if(TextUtils.isEmpty(phoneNumber?.text.toString())){
                showSnackbar("Must Enter Phone Number", activity)
            }else if(TextUtils.isEmpty(Companyname?.text.toString())){
                showSnackbar("Must Enter Company Name", activity)
            }
            else{
                RetrofitInstance.apiInterface.UpdateProfile(Username, UserPhone, UserCompany, token)
                    .enqueue(object : Callback<GetUserDataModelRequired?> {
                        override fun onResponse(
                            call: Call<GetUserDataModelRequired?>,
                            response: Response<GetUserDataModelRequired?>
                        ) {
                            if (response.isSuccessful) {
                                progressBar.visibility = View.INVISIBLE
                                showSnackbar("Profile Updated", activity)
                                imageview!!.setOnClickListener { }
                                name?.isEnabled = false
                                phoneNumber?.isEnabled = false
                                Companyname?.isEnabled = false
                                editBtn?.visibility = View.VISIBLE
                                updateBtn?.visibility = View.GONE
                            }else{
                                showSnackbar("Internal Server Error", activity)
                            }
                        }

                        override fun onFailure(
                            call: Call<GetUserDataModelRequired?>,
                            t: Throwable
                        ) {
                            if (t.message == "Unable to resolve host \"api.davidweisssales.com\": No address associated with hostname") {
                                showSnackbar("Server issue try Again Later", activity)
                            } else {
                                showSnackbar("Check Internet & Refresh page", activity)
                            }
//                            Toast.makeText(context, "" + t.message, Toast.LENGTH_SHORT).show()
                        }
                    })
            }
            } catch (e: Exception){
                Log.v("Error", "" + e.message)
            }

    }
    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(context)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems =
            arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(
            pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> choosePhotoFromGallary()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }
    private fun choosePhotoFromGallary() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, GALLERY)
    }
    private fun takePhotoFromCamera() {
        val Camintent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(Camintent, CAMERA)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY) {
            if (data != null) {
                val contentURI = data!!.data
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(
                        requireActivity().contentResolver,
                        contentURI
                    )
                    imageview!!.setImageBitmap(bitmap)
                    val file = bitmapToFile(bitmap, requireContext())
                    val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                    val multipartBody = MultipartBody.Part.createFormData("photo", file.name, requestBody)
                    val call = RetrofitInstance.apiInterface.uploadImage(multipartBody, token)
                    call.enqueue(object : Callback<ApiResponse> {
                        override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                            if (response.isSuccessful){
                                showSnackbar("Image Uploaded!", activity)
                            }else{
                                showSnackbar("Try Again", activity)
                            }
                        }

                        override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                            // Handle error
                        }
                    })
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }

        } else if (requestCode == CAMERA) {
            try {
                val thumbnail = data!!.extras!!.get("data") as Bitmap
                imageview!!.setImageBitmap(thumbnail)
                val file = bitmapToFile(thumbnail, requireContext())
                val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                val multipartBody = MultipartBody.Part.createFormData("photo", file.name, requestBody)
                val call = RetrofitInstance.apiInterface.uploadImage(multipartBody, token)
                call.enqueue(object : Callback<ApiResponse> {
                    override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                        if (response.isSuccessful){
                            showSnackbar("Image Uploaded!", activity)
                        }else{
                            showSnackbar("Try Again", activity)
                        }
                    }
                    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                        // Handle error
                    }
                })
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }
    fun bitmapToFile(bitmap: Bitmap, context: Context): File {
        val filesDir = context.filesDir
        val imageFile = File(filesDir, "image.png")
        val os: OutputStream
        try {
            os = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
            os.flush()
            os.close()
        } catch (e: Exception) {
            Log.e("Error writing bitmap", e.message!!)
        }
        return imageFile
    }
}

