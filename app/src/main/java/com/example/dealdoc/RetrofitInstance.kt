package com.example.dealdoc

import com.example.dealdoc.Interfaces.ApiInterface
import com.example.dealdoc.Utils.baseUrl
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance  {

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiInterface by lazy {
        retrofit.create(ApiInterface::class.java)
    }

}