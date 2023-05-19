package com.ryansama.workdaynasaapp.data.api

import com.ryansama.workdaynasaapp.data.dto.NasaImagesRootDTO
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NasaImageApi {

    @GET("search")
    fun searchImages(
        @Query("q") query: String,
        @Query("media_type") mediaType: String = "image",
        @Query("page") page: Int? = null
    ): Single<Response<NasaImagesRootDTO>>
}