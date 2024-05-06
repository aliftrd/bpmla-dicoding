package com.dicoding.asclepius.data.source.remote.retrofit

import retrofit2.Call
import com.dicoding.asclepius.data.source.remote.response.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("top-headlines")
    fun getTopHeadlines(
        @Query("q") query: String,
        @Query("category") category: String = "health",
        @Query("language") language: String = "en",
    ): Call<NewsResponse>
}