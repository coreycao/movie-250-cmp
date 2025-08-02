package me.demo.dou.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import me.demo.dou.data.Movie
import me.demo.dou.data.MovieRepository

/**
 * @author Yeung
 * @date 2025/8/1
 */
class HomeViewModel(private val movieRepository: MovieRepository) : ViewModel() {

    val homeUiState = movieRepository.observeMovieList()
        .map { result ->
            result.fold(
                onSuccess = { movies ->
                    if (movies.isEmpty()) {
                        UiState.Empty
                    } else {
                        UiState.Success(movies)
                    }
                },
                onFailure = { exception ->
                    UiState.Error(exception.message)
                }
            )
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