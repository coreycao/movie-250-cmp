package me.demo.dou.net

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import me.demo.dou.data.Movie

/**
 * @author Yeung
 * @date 2025/8/1
 */

class MovieApi(private val httpClient: HttpClient) {

    companion object {
        private const val END_POINT = "https://raw.githubusercontent.com/coreycao/douban-movie-250-diff/main/recently_movie_250.json"
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun fetchMovieList(): Flow<Result<List<Movie>>> = flow {
        emit(runCatching {
            val response = httpClient.get(END_POINT)
            val json = Json {
                allowTrailingComma = true
            }
            json.decodeFromString(response.bodyAsText())
        })
    }
}