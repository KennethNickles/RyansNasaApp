package com.ryansama.workdaynasaapp.domain.usecase

import com.ryansama.workdaynasaapp.domain.model.NasaImagesResult
import com.ryansama.workdaynasaapp.domain.repository.NasaImageRepository
import io.reactivex.Single

interface GetNasaImagesUseCase {
    fun execute(query: String, page: Int? = null): Single<NasaImagesResult>
}

class DefaultGetNasaImagesUseCase(
    private val repository: NasaImageRepository
) : GetNasaImagesUseCase {
    override fun execute(query: String, page: Int?): Single<NasaImagesResult> = repository.getImages(query, page)
}