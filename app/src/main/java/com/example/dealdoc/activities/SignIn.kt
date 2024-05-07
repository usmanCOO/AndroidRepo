package com.example.dealdoc.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.dealdoc.Models.Data
import com.example.dealdoc.NetworkUtils
import com.example.dealdoc.NetworkUtils.isNetworkConnected
import com.example.dealdoc.NetworkUtils.showSnackbar
import com.example.dealdoc.RetrofitInstance
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.medpicc.dealdoc.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignIn : AppCompatActivity() {
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var sharedPreferences: SharedPreferences
    private var PREFS_KEY = "prefs"
    private var Token = "token"
    private var AlreadyToken = ""
    private var progressBar: ProgressBar? = null
    lateinit var terms : TextView
    lateinit var policy : TextView
    lateinit var googleLoginButton : Button
    private var isConnected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_in)
        supportActionBar?.hide()
        isConnected = isNetworkConnected(this)  == true
            sharedPreferences = getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)
            AlreadyToken = sharedPreferences.getString(Token, "").toString()
            progressBar = findViewById(R.id.progress_bar_signIn)
            terms = findViewById(R.id.conditionText)
            policy = findViewById(R.id.privacyText)
            terms.movementMethod = LinkMovementMethod.getInstance()
            policy.movementMethod = LinkMovementMethod.getInstance()
            val gso =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build()

            mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

            googleLoginButton = findViewById<Button>(R.id.SignInbutton)
        googleLoginButton.isClickable = true
//        if (isConnected) {
            // The device is connected to the internet
            googleLoginButton.setOnClickListener {
                googleLoginButton.isClickable = false
                progressBar!!.visibility = View.VISIBLE
                signIn()
                progressBar!!.visibility = View.GONE
            }
//        } else {
//            // The device is not connected to the internet
//            Toast.makeText(this@SignIn, "Check Internet Connection",Toast.LENGTH_SHORT).show()
//        }

    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(
            signInIntent, RC_SIGN_IN
        )
    }

    private fun signOut() {
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(this) {
                // Update your UI here
            }
    }

    private fun revokeAccess() {
        mGoogleSignInClient.revokeAccess()
            .addOnCompleteListener(this) {
                // Update your UI here
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(
                ApiException::class.java
            )
            // Signed in successfully
//            Log.v(TAG, "Google_id" + account.id);
            getData(account.id, account.email)

        } catch (e: ApiException) {
            googleLoginButton.isClickable = true
            // Sign in was unsuccessful
            Log.e(
                "failed code=", e.statusCode.toString()
            )
        }
    }

    private fun getData(id: String?, email: String?) {
        if (id != null) {
            if (email != null) {
                try {
                    RetrofitInstance.apiInterface.sendGoogleId(id, email)
                        .enqueue(object : Callback<Data?> {
                            override fun onResponse(call: Call<Data?>, response: Response<Data?>) {
//                                Log.v(TAG, "Response" + response.body());
                                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                                editor.putString(Token, response.body()?.token.toString())
                                editor.apply()
                                val myIntent = Intent(this@SignIn, HomePage::class.java)
                                this@SignIn.startActivity(myIntent)
                                finish()
                            }
                            override fun onFailure(call: Call<Data?>, t: Throwable) {
//                                Toast.makeText(
//                                    applicationContext,
//                                    "${t.localizedMessage}",
//                                    Toast.LENGTH_LONG
//                                ).show()
                                Toast.makeText(
                                    applicationContext,
                                    "Check Internet",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        })
                }catch (e: Exception){
//                    Toast.makeText(applicationContext, ""+e.message.toString(), Toast.LENGTH_SHORT).show()
                    Toast.makeText(applicationContext, "Check Internet", Toast.LENGTH_SHORT).show()
                }
            }
        }else{
            Toast.makeText(this@SignIn, "Google Id Not Found Please Try Again", Toast.LENGTH_SHORT).show()
        }
    }
    companion object {
        const val RC_SIGN_IN = 9001
    }

    override fun onStart() {
        super.onStart()
        if (!AlreadyToken.equals("")) {
            val i = Intent(this@SignIn, HomePage::class.java)
            startActivity(i)
            finish()
        }
    }
}
