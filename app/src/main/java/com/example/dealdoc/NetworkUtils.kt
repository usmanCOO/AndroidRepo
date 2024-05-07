package com.example.dealdoc

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.text.Layout
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AlignmentSpan
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.FragmentActivity
import com.google.android.material.snackbar.Snackbar

object NetworkUtils {
    fun isNetworkConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        Log.v("NetworkChecking", activeNetworkInfo.toString() + "\n" + networkCapabilities.toString())
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true &&
                networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true
    }
//    fun showSnackbar(message: String, activity: FragmentActivity?) {
//        var rootView = activity?.findViewById<View>(android.R.id.content)
//        rootView?.let { rootView ->
//            Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show()
//        }
//    }
fun showSnackbar(message: String, activity: FragmentActivity?) {
    activity?.let { fragmentActivity ->
        val rootView = fragmentActivity.findViewById<View>(android.R.id.content)
        rootView?.let { rootView ->
            val snackbar = Snackbar.make(rootView, "", Snackbar.LENGTH_SHORT)
            val textView = snackbar.view.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
            textView.textAlignment = View.TEXT_ALIGNMENT_CENTER

            val spannableString = SpannableString(message)
            spannableString.setSpan(AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, spannableString.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            textView.text = spannableString
            snackbar.show()
        }
    }
}
}
