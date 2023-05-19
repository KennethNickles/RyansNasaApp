package com.ryansama.workdaynasaapp.presentation.viewmodel

import android.content.Context
import com.ryansama.workdaynasaapp.R
import com.ryansama.workdaynasaapp.domain.model.NasaImage
import com.ryansama.workdaynasaapp.presentation.viewdata.ImageItemViewState
import java.time.format.DateTimeFormatter
import java.util.TimeZone

class NasaImagePresentationMapper(
    private val context: Context
) {
    fun getErrorMessage() = context.getString(R.string.list_error_msg)

    fun mapToPresentation(nasaImage: NasaImage) = ImageItemViewState(
        imageUrl = nasaImage.imageUrl,
        title = nasaImage.title,
        photographer = nasaImage.photographer,
        description = nasaImage.description,
        date = nasaImage.dateCreated?.let {
            DateTimeFormatter.ofPattern("LLLL dd, yyyy")
                .withZone(TimeZone.getDefault().toZoneId())
                .format(it)
        }
    )
}
