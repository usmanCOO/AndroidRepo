package com.example.dealdoc

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.AcknowledgePurchaseResponseListener
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ConsumeResponseListener
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.example.dealdoc.Interfaces.Security
import com.example.dealdoc.Models.checkFirstDeal
import com.example.dealdoc.Models.subscription
import com.example.dealdoc.fragments.fragmentCreateDeal
import com.medpicc.dealdoc.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.security.Signature
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class fragment_subscription : Fragment() {

    lateinit var GlobalView: View
    lateinit var IVBackBtnSubscription: ImageView
    lateinit var TVTermToUse: TextView
    lateinit var BuyBtnSubscription: Button
    lateinit var BuyBtncheckSubscription: Button
    private var billingClient: BillingClient? = null

    var response: String? = null
    var des: String? = null
    var sku: String? = null
    var isSuccess = false
    var token = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_subscription, container, false)
        view.setOnClickListener {
        }
        val sharedPreferences: SharedPreferences =
            (this.activity?.getSharedPreferences("prefs", 0) ?: "") as SharedPreferences
        token = "Bearer " + sharedPreferences.getString("token", "").toString()
        billingClient = context?.let {
            BillingClient.newBuilder(it)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build()
        }
        GlobalView = view
        init()
        actionListeners()
        return view
    }

    private fun subscribed() {
        try {
            RetrofitInstance.apiInterface.subscription(isSuccess,"30",token)
                .enqueue(object : Callback<subscription?> {
                    override fun onResponse(
                        call: Call<subscription?>,
                        response: Response<subscription?>
                    ) {
                        if (response.isSuccessful) {

                        } else {
                            Log.v("error", "error")
                        }
                    }

                    override fun onFailure(call: Call<subscription?>, t: Throwable) {
                        if (t.message == "Unable to resolve host \"api.davidweisssales.com\": No address associated with hostname"){
                            Toast.makeText(context, "Server issue try Again Later", Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(context,"Check Internet & Try Again", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun init() {
        IVBackBtnSubscription = GlobalView.findViewById(R.id.IVBackBtnSubscription)
        TVTermToUse = GlobalView.findViewById(R.id.TermToUse)
        BuyBtnSubscription = GlobalView.findViewById(R.id.buysubscriptionBtn)
//        BuyBtncheckSubscription = GlobalView.findViewById(R.id.buysubscriptionCheckBtn)
    }

    private fun actionListeners() {
        getPrice()
        IVBackBtnSubscription.setOnClickListener {
            loadFragment(fragmentCreateDeal())
        }
//        BuyBtncheckSubscription.setOnClickListener {
//            getPrice()
//        }
        BuyBtnSubscription.setOnClickListener {
            billingClient!!.startConnection(object : BillingClientStateListener {
                override fun onBillingServiceDisconnected() {

                }

                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    val productList = listOf(
                        QueryProductDetailsParams.Product.newBuilder()
                            .setProductId("dealdoc1.1")
                            .setProductType(BillingClient.ProductType.SUBS)
                            .build()
                    )
                    val params = QueryProductDetailsParams.newBuilder()
                        .setProductList(productList)
                    billingClient!!.queryProductDetailsAsync(params.build()) { billingResult, productDetailsList ->

                        for (productDetails in productDetailsList) {
                            val offerToken =
                                productDetails.subscriptionOfferDetails?.get(0)?.offerToken
                            val productDetailsParamList =
                                listOf(
                                    offerToken?.let {
                                        BillingFlowParams.ProductDetailsParams.newBuilder()
                                            .setProductDetails(productDetails)
                                            .setOfferToken(it)
                                            .build()
                                    }
                                )
                            val billingFlowParams = BillingFlowParams.newBuilder()
                                .setProductDetailsParamsList(productDetailsParamList)
                                .build()
                            val billingResult = billingClient!!.launchBillingFlow(
                                requireActivity(),
                                billingFlowParams
                            )
                        }
                    }
                }

            })
        }
        TVTermToUse.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun loadFragment(fragment: Fragment) {
        val appCompatActivity = context as AppCompatActivity
        val transaction = appCompatActivity.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.commit()
    }

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, Purchase ->
        try {
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && Purchase != null) {
                for (purchase in Purchase) {
                    handlePurchase(purchase)
                }
            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
                Toast.makeText(context, "ITEM ALREADY OWNED", Toast.LENGTH_SHORT).show()
                isSuccess = true
                subscribed()
            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED) {
                Toast.makeText(context, "FEATURE NOT SUPPORTED", Toast.LENGTH_SHORT).show()
            } else {
//                if (billingResult.debugMessage != null) {
//                    Log.v("Error", "Try Again")
//                } else {
                    Toast.makeText(context,"${billingResult.debugMessage}",Toast.LENGTH_SHORT).show()
//                }
            }
        } catch (e: RuntimeException) {
            Log.v("Runtime Exception:", "${e.message}")
//            Toast.makeText(context, "Runtime Exception: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        val consumeParams = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        val listener = ConsumeResponseListener { billingResult, s ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            }
        }
        billingClient!!.consumeAsync(consumeParams, listener)
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {

            if (!verifyValidsignature(purchase.originalJson, purchase.signature)) {
                Toast.makeText(context, "Error : invalid Purchase", Toast.LENGTH_SHORT).show()
                return
            }
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams
                    .newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                billingClient!!.acknowledgePurchase(
                    acknowledgePurchaseParams,
                    acknowledgePurchaseResponseListener
                )
                Toast.makeText(context, "Subscribe successfully", Toast.LENGTH_SHORT).show()
                isSuccess = true
                subscribed()
            } else {
                Toast.makeText(context, "Already Subscribed", Toast.LENGTH_SHORT).show()
            }
        } else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
            Toast.makeText(context, "Subscription Pending", Toast.LENGTH_SHORT).show()
        } else if (purchase.purchaseState == Purchase.PurchaseState.UNSPECIFIED_STATE) {
            Toast.makeText(context, "UNSPECIFIED_STATE", Toast.LENGTH_SHORT).show()
        }
    }

    var acknowledgePurchaseResponseListener = AcknowledgePurchaseResponseListener { billingResult ->
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            isSuccess = true
            subscribed()
        }
    }

    private fun verifyValidsignature(signedData: String, signature: String): Boolean {
        return try {
            val base64Key =
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlNIIJJeCS0psorz0LuYj6ihWc+n7/beOabTql8JyqA5cc47PneDcJ3r5WeTez9W0gl6TupZmHr0mKLUbmx/yqy79po/wnuo1TtmL17sYI55Ldo7UYL6dWWF8MWpla2UGFC3ybCeVyOhFhxtMJSL589Etkf8fDeUXn4hfrE9iuOKw0UnQv+NOI1Qis8pJYEyDRnnyyNh1ddT1KYormfLjFF1WmOs9RrCZdYwmqDEa4AtbrRJcBZm4RkLBOWBdWg3cHBb//KNDCor/KjKhGIaein7uLtEvxfLvs1clZTxmc29yDCiG0k/SMI+1dzGoOYuWRzTVq0U4yICJOk2uk3sY9QIDAQAB"
            val security = Security()
            security.verifyPurchase(base64Key, signedData, signature)
        } catch (e: IOException) {
            false
        }
    }

    private fun getPrice() {
//        Toast.makeText(context,"Test",Toast.LENGTH_SHORT).show()
        billingClient!!.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {

            }

            override fun onBillingSetupFinished(billingResult: BillingResult) {
                val executorService = Executors.newSingleThreadExecutor()
                executorService.execute {
                    val productList =
                        listOf(
                            QueryProductDetailsParams.Product.newBuilder()
                                .setProductId("dealdoc1.1")
                                .setProductType(BillingClient.ProductType.SUBS)
                                .build()
                        )
                    val params = QueryProductDetailsParams.newBuilder().setProductList(productList)
                    billingClient!!.queryProductDetailsAsync(params.build()) { billingResult, productDetailsList ->
                        for (productDetails in productDetailsList) {
                            response =
                                productDetails.subscriptionOfferDetails?.get(0)?.pricingPhases?.pricingPhaseList?.get(
                                    0
                                )?.formattedPrice
                            sku = productDetails.name
                            val ds = productDetails.description
                            des = "$sku : $ds: price: $response"
                        }
                    }
                }
                Thread.sleep(2000)
                Toast.makeText(context, "Price $response des: $des", Toast.LENGTH_SHORT).show()
                Log.v("billingData", "Price $response des: $des")
            }
        })
    }

}