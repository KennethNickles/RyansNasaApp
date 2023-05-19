package com.ryansama.workdaynasaapp.data.dto

import com.google.gson.annotations.SerializedName

class NasaImagesRootDTO(
    @SerializedName("collection") val collection: NasaCollectionDTO? = null
)

class NasaCollectionDTO(
    @SerializedName("items") val items: List<NasaImageDTO>? = null,
    @SerializedName("links") val links: List<LinkDTO>? = null,
)

class NasaImageDTO(
    @SerializedName("data") val data: List<DataDTO>? = null,
    @SerializedName("links") val links: List<LinkDTO>? = null,
)

class DataDTO(
    @SerializedName("description") val description: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("photographer") val photographer: String? = null,
    @SerializedName("date_created") val dateCreated: String? = null
)

class LinkDTO(
    @SerializedName("href") val href: String? = null,
    @SerializedName("rel") val rel: String? = null
)