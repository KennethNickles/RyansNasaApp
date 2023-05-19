package com.ryansama.workdaynasaapp

import com.ryansama.workdaynasaapp.data.api.NasaImageApi
import com.ryansama.workdaynasaapp.data.dto.DataDTO
import com.ryansama.workdaynasaapp.data.dto.LinkDTO
import com.ryansama.workdaynasaapp.data.dto.NasaCollectionDTO
import com.ryansama.workdaynasaapp.data.dto.NasaImageDTO
import com.ryansama.workdaynasaapp.data.dto.NasaImagesRootDTO
import com.ryansama.workdaynasaapp.data.repository.DefaultNasaImageRepository
import io.reactivex.Single
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.whenever
import retrofit2.Response
import java.time.Instant

class DefaultNasaImageRepositoryTest {
    lateinit var repository: DefaultNasaImageRepository

    @Mock
    lateinit var api: NasaImageApi

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        repository = DefaultNasaImageRepository(api)
    }

    @Test
    fun `repository maps dto to domain model`() {
        // given
        val title = "Black hole"
        val description = "yikes"
        val photographer = "Homer Simpson"
        val dateCreated = "1996-08-29T17:29:40Z"
        val href = "nasa.com/blackhole"
        val response = Single.just(
            Response.success(
                NasaImagesRootDTO(
                    collection = NasaCollectionDTO(
                        items = listOf(
                            NasaImageDTO(
                                data = listOf(
                                    DataDTO(
                                        description = description,
                                        title = title,
                                        photographer = photographer,
                                        dateCreated = dateCreated
                                    )
                                ),
                                links = listOf(
                                    LinkDTO(
                                        href = href
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )
        whenever(api.searchImages(any(), any(), anyOrNull())).thenReturn(response)

        // when
        val result = repository.getImages("something").blockingGet()

        // then
        result.items[0].run {
            assert(this.title == title)
            assert(this.description == description)
            assert(this.photographer == photographer)
            assert(this.dateCreated == Instant.parse(dateCreated))
            assert(this.imageUrl == href)
        }
    }

    @Test
    fun `repository returns empty list when api sends empty list response`() {
        // given
        whenever(api.searchImages(any(), any(), anyOrNull())).thenReturn(Single.just(Response.success(NasaImagesRootDTO(null))))

        // when
        val result = repository.getImages("something").blockingGet()

        // then
        assert(result.items.isEmpty())
    }

    @Test
    fun `repository throws exception when api sends error response`() {
        // given
        whenever(api.searchImages(any(), any(), anyOrNull())).thenReturn(Single.just(Response.error(404, "yikes".toResponseBody())))

        // then
        Assert.assertThrows(Exception::class.java) { repository.getImages("something").blockingGet() }
    }

    @Test
    fun `repository throws exception when failing to parse date`() {
        // given
        val dateCreated = "19ferwfw"
        val response = Single.just(
            Response.success(
                NasaImagesRootDTO(
                    collection = NasaCollectionDTO(
                        items = listOf(
                            NasaImageDTO(
                                data = listOf(
                                    DataDTO(
                                        dateCreated = dateCreated
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )
        whenever(api.searchImages(any(), any(), anyOrNull())).thenReturn(response)

        // then
        Assert.assertThrows(Exception::class.java) { repository.getImages("something").blockingGet() }
    }
}