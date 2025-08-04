package me.demo.dou.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.touchlab.kermit.Logger
import coil3.compose.AsyncImage
import me.demo.dou.data.Movie
import me.demo.dou.net.NetworkStatus
import movie250.composeapp.generated.resources.Res
import movie250.composeapp.generated.resources.fetch_data_error
import movie250.composeapp.generated.resources.network_offline
import movie250.composeapp.generated.resources.no_data_available
import movie250.composeapp.generated.resources.retry
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * @author Yeung
 * @date 2025/8/1
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {

    val log = remember { Logger.withTag("HomeScreen") }

    LaunchedEffect(Unit) {
        log.d { "HomeScreen launched" }
    }

    val homeViewModel = koinViewModel<HomeViewModel>()

    val netState = homeViewModel.netState.collectAsStateWithLifecycle()

    val homeUiState by homeViewModel.homeUiState.collectAsStateWithLifecycle()

    val gridListState = homeViewModel.gridListState

    val isFABVisible by remember(gridListState) {
        derivedStateOf {
            gridListState.firstVisibleItemScrollOffset > 0
        }
    }

    when (homeUiState) {
        is UiState.Success -> {
            val movieList = (homeUiState as UiState.Success).movies

            val snackbarHostState = remember { SnackbarHostState() }

            LaunchedEffect(snackbarHostState) {
                homeViewModel.effect.collect { effect ->
                    when (effect) {
                        is Effect.Toast -> {
                            snackbarHostState.showSnackbar(effect.message)
                        }
                    }
                }
            }

            Scaffold(
                modifier = modifier,
                snackbarHost = {
                    SnackbarHost(snackbarHostState)
                },

                floatingActionButton = {
                    if (isFABVisible) {
                        FloatingActionButton(onClick = {
                            homeViewModel.scrollToTop()
                        }) {
                            Text("Top")
                        }
                    }
                }) {
                PullToRefreshBox(
                    modifier = Modifier.fillMaxSize(),
                    isRefreshing = homeViewModel.isRefreshing,
                    onRefresh = homeViewModel::refresh
                ) {
                    MovieGridList(
                        state = gridListState,
                        modifier = Modifier.fillMaxSize(),
                        movieList = movieList
                    )

                    if (netState.value == NetworkStatus.Disconnected) {
                        log.d { "Network is offline" }
                        TopToast(
                            modifier = Modifier.background(Color.Red),
                            message = stringResource(Res.string.network_offline)
                        )
                    } else {
                        log.d { "Network is available" }
                    }
                }
            }
        }

        is UiState.Loading -> {
            log.d { "Loading movies..." }
            LoadingScreen(modifier = modifier)
        }

        is UiState.Error -> {
            log.e { "Error fetching movies: ${(homeUiState as UiState.Error).message}" }
            ErrorScreen(
                modifier = modifier,
                errorMessage = (homeUiState as UiState.Error).message
            ) {
                homeViewModel.refresh()
            }
        }

        is UiState.Empty -> {
            log.d { "Movie list is empty, showing empty state" }
            EmptyScreen(modifier = modifier)
        }
    }
}

@Composable
fun TopToast(
    modifier: Modifier = Modifier,
    message: String,
) {
    Box(
        modifier = modifier.fillMaxWidth().padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
fun MovieGridList(
    modifier: Modifier = Modifier,
    movieList: List<Movie>,
    state: LazyGridState
) {
    LazyVerticalGrid(
        state = state,
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = WindowInsets.safeDrawing.asPaddingValues(),
    ) {
        items(movieList, key = { it.id }) { movie ->
            MovieCard(movie = movie)
        }
    }
}

@Composable
fun MovieCard(
    modifier: Modifier = Modifier,
    movie: Movie,
    onClick: () -> Unit = {}
) {
    Column(
        modifier
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = movie.pic,
            contentDescription = movie.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(Color.LightGray),
        )

        Spacer(Modifier.height(2.dp))

        Text(movie.name, style = MaterialTheme.typography.titleMedium)
        Text(movie.score, style = MaterialTheme.typography.bodyMedium)
    }
}


@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
fun EmptyScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            stringResource(Res.string.no_data_available),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun ErrorScreen(modifier: Modifier = Modifier, errorMessage: String? = null, onClick: () -> Unit) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = {
                onClick()
            }, shape = MaterialTheme.shapes.extraLarge) {
                Text(
                    text = stringResource(Res.string.retry),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(

                text = errorMessage ?: stringResource(Res.string.fetch_data_error),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}