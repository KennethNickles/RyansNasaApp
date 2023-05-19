package com.ryansama.workdaynasaapp.presentation.view

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ryansama.workdaynasaapp.R
import com.ryansama.workdaynasaapp.presentation.theme.Typography
import com.ryansama.workdaynasaapp.presentation.theme.WorkdayNasaAppTheme
import com.ryansama.workdaynasaapp.presentation.viewdata.ImageItemViewState

@OptIn(ExperimentalMaterial3Api::class)
class ImageDetailActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(KEY_VIEW_STATE, ImageItemViewState::class.java)
        } else {
            intent.getParcelableExtra(KEY_VIEW_STATE)
        } ?: throw IllegalStateException("View state of type ${ImageItemViewState::class.simpleName} not found")

        setContent {
            WorkdayNasaAppTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {},
                            navigationIcon = {
                                IconButton(onClick = { this.finish() }) {
                                    Icon(Icons.Default.ArrowBack, contentDescription = null)
                                }
                            }
                        )
                    }
                ) { padding ->
                    Body(viewState = viewState, modifier = Modifier.padding(padding))
                }
            }
        }
    }

    @Composable
    fun Body(
        viewState: ImageItemViewState,
        modifier: Modifier = Modifier
    ) {
        val scrollState = rememberScrollState()
        Column(modifier = modifier
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp)
            .padding(bottom = 12.dp)
            .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            AsyncImage(
                modifier = Modifier
                    .clip(RoundedCornerShape(9.dp)),
                model = viewState.imageUrl,
                placeholder = painterResource(R.drawable.ic_earth),
                error = painterResource(R.drawable.ic_earth),
                contentDescription = null,
            )

            viewState.title?.let { Text(it, style = Typography.displaySmall) }
            viewState.photographer?.let { Text(it, style = Typography.titleLarge) }
            viewState.date?.let { Text(it, style = Typography.labelLarge) }
            viewState.description?.let { Text(it, style = Typography.bodyLarge) }
        }
    }

    @Composable
    @Preview
    fun BodyPreview() {
        WorkdayNasaAppTheme {
            Body(
                viewState = ImageItemViewState(
                    title = "Andromeda Galaxy",
                    photographer = "Ryan Samarajeewa",
                    description = "Some very cool place out in space ".repeat(20),
                    date = "March 23, 2023"
                ),
                modifier = Modifier.background(Color.White).fillMaxSize()
            )
        }
    }

    companion object {
        private const val KEY_VIEW_STATE = "VIEW_STATE"

        fun newIntent(context: Context, viewState: ImageItemViewState) =
            Intent(context, ImageDetailActivity::class.java).apply {
                putExtra(KEY_VIEW_STATE, viewState)
            }
    }
}