package com.ryansama.workdaynasaapp.domain.repository

import com.ryansama.workdaynasaapp.domain.model.NasaImagesResult
import io.reactivex.Single

interface NasaImageRepository {
    fun getImages(query: String, page: Int? = null): Single<NasaImagesResult>
}