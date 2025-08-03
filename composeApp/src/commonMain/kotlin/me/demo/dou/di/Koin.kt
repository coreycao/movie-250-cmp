package me.demo.dou.di

import co.touchlab.kermit.Logger
import me.demo.dou.data.MovieRepository
import me.demo.dou.db.AppDatabase
import me.demo.dou.db.MovieDao
import me.demo.dou.net.MovieApi
import me.demo.dou.net.sharedHttpClient
import me.demo.dou.ui.HomeViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * @author Yeung
 * @date 2025/8/1
 */

expect fun platformModule(): Module

val commonModule = module {
    single { sharedHttpClient }
    single { MovieApi(get()) }

    viewModel { HomeViewModel(get()) }

    single { MovieRepository(get(), get()) }

    single { provideMovieDao(get()) }
}

private fun provideMovieDao(appDatabase: AppDatabase): MovieDao{
    return appDatabase.movieDao()
}

// just invoked on iOS
fun initKoin() {
    Logger.i {
        "Initializing Koin"
    }
    startKoin {
        modules(
            platformModule(),
            commonModule
        )
    }
}