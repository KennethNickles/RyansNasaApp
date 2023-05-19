package com.ryansama.workdaynasaapp.presentation.viewdata

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class ImageItemViewState(
    val imageUrl: String? = null,
    val title: String? = null,
    val photographer: String? = null,
    val description: String? = null,
    val date: String? = null
) : Parcelable