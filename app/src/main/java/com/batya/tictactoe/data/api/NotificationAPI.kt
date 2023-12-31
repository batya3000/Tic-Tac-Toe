package com.batya.tictactoe.data.api

import com.batya.tictactoe.BuildConfig.SERVER_KEY
import com.batya.tictactoe.domain.model.PushNotification
import com.batya.tictactoe.util.Constants.CONTENT_TYPE
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationAPI {

    @Headers(
        "Authorization: key=$SERVER_KEY",
        "Content-Type:$CONTENT_TYPE"
    )
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification: PushNotification
    ): Response<ResponseBody>
}