package com.ryansama.workdaynasaapp.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ryansama.workdaynasaapp.data.api.NasaImageApi
import com.ryansama.workdaynasaapp.data.repository.DefaultNasaImageRepository
import com.ryansama.workdaynasaapp.domain.usecase.DefaultGetNasaImagesUseCase
import com.ryansama.workdaynasaapp.domain.usecase.GetNasaImagesUseCase
import com.ryansama.workdaynasaapp.presentation.viewmodel.ImageListViewModel
import com.ryansama.workdaynasaapp.presentation.viewmodel.NasaImagePresentationMapper
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class AppContainer(
    private val applicationContext: Context
) {

    private val nasaImageApi: NasaImageApi by lazy {
        Retrofit
            .Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://images-api.nasa.gov/")
            .build()
            .create()
    }

    private val nasaImagesRepository by lazy {
        DefaultNasaImageRepository(nasaImageApi)
    }
    private val getNasaImagesUseCase by lazy {
        DefaultGetNasaImagesUseCase(nasaImagesRepository)
    }

    private val nasaImagePresentationMapper by lazy { NasaImagePresentationMapper(applicationContext) }

    val imageListViewModelFactory = ImageListViewModelFactory(getNasaImagesUseCase, nasaImagePresentationMapper)
}

@Suppress("UNCHECKED_CAST")
class ImageListViewModelFactory(
    private val getNasaImagesUseCase: GetNasaImagesUseCase,
    private val nasaImagesPresentationMapper: NasaImagePresentationMapper
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ImageListViewModel(nasaImagesPresentationMapper, getNasaImagesUseCase) as T
    }
}
