package com.example.dealdoc.fragments

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.dealdoc.Utils.CalendlybaseUrl
import com.example.dealdoc.Utils.baseUrl
import com.example.dealdoc.activities.HomePage
import com.google.android.material.tabs.TabLayout
import com.medpicc.dealdoc.R

class fragmentCoachingMaterial : Fragment() {
    lateinit var tabBtn: TabLayout
    lateinit var dealID: String
    lateinit var url: String
    private var token = ""
    lateinit var SpeakDocDealBtn: Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        loadFragment(fragment_coaching())
        val sharedPreferences: SharedPreferences =
            (this.activity?.getSharedPreferences("prefs", 0) ?: "") as SharedPreferences
//        token = "Bearer " + sharedPreferences.getString("token", "").toString()
        token = sharedPreferences.getString("token", "").toString()
        val view = inflater.inflate(R.layout.fragment_coaching_material, container, false)
        tabBtn = view.findViewById(R.id.tabLayout_CoachingMaterial)
        SpeakDocDealBtn = view.findViewById(R.id.SpeakDocDealBtn)
        view.setOnClickListener {
        }
        val args = arguments
        dealID = args?.getString("deal_id").toString()
        url = if(dealID == "null") {
            CalendlybaseUrl+"calendly?token=$token"
        }else {
            CalendlybaseUrl+"calendly?deal_id=$dealID&token=$token"
        }
        Log.v("url",url+dealID)
        tabBtn.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    when (tab.position) {
                        0 -> {
                            loadFragment(fragment_coaching())
                        }
                        1 -> {
                            loadFragment(fragment_my_stuff())
                        }
                        else -> {
                //                        loadFragment(fragment_tab_lost())
                        }
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
        SpeakDocDealBtn.setOnClickListener {
            Log.v("click",url)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
        return view
    }
    private fun loadFragment(fragment: Fragment) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.CoachingMaterialFL, fragment)
        transaction.commit()
    }
}