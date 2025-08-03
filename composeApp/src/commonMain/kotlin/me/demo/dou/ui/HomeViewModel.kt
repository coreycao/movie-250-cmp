package me.demo.dou.ui

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.demo.dou.data.Movie
import me.demo.dou.data.MovieRepository
import me.demo.dou.data.RepoEvent
import me.demo.dou.db.toModel
import me.demo.dou.net.NetStateMonitor
import me.demo.dou.net.NetworkStatus

/**
 * @author Yeung
 * @date 2025/8/1
 */
class HomeViewModel(
    private val movieRepository: MovieRepository,
    private val netStateMonitor: NetStateMonitor
) : ViewModel() {

    private val log = Logger.withTag("HomeViewModel")

    private val _effect = MutableSharedFlow<Effect>()
    val effect = _effect.asSharedFlow()

    val netState = netStateMonitor.networkStatus
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = NetworkStatus.Connected
        )

    private suspend fun initData() {
        movieRepository.initMovieList().collect {
            when (it) {
                is RepoEvent.Success -> {
                    log.d {
                        "Movie list initialized successfully with ${it.movies.size} movies"
                    }
                }

                RepoEvent.None -> {
                    log.d {
                        "Movie list already has local cache, no need to fetch remote data"
                    }
                }

                RepoEvent.NetError -> {
                    log.e {
                        "Failed to initialize movie list due to network error"
                    }
                    _effect.emit(
                        Effect.Toast(message = "Failed to fetch movie list")
                    )
                }
            }
        }
    }

    init {
        viewModelScope.launch {
            log.d {
                "Initializing HomeViewModel, refreshing movie list"
            }
            initData()
        }
    }

    val gridListState = LazyGridState()

    fun scrollToTop() {
        viewModelScope.launch {
            gridListState.scrollToItem(index = 0)
        }
    }

    var isRefreshing by mutableStateOf(false)
        private set

    var refreshJob: Job? = null

    fun refresh() {
        if (refreshJob?.isActive == true) {
            log.d {
                "Refresh job is already running, skipping new refresh"
            }
            return
        }
        refreshJob = viewModelScope.launch {
            isRefreshing = true
            movieRepository.fetchRemoteMovieList().first()
                .fold(
                    onSuccess = {
                        isRefreshing = false
                        log.d {
                            "Successfully fetched movie list, updating UI"
                        }
                    }, onFailure = {
                        isRefreshing = false
                        log.e {
                            "Failed to fetch movie list: ${it.message}"
                        }
                        _effect.emit(
                            Effect.Toast(message = "Failed to fetch movie list")
                        )
                    }
                )
        }
    }

    val homeUiState = movieRepository.observeMovieList()
        .map { entities ->
            val movies = entities.map { it.toModel() }
            if (movies.isEmpty()) {
                log.d {
                    "Movie list is empty, showing empty state"
                }
                UiState.Empty
            } else {
                log.d {
                    "Fetched ${movies.size} movies successfully"
                }
                UiState.Success(movies)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState.Loading
        )
}

sealed class Effect {
    data class Toast(val message: String) : Effect()
}

sealed class UiState {
    data class Success(val movies: List<Movie>) : UiState()
    data class Error(val message: String?) : UiState()
    object Loading : UiState()
    object Empty : UiState()
}