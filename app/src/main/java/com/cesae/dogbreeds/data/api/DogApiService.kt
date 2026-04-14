package com.cesae.dogbreeds.data.api

import com.cesae.dogbreeds.data.model.Breed
import retrofit2.http.GET
import retrofit2.http.Query

interface DogApiService {
    @GET("breeds")
    suspend fun getBreeds(
        @Query("limit") limit: Int = 100,
        @Query("page") page: Int = 0
    ): List<Breed>
}
