package com.ryansama.workdaynasaapp.domain.model

import java.time.Instant

class NasaImagesResult(
    val items: List<NasaImage> = emptyList(),
    val nextPage: Int? = null
)

class NasaImage(
    val title: String? = null,
    val imageUrl: String? = null,
    val photographer: String? = null,
    val description: String? = null,
    val dateCreated: Instant? = null
)