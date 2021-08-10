package com.tiendunghk.cachingnetwork

import android.content.Context
import android.util.Log
import android.widget.Toast
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HttpManager {
    companion object {
        fun read(url: String, context: Context): ByteArray? {
            var result: ByteArray? = null

            val databaseHandler = DatabaseHandler(context)

            val caching = databaseHandler.getCaching(url)

            if (caching == null) {
                val retrofit: Retrofit = Retrofit.Builder()
                    .baseUrl("http://demo.abc")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

                val service = retrofit.create(NetworkService::class.java)

                val listCall = service.getTest(url)



                listCall.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                        if (response.isSuccessful) {
                            Log.d("Debug Info", "Get done")
                            result = response.body()?.bytes()

                            val length = response.body()!!.contentLength()
                            val sizeMB: Float = length * 1.0F / 1024 / 1024;
                            if (sizeMB > 100.0F) {
                                result = null
                                Toast.makeText(context, "Over 100MB", Toast.LENGTH_SHORT).show()
                                return
                            }

                            val cachings = databaseHandler.getCachings()
                            var sumSize = 0.0F
                            cachings.forEach { c -> sumSize += c.size }
                            sumSize += sizeMB

                            //Remove when over 100MB
                            if (sumSize >= 100.0F) {
                                var pivot = 0;
                                while (sumSize - cachings[pivot].size >= 100.0F && pivot < cachings.size) {
                                    pivot++
                                    sumSize -= cachings[pivot].size
                                }

                                for (i in 0..pivot) {
                                    databaseHandler.deleteCaching(cachings[i].url!!)
                                }
                            }

                            //add cache to DB
                            val status = databaseHandler.addCaching(CachingModel(0, url, result!!, sizeMB))
                        }
                    }


                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.d("Error", t.message.toString())
                    }
                })
            } else {
                result = caching.bytes
                Log.d("URL stored", caching.url!!)
                Toast.makeText(context, "Already stored", Toast.LENGTH_SHORT).show()
            }

            return result
        }
    }
}
