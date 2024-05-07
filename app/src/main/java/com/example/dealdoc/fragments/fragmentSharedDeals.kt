package com.example.dealdoc.fragments

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Spinner
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.tabs.TabLayout
import com.medpicc.dealdoc.R


class fragmentSharedDeals : Fragment() {
    lateinit var tabBtn: TabLayout
    companion object {
        lateinit var spinner: AutoCompleteTextView
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        loadFragment(fragment_shared_with_me())
        val view = inflater.inflate(R.layout.fragment_shared_deals, container, false)
        view.setOnClickListener {
        }
        try {
        tabBtn = view.findViewById(R.id.tabLayout_SharedDeal)
        spinner = view.findViewById(R.id.spinner)
            val SortBy = resources.getStringArray(R.array.sortBy)
            val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_menu, SortBy)
            spinner.setDropDownBackgroundDrawable( ColorDrawable(ContextCompat.getColor(requireContext(), R.color.black)));
            spinner.setAdapter(arrayAdapter)
        tabBtn.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    when (tab.position) {
                        0 -> {
                            loadFragment(fragment_shared_with_me())
                        }
                        else -> {
                            loadFragment(fragment_shared_by_me())
                        }
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                loadFragment(fragment_shared_by_me())
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
        }catch (e : Exception){
            Log.v("error",""+e.message)
        }

        return view
    }
    private fun loadFragment(fragment: Fragment) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.Shared_tab_Layout, fragment)
        transaction.commit()
    }
}