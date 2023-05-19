package com.ryansama.workdaynasaapp

import com.ryansama.workdaynasaapp.domain.model.NasaImage
import com.ryansama.workdaynasaapp.domain.model.NasaImagesResult
import com.ryansama.workdaynasaapp.domain.usecase.GetNasaImagesUseCase
import com.ryansama.workdaynasaapp.presentation.viewdata.ImageItemViewState
import com.ryansama.workdaynasaapp.presentation.viewmodel.ImageListViewModel
import com.ryansama.workdaynasaapp.presentation.viewmodel.NasaImagePresentationMapper
import io.reactivex.Single
import org.junit.Test

import org.junit.Before
import org.junit.Rule
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.whenever

class ImageListViewModelTest {
    private lateinit var viewModel: ImageListViewModel

    @Mock
    private lateinit var getNasaImagesUseCase: GetNasaImagesUseCase

    @Mock
    private lateinit var presentationMapper: NasaImagePresentationMapper

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = ImageListViewModel(presentationMapper, getNasaImagesUseCase)
    }

    @Test
    fun `viewmodel sets loading states on search`() {
        // given
        whenever(getNasaImagesUseCase.execute(any(), anyOrNull())).thenReturn(Single.just(getDummyResponse()))
        whenever(presentationMapper.mapToPresentation(any())).thenReturn(mock(ImageItemViewState::class.java))

        val viewStates = viewModel.viewState.test()

        // when
        viewModel.process(ImageListViewModel.ViewIntent.Search("mars"))

        // then
        viewStates.run {
            assertValueAt(0) { !it.isLoading }
            assertValueAt(1) { it.isLoading }
            assertValueAt(2) { !it.isLoading }
        }
    }

    @Test
    fun `viewmodel clears existing items on search`() {
        // given
        whenever(getNasaImagesUseCase.execute(any(), anyOrNull())).thenReturn(Single.just(getDummyResponse()))
        whenever(presentationMapper.mapToPresentation(any())).thenReturn(mock(ImageItemViewState::class.java))

        val viewStates = viewModel.viewState.test()

        // when
        viewModel.process(ImageListViewModel.ViewIntent.Search("mars"))

        // then
        viewStates.run {
            assertValueAt(1) { it.items.isEmpty() }
            assertValueAt(2) { it.items.isNotEmpty() }
        }
    }

    @Test
    fun `viewmodel sets loading states on pagination`() {
        // given
        whenever(getNasaImagesUseCase.execute(any(), anyOrNull())).thenReturn(Single.just(getDummyResponse()))
        whenever(presentationMapper.mapToPresentation(any())).thenReturn(mock(ImageItemViewState::class.java))

        val viewStates = viewModel.viewState.test()

        // when
        viewModel.process(ImageListViewModel.ViewIntent.Paginate(2, "saturn"))

        // then
        viewStates.run {
            assertValueAt(0) { !it.isLoading }
            assertValueAt(1) { it.isLoading }
            assertValueAt(2) { !it.isLoading }
        }
    }

    @Test
    fun `viewmodel sets search bar text on text update`() {
        // given
        whenever(getNasaImagesUseCase.execute(any(), anyOrNull())).thenReturn(Single.just(getDummyResponse()))
        whenever(presentationMapper.mapToPresentation(any())).thenReturn(mock(ImageItemViewState::class.java))

        val viewStates = viewModel.viewState.test()

        // when
        viewModel.process(ImageListViewModel.ViewIntent.UpdateSearchQuery("jupiter"))

        // then
        viewStates.run {
            assertValueAt(0) { it.searchBarText == "" }
            assertValueAt(1) { it.searchBarText == "jupiter" }
        }
    }

    @Test
    fun `viewmodel appends new set of items on pagination complete`() {
        // given
        whenever(getNasaImagesUseCase.execute(any(), anyOrNull())).thenReturn(Single.just(getDummyResponse()))
        whenever(presentationMapper.mapToPresentation(any())).thenReturn(mock(ImageItemViewState::class.java))

        val viewStates = viewModel.viewState.test()

        // when
        viewModel.process(ImageListViewModel.ViewIntent.Search("saturn"))
        viewModel.process(ImageListViewModel.ViewIntent.Paginate(2, "saturn"))

        // then
        viewStates.run {
            assertValueAt(0) { it.items.isEmpty() }
            assertValueAt(1) { it.items.isEmpty() }
            assertValueAt(2) { it.items.size == 3 }
            assertValueAt(3) { it.items.size == 3 }
            assertValueAt(4) { it.items.size == 6 }
        }
    }

    @Test
    fun `viewmodel updates current query on search complete`() {
        // given
        whenever(getNasaImagesUseCase.execute(any(), anyOrNull())).thenReturn(Single.just(getDummyResponse()))
        whenever(presentationMapper.mapToPresentation(any())).thenReturn(mock(ImageItemViewState::class.java))

        val viewStates = viewModel.viewState.test()

        // when
        viewModel.process(ImageListViewModel.ViewIntent.Search("saturn"))

        // then
        viewStates.run {
            assertValueAt(0) { it.currentQuery == "earth" }
            assertValueAt(1) { it.currentQuery == "earth" }
            assertValueAt(2) { it.currentQuery == "saturn" }
        }
    }

    @Test
    fun `viewmodel sets loading states on search error`() {
        // given
        whenever(getNasaImagesUseCase.execute(any(), anyOrNull())).thenReturn(Single.error(Exception()))
        whenever(presentationMapper.mapToPresentation(any())).thenReturn(mock(ImageItemViewState::class.java))

        val viewStates = viewModel.viewState.test()

        // when
        viewModel.process(ImageListViewModel.ViewIntent.Search("mars"))

        // then
        viewStates.run {
            assertValueAt(0) { !it.isLoading }
            assertValueAt(1) { it.isLoading }
            assertValueAt(2) { !it.isLoading }
        }
    }

    @Test
    fun `viewmodel sets item states on successful content load`() {
        // given
        whenever(getNasaImagesUseCase.execute(any(), anyOrNull())).thenReturn(Single.just(getDummyResponse()))
        whenever(presentationMapper.mapToPresentation(any())).thenReturn(
            ImageItemViewState(
                title = "The sun",
                photographer = "Homer Simpson",
                imageUrl = "nasaimages.com/sun",
                description = "doh",
                date = "Jan 12"
            )
        )

        val viewStates = viewModel.viewState.test()

        // when
        viewModel.process(ImageListViewModel.ViewIntent.Search("the sun"))

        // then
        viewStates.run {
            assertValueAt(0) { it.items.isEmpty() }
            assertValueAt(1) { it.items.isEmpty() }
            assertValueAt(2) { it.items[0].title == "The sun" }
            assertValueAt(2) { it.items[0].photographer == "Homer Simpson" }
            assertValueAt(2) { it.items[0].imageUrl == "nasaimages.com/sun" }
            assertValueAt(2) { it.items[0].description == "doh" }
            assertValueAt(2) { it.items[0].date == "Jan 12" }
        }
    }

    @Test
    fun `viewmodel emits error effect on content load error`() {
        // given
        whenever(getNasaImagesUseCase.execute(any(), anyOrNull())).thenReturn(Single.error(Exception()))
        val errorMessage = "Something went wrong!"
        whenever(presentationMapper.getErrorMessage()).thenReturn(errorMessage)

        val viewEffects = viewModel.viewEffects.test()

        // when
        viewModel.process(ImageListViewModel.ViewIntent.Search("jupiter"))

        // then
        viewEffects.run {
            assertValueAt(0) { it is ImageListViewModel.ViewEffect.None }
            assertValueAt(1) { (it as ImageListViewModel.ViewEffect.ShowError).message == errorMessage }
        }
    }

    private fun getDummyResponse() = NasaImagesResult(
        items = listOf(
            NasaImage("Some image 1"),
            NasaImage("Some image 2"),
            NasaImage("Some image 3")
        ),
        nextPage = 2
    )
}