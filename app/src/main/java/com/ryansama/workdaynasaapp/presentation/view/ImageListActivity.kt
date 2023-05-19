package com.ryansama.workdaynasaapp.presentation.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.rxjava2.subscribeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ryansama.workdaynasaapp.R
import com.ryansama.workdaynasaapp.WorkdayNasaApplication
import com.ryansama.workdaynasaapp.presentation.theme.Typography
import com.ryansama.workdaynasaapp.presentation.theme.WorkdayNasaAppTheme
import com.ryansama.workdaynasaapp.presentation.viewmodel.ImageListViewModel

@OptIn(ExperimentalMaterial3Api::class)
class ImageListActivity : ComponentActivity() {
    private val viewModel: ImageListViewModel by viewModels {
        (application as WorkdayNasaApplication).appContainer.imageListViewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val state = viewModel.viewState.subscribeAsState(initial = ImageListViewModel.ViewState())
            val effects = viewModel.viewEffects.subscribeAsState(initial = ImageListViewModel.ViewEffect.None)
            val focusManager = LocalFocusManager.current
            val snackbarHostState = remember { SnackbarHostState() }

            WorkdayNasaAppTheme {
                Scaffold(
                    topBar = {
                        TopBar(state, focusManager)
                    },
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { innerPadding ->
                    when (state.value.items.isEmpty() && !state.value.isLoading) {
                        true -> EmptyState()
                        false -> ImagesList(innerPadding, state.value)
                    }
                }
            }

            HandleEffects(effects, snackbarHostState)

            LaunchedEffect(Unit) {
                // Check due to LaunchedEffect recomposing on configuration changes -- there may be a better way to prevent unnecessary loading
                if (!state.value.hasInitialLoadCompleted) viewModel.process(ImageListViewModel.ViewIntent.Search(state.value.currentQuery))
            }
        }
    }

    @Composable
    private fun TopBar(
        state: State<ImageListViewModel.ViewState>,
        focusManager: FocusManager,
        modifier: Modifier = Modifier
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SearchBar(
                modifier = Modifier.padding(bottom = 12.dp),
                query = state.value.searchBarText,
                onQueryChange = {
                    viewModel.process(
                        ImageListViewModel.ViewIntent.UpdateSearchQuery(it)
                    )
                },
                onSearch = {
                    viewModel.process(ImageListViewModel.ViewIntent.Search(it))
                    focusManager.clearFocus()
                },
                active = false,
                onActiveChange = {},
                placeholder = { Text(stringResource(id = R.string.list_search_placeholder)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            ) {}

            if (state.value.isLoading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
    }

    @Composable
    private fun ImagesList(
        innerPadding: PaddingValues,
        state: ImageListViewModel.ViewState,
        modifier: Modifier = Modifier
    ) {
        val lastIndex = state.items.lastIndex
        Column(modifier = modifier.padding(innerPadding)) {
            LazyColumn {
                itemsIndexed(state.items) { index, viewState  ->
                    ImageItem(
                        viewState = viewState,
                        onClick = { viewModel.process(ImageListViewModel.ViewIntent.TapItem(viewState)) }
                    )
                    if (lastIndex == index && state.nextPage != null) {
                        LaunchedEffect(Unit) {
                            viewModel.process(ImageListViewModel.ViewIntent.Paginate(state.nextPage, state.currentQuery))
                        }
                    }
                }
            }
        }
    }


    @Composable
    private fun EmptyState(
        modifier: Modifier = Modifier
    ) {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painterResource(
                    id = R.drawable.satellite
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(150.dp)
                    .padding(bottom = 16.dp)
            )
            Text(
                text = stringResource(id = R.string.list_empty_msg),
                style = Typography.headlineSmall
            )
        }
    }

    @Composable
    private fun HandleEffects(effects: State<ImageListViewModel.ViewEffect>, snackbarHostState: SnackbarHostState) {
        when (val effect = effects.value) {
            is ImageListViewModel.ViewEffect.ShowError -> {
                LaunchedEffect(snackbarHostState) { snackbarHostState.showSnackbar(effect.message) }
            }
            is ImageListViewModel.ViewEffect.OpenDetails -> {
                startActivity(ImageDetailActivity.newIntent(this, effect.viewState))
            }
            ImageListViewModel.ViewEffect.None -> {}
        }
    }
}