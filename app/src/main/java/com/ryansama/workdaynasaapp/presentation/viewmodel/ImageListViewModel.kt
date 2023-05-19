package com.ryansama.workdaynasaapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.ryansama.workdaynasaapp.domain.model.NasaImagesResult
import com.ryansama.workdaynasaapp.domain.usecase.GetNasaImagesUseCase
import com.ryansama.workdaynasaapp.presentation.viewdata.ImageItemViewState
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class ImageListViewModel(
    presentationMapper: NasaImagePresentationMapper,
    getNasaImagesUseCase: GetNasaImagesUseCase
): ViewModel() {
    val viewState: Flowable<ViewState>
    val viewEffects: Flowable<ViewEffect>

    private val intentSubject: PublishSubject<ViewIntent> = PublishSubject.create()
    private lateinit var disposable: Disposable

    init {
        val results: Flowable<ViewResult> =
            intentToResult(
                intents = intentSubject.toFlowable(BackpressureStrategy.LATEST),
                getNasaImagesUseCase = getNasaImagesUseCase
            ).share()

        viewState = results
            .resultToViewState(presentationMapper = presentationMapper)
            .replay(1)
            .autoConnect(1) { disposable = it }
        viewEffects = results.resultToViewEffect(presentationMapper = presentationMapper)
    }

    fun process(intent: ViewIntent) {
        intentSubject.onNext(intent)
    }

    override fun onCleared() {
        disposable.dispose()
        super.onCleared()
    }

    private fun intentToResult(
        intents: Flowable<ViewIntent>,
        getNasaImagesUseCase: GetNasaImagesUseCase
    ) = intents.flatMap { intent ->
        when (intent) {
            is ViewIntent.Search -> getNasaImagesUseCase.execute(intent.query)
                .toFlowable()
                .subscribeOn(Schedulers.io())
                .map {
                    ViewResult.ContentLoaded(it, intent.query) as ViewResult
                }
                .startWith(ViewResult.ContentLoading)
                .onErrorReturnItem(ViewResult.LoadingError)
            is ViewIntent.UpdateSearchQuery -> Flowable.just(ViewResult.SearchBarTextChanged(intent.query))
            is ViewIntent.TapItem -> Flowable.just(ViewResult.ItemTapped(intent.viewState))
            is ViewIntent.Paginate -> getNasaImagesUseCase.execute(intent.query, intent.page)
                .toFlowable()
                .subscribeOn(Schedulers.io())
                .map {
                    ViewResult.ContentLoaded(it, intent.query) as ViewResult
                }
                .startWith(ViewResult.Paginating)
                .onErrorReturnItem(ViewResult.LoadingError)
        }
    }

    private fun Flowable<ViewResult>.resultToViewState(presentationMapper: NasaImagePresentationMapper) = this.scan(ViewState()) { prevState, viewResult ->
        when (viewResult) {
            is ViewResult.ContentLoaded -> prevState.copy(
                items = prevState.items + viewResult.result.items.map { item -> presentationMapper.mapToPresentation(item) },
                isLoading = false,
                hasInitialLoadCompleted = true,
                nextPage = viewResult.result.nextPage,
                currentQuery = viewResult.query
            )
            ViewResult.ContentLoading -> prevState.copy(
                items = emptyList(),
                isLoading = true
            )
            ViewResult.Paginating -> prevState.copy(
                isLoading = true
            )
            ViewResult.LoadingError -> prevState.copy(
                isLoading = false,
                hasInitialLoadCompleted = true
            )
            is ViewResult.SearchBarTextChanged -> prevState.copy(
                searchBarText = viewResult.text
            )
            is ViewResult.ItemTapped -> prevState.copy()
        }
    }

    private fun Flowable<ViewResult>.resultToViewEffect(presentationMapper: NasaImagePresentationMapper) = this.map { viewResult ->
        when (viewResult) {
            ViewResult.LoadingError -> ViewEffect.ShowError(message = presentationMapper.getErrorMessage())
            is ViewResult.ItemTapped -> ViewEffect.OpenDetails(viewState = viewResult.viewState)
            else -> ViewEffect.None
        }
    }

    sealed class ViewIntent {
        class Search(val query: String): ViewIntent()
        class UpdateSearchQuery(val query: String) : ViewIntent()
        class TapItem(val viewState: ImageItemViewState) : ViewIntent()
        class Paginate(val page: Int, val query: String) : ViewIntent()
    }

    sealed class ViewResult {
        class ContentLoaded(val result: NasaImagesResult, val query: String): ViewResult()
        object ContentLoading : ViewResult()
        object Paginating : ViewResult()
        object LoadingError: ViewResult()
        class SearchBarTextChanged(val text: String) : ViewResult()
        class ItemTapped(val viewState: ImageItemViewState) : ViewResult()
    }

    sealed class ViewEffect {
        class ShowError(val message: String): ViewEffect()
        class OpenDetails(val viewState: ImageItemViewState) : ViewEffect()
        object None: ViewEffect()
    }

    data class ViewState(
        // Visible
        val items: List<ImageItemViewState> = emptyList(),
        val searchBarText: String = "",
        val isLoading: Boolean = false,

        // Invisible
        val currentQuery: String = "earth",
        val hasInitialLoadCompleted: Boolean = false,
        val nextPage: Int? = null
    )
}