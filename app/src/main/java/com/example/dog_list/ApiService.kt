package com.example.dog_list

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiService {
//    suspend para funciones que se ejecutan en corutines
    @GET
    suspend fun getDogsByBreed(@Url url: String): Response<DogResponse>
}