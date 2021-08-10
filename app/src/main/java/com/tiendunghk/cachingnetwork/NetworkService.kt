package com.tiendunghk.cachingnetwork

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface NetworkService {
    @GET
    fun getTest(@Url url: String): Call<ResponseBody>
}
