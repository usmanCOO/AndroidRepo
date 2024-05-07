package com.example.dealdoc.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.dealdoc.Models.ModelClassForUserProfile
import com.example.dealdoc.RetrofitInstance
import com.example.dealdoc.activities.SignIn
import com.example.dealdoc.fragments.fragmentTabActive.Companion.token
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputLayout
import com.medpicc.dealdoc.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeFragment : Fragment() {
    lateinit var tabBtn: TabLayout
    companion object{
    lateinit var autocompleteTV: AutoCompleteTextView
}
    override fun onCreateView(inflater: LayoutInflater,
                          container: ViewGroup?,
                          savedInstanceState: Bundle?): View? {
        loadFragment(fragmentTabActive())
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        view.setOnClickListener {
        }
        tabBtn = view.findViewById(R.id.tabLayout)
        val SortBy = resources.getStringArray(R.array.sortBy)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_menu, SortBy)
        autocompleteTV = view.findViewById(R.id.autoCompleteTextView)
        autocompleteTV.setDropDownBackgroundDrawable( ColorDrawable(ContextCompat.getColor(requireContext(), R.color.black)));
        autocompleteTV.setAdapter(arrayAdapter)
        tabBtn.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    when (tab.position) {
                        0 -> {
                            loadFragment(fragmentTabActive())
                        }
                        1 -> {
                            loadFragment(fragment_tab_won())
                        }
                        else -> {
                            loadFragment(fragment_tab_lost())
                        }
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
        return view
        }
    private fun loadFragment(fragment: Fragment) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.tab_Layout, fragment)
        transaction.commit()
    }
    }