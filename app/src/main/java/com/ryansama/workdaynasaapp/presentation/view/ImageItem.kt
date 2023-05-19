package com.ryansama.workdaynasaapp.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ryansama.workdaynasaapp.R
import com.ryansama.workdaynasaapp.presentation.theme.Typography
import com.ryansama.workdaynasaapp.presentation.theme.WorkdayNasaAppTheme
import com.ryansama.workdaynasaapp.presentation.viewdata.ImageItemViewState

@Composable
fun ImageItem(
    viewState: ImageItemViewState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = viewState.imageUrl,
            placeholder = painterResource(R.drawable.ic_earth),
            error = painterResource(R.drawable.ic_earth),
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(6.dp))
                .aspectRatio(1f),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.align(Alignment.CenterVertically),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            viewState.title?.let {
                Text(
                    text = it,
                    style = Typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            viewState.photographer?.let {
                Text(
                    text = it,
                    style = Typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview
@Composable
fun ImageItemPreview() {
    WorkdayNasaAppTheme {
        ImageItem(
            viewState = ImageItemViewState(
                imageUrl = "https://images-assets.nasa.gov/image/as11-40-5874/as11-40-5874~thumb.jpg",
                title = "Andromeda Galaxy",
                photographer = "Ryan Samarajeewa"
            ),
            modifier = Modifier.width(1080.dp).background(Color.White),
            onClick = {}
        )
    }
}

@Preview
@Composable
fun ImageItemLongTextPreview() {
    WorkdayNasaAppTheme {
        ImageItem(
            viewState = ImageItemViewState(
                imageUrl = "https://images-assets.nasa.gov/image/as11-40-5874/as11-40-5874~thumb.jpg",
                title = "Some long title ".repeat(10),
                photographer = "Ryan Samarajeewa ".repeat(10)
            ),
            modifier = Modifier.width(1080.dp).background(Color.White),
            onClick = {}
        )
    }
}