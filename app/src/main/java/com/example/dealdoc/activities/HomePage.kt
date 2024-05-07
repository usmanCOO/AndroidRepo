package com.example.dealdoc.activities

import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.dealdoc.fragments.*
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.medpicc.dealdoc.R

class HomePage : AppCompatActivity() {
    companion object {
    lateinit var fabBtn: FloatingActionButton
    lateinit var bottomNav: BottomNavigationView
    }
    private val MY_REQUEST_CODE = 100
    private var isFragmentLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)
        supportActionBar?.hide()
        findViews()
        CheckUpdateApp()
        loadFragment(HomeFragment())
        actionListeners()
        bottomNav.background = null
        bottomNav.menu.getItem(2).isEnabled = false
    }
    private fun findViews(){
        fabBtn = findViewById(R.id.fab)
        bottomNav = findViewById(R.id.bottomNavigationView)
    }
    private fun actionListeners(){
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.createDeal -> {
                    loadFragment(fragmentCreateDeal())
                    true
                }
                R.id.profile -> {
                    loadFragment(fragmentProfile())
                    true
                }
                R.id.sharedDeals -> {
                    loadFragment(fragmentSharedDeals())
                    true
                }
                R.id.yourDeal -> {
                    loadFragment(fragmentCoachingMaterial())
                    true
                }
                else -> false
            }

        }
        fabBtn.setOnClickListener {
                loadFragment(HomeFragment())
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.commit()
//        if (!isFragmentLoading) {
//            isFragmentLoading = true
//            fabBtn.isEnabled = false
//            val transaction = supportFragmentManager.beginTransaction()
//            transaction.replace(R.id.container, fragment)
//            transaction.commit()
//            supportFragmentManager.executePendingTransactions()
//            isFragmentLoading = false
//            fabBtn.isEnabled = true
//        }
    }

    fun CheckUpdateApp() {
        val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(this)

// Returns an intent object that you use to check for an update.
        val appUpdateInfoTask: Task<AppUpdateInfo> = appUpdateManager.appUpdateInfo

// Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(OnSuccessListener<AppUpdateInfo> { appUpdateInfo: AppUpdateInfo ->
            if (appUpdateInfo.updateAvailability() === UpdateAvailability.UPDATE_AVAILABLE // This example applies an immediate update. To apply a flexible update
                // instead, pass in AppUpdateType.FLEXIBLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                // Request the update.
                try {
                    appUpdateManager.startUpdateFlowForResult( // Pass the intent that is returned by 'getAppUpdateInfo()'.
                        appUpdateInfo,  // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                        AppUpdateType.IMMEDIATE,  // The current activity making the update request.
                        this,  // Include a request code to later monitor this update request.
                        MY_REQUEST_CODE
                    )
                } catch (e: SendIntentException) {
                    throw RuntimeException(e)
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
//                log("Update flow failed! Result code: " + resultCode);
                // If the update is cancelled or fails,
                // you can request to start the update again.
            }
        }
    }
}