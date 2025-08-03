package me.demo.dou.data

import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import me.demo.dou.db.MovieDao
import me.demo.dou.net.MovieApi

/**
 * @author Yeung
 * @date 2025/8/1
 */
class MovieRepository(private val movieApi: MovieApi, private val movieDao: MovieDao) {

    private val log = Logger.withTag("MovieRepo")

    fun observeMovieList() = movieDao.getAllAsFlow()

    fun fetchRemoteMovieList() = movieApi.fetchMovieList()
        .onEach { result ->
            result.fold(
                onSuccess = { movies->
                    log.d { "fetchRemoteMovieList success, save to db" }
                    movieDao.replaceAll(movies.map { movie -> movie.toEntity() })
                },
                onFailure = { error ->
                    log.d { "fetchRemoteMovieList failed" }
                }
            )
        }

    suspend fun initMovieList() {
        val hasLocalCache = movieDao.isNotEmpty()
        if (hasLocalCache) {
            log.d { "init movie list, has local cache" }
        } else {
            log.d { "init movie list, no local cache, fetching remote data" }
            fetchRemoteMovieList().first()
        }
    }
}