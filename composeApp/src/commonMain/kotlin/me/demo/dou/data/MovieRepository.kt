package me.demo.dou.data

import co.touchlab.kermit.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flowOn
import me.demo.dou.db.MovieDao
import me.demo.dou.net.MovieApi

/**
 * @author Yeung
 * @date 2025/8/1
 */
class MovieRepository(private val movieApi: MovieApi, private val movieDao: MovieDao) {

    private val log = Logger.withTag("MovieRepo")

    sealed class Event {
        data class NetError(val error: Throwable) : Event()
    }

    private val _event = MutableSharedFlow<Event>()
    val state = _event.asSharedFlow()

    fun observeMovieList() = movieDao.getAllAsFlow().flowOn(Dispatchers.IO)

    suspend fun refresh() {
        val hasLocalCache = movieDao.isNotEmpty()
        if (hasLocalCache){
            log.d { "refresh, has local cache" }
        } else{
            movieApi.fetchMovieList().collect { result ->
                result.fold(
                    onSuccess = { movies ->
                        log.d { "refresh success, save to db" }
                        movieDao.replaceAll(movies.map { movie -> movie.toEntity() })
                    },
                    onFailure = {
                        log.d { "refresh failed" }
                        _event.emit(Event.NetError(it))
                    }
                )
            }
        }
    }
}