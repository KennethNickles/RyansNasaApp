package com.ryansama.workdaynasaapp.data.repository

import android.util.Log
import androidx.core.net.toUri
import com.ryansama.workdaynasaapp.data.api.NasaImageApi
import com.ryansama.workdaynasaapp.data.dto.NasaImagesRootDTO
import com.ryansama.workdaynasaapp.domain.model.NasaImage
import com.ryansama.workdaynasaapp.domain.model.NasaImagesResult
import com.ryansama.workdaynasaapp.domain.repository.NasaImageRepository
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.time.Instant

class DefaultNasaImageRepository(
    private val nasaImageApi: NasaImageApi
): NasaImageRepository {
    override fun getImages(query: String, page: Int?): Single<NasaImagesResult> =
        nasaImageApi.searchImages(query, page = page)
            .observeOn(Schedulers.io())
            .map { response ->
                response.takeIf { response.isSuccessful }?.let {
                    it.body()?.let { collection ->
                        mapToDomain(collection)
                    }
                } ?: throw Exception("Error retrieving images: ${response.errorBody()}")
            }
            .doOnError {
                Log.d("ERROR", it.message ?: "Something went wrong")
            }

    private fun mapToDomain(dto: NasaImagesRootDTO): NasaImagesResult =
        NasaImagesResult(
            items = dto.collection?.items?.map {
                NasaImage(
                    title = it.data?.firstOrNull()?.title,
                    photographer = it.data?.firstOrNull()?.photographer,
                    imageUrl = it.links?.firstOrNull()?.href,
                    description = it.data?.firstOrNull()?.description,
                    dateCreated = it.data?.firstOrNull()?.dateCreated?.let {  isoDate ->
                        Instant.parse(isoDate)
                    }
                )
            } ?: emptyList(),
            nextPage = dto.collection?.links?.firstOrNull { it.rel == "next" }?.href?.toUri()?.getQueryParameter("page")?.toInt()
        )
}