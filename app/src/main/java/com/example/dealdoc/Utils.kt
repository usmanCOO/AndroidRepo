package com.example.dealdoc

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object Utils {
    val baseUrl = "https://api.dealdoc.app/"
    val CalendlybaseUrl = "https://admin.dealdoc.app/"
    val ImagesUrl = "$baseUrl/Images/"
    var getuserDeals = baseUrl+"api/app/userdeals/"
    var deleteUserDeal = baseUrl+"api/app/deals/"
    var getUserSubscription = baseUrl+"api/auth/getsubscription"
    var getUserSessions = baseUrl+"api/app/getusersessions"
    var getUserComments = baseUrl+"api/app/comment/"
    var getUserReadComments = baseUrl+"api/notification/readnotification/"
    var getSharedByMeDeals = baseUrl+"api/app/deals_shared/"
    var getSharedWithMeDeals = baseUrl+"api/app/my_shared_deals/"
    var updateDealData = baseUrl+"api/app/deals/update"
    var deleteSharedDeal = baseUrl+"api/app/deals/"
    fun changeDateFormat(dateFormat: String): String {

        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())

        val date = inputFormat.parse(dateFormat)
        val outputDate = outputFormat.format(date)

        println(outputDate) // Output: 06-20-2023
        Log.v("date", "Date: $outputDate")
        return outputDate
    }
    fun convertTime24To12(time24: String): String {
        val timeArray = time24.split(":")
        var hour = timeArray[0].toInt()
        val minute = timeArray[1]
        val ampm = if (hour < 12) "AM" else "PM"
        if (hour == 0) {
            hour = 12
        } else if (hour > 12) {
            hour -= 12
        }
        return String.format("%d:%s %s", hour, minute, ampm)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun convertIsoToLocalTime(isoTime: String): LocalDateTime {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")
        val offsetDateTime = OffsetDateTime.parse(isoTime, formatter)
        return offsetDateTime.atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
    }

}