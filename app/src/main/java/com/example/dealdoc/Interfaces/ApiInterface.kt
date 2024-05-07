package com.example.dealdoc.Interfaces

import com.example.dealdoc.Models.*
import com.medpicc.dealdoc.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import java.sql.Statement

interface ApiInterface {
    @POST("api/auth/applesignin")
    @FormUrlEncoded
    fun sendGoogleId(
        @Field("appleID") googleId: String,
        @Field("email") googleEmail: String
    ): Call<Data>

    @POST("api/app/create_deal")
    @FormUrlEncoded
    fun CreateDeal(
        @Field("deal_name") deal_name: String,
        @Field("investment_size") investment_size: Number,
        @Field("closed_date") closedDate: String,
        @Field("status") status: String,
        @Header("Authorization") Authorization: String
    ): Call<ModelCLassForCreateDeal>

    @GET()
    fun GetAllDeal(
        @Url() url: String,
        @Header("Authorization") Authorization: String
    ): Call<questionModel>

    @GET("api/app/getvideosforapp")
    fun GetVideoDeal(
        @Header("Authorization") Authorization: String
    ): Call<ModelClassForCoachingVideos>
    //"api/app/deals"
    @GET()
    fun GetAllDeals(
        @Url() url: String,
        @Header("Authorization") Authorization: String
    ): Call<ModelClassForGetAllDeal>
    @GET("api/deal/isuserfirstdeal")
    fun GetFirstDeals(
        @Header("Authorization") Authorization: String
    ): Call<checkFirstDeal>

    @GET()
    fun GetMyStuffDeal(
        @Url() url: String,
        @Header("Authorization") Authorization: String
    ): Call<ModelClassForMyStuff>
    @GET()
    fun Getsubscription(
        @Url() url: String,
        @Header("Authorization") Authorization: String
    ): Call<getsubscription>

    @GET("api/auth/getuser")
    fun GetUserProfile(
        @Header("Authorization") Authorization: String
    ): Call<ModelClassForUserProfile>

    @PUT("api/auth/updateuser")
    @FormUrlEncoded
    fun UpdateProfile(
        @Field("fullName") fullName: String,
        @Field("phone_no") phoneNo: String,
        @Field("company") companyName: String,
        @Header("Authorization") Authorization: String
    ): Call<GetUserDataModelRequired>

    @POST("api/app/deals/draft")
    fun draftDeal(
        @Body dealData: Deal_Data,
        @Header("Authorization") Authorization: String
    ): Call<draftResponse>

    @PATCH()
    @FormUrlEncoded
    fun UpdateDeal(
        @Url() url: String,
//        @Body dealData: dealData,
        @Field("deal_name") deal_name: String,
        @Field("investment_size") deal_size: Int,
        @Field("closed_date") closedDate: String,
        @Header("Authorization") Authorization: String
    ): Call<dealUpdateData>

    @POST("api/app/deals/submit")
    @FormUrlEncoded
    fun submitDeal(
        @Field("dealId") deal_Id: Int,
        @Field("color") color: String,
        @Header("Authorization") Authorization: String
    ): Call<ModelClassForSubmitDeal>
    @POST("api/auth/postsubscription")
    @FormUrlEncoded
    fun subscription(
        @Field("status") status: Boolean,
        @Field("duration") duration: String,
        @Header("Authorization") Authorization: String
    ): Call<subscription>

    @POST("api/app/deals/shareDeal")
    @FormUrlEncoded
    fun sharedDeal(
        @Field("dealId") deal_Id: Int,
        @Field("email") email: String,
        @Field("message") message: String,
        @Header("Authorization") Authorization: String
    ): Call<ModelClassForSharedDeal>

    @GET()
    fun GetSharedByMe(
        @Url() url: String,
        @Header("Authorization") Authorization: String
    ): Call<ModelClassForSharedByMe>

    @GET()
    fun GetSharedWithMe(
        @Url() url: String,
        @Header("Authorization") Authorization: String
    ): Call<ModelClassForSharedByMe>

    @PATCH("api/app/deals/status")
    @FormUrlEncoded
    fun UpdateDealStatus(
        @Field("dealId") deal_id: Int,
        @Field("status") status: String,
        @Header("Authorization") Authorization: String
    ): Call<dealStatus>

    @Multipart
    @POST("api/app/upload")
    fun uploadImage(
        @Part file: MultipartBody.Part,
        @Header("Authorization") Authorization: String
    ): Call<ApiResponse>

    @DELETE()
    fun deleteUser(
        @Url() url: String,
        @Header("Authorization") Authorization: String
    ): Call<Unit>
    @GET()
    fun GetCommentsData(
        @Url() url: String,
        @Header("Authorization") Authorization: String
    ): Call<commentdata>
    @GET()
    fun GetUnReadCommentsData(
        @Url() url: String,
        @Header("Authorization") Authorization: String
    ): Call<unreadMessages>
    @POST("api/app/comment")
    @FormUrlEncoded
    fun sentComment(
        @Field("deal_id") deal_id: Int,
        @Field("statement") statement: String,
        @Header("Authorization") Authorization: String
    ): Call<sent_comment>
    @POST("api/app/comment")
    @FormUrlEncoded
    fun sentReplyComment(
        @Field("deal_id") deal_id: Int,
        @Field("statement") statement: String,
        @Field("replied_to") comment_id: Int,
        @Header("Authorization") Authorization: String
    ): Call<sent_comment>
}