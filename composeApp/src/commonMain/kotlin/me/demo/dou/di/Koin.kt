package me.demo.dou.di

import me.demo.dou.data.MovieRepository
import me.demo.dou.net.MovieApi
import me.demo.dou.net.sharedHttpClient
import me.demo.dou.ui.HomeViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

/**
 * @author Yeung
 * @date 2025/8/1
 */

fun initKoin() {
    startKoin {
        modules(
            networkModule,
            viewModelModule,
            repositoryModule
        )
    }
}

val networkModule = module {
    single { sharedHttpClient }
    single { MovieApi(get()) }
}

val viewModelModule = module {
    factoryOf(::HomeViewModel)
}

val repositoryModule = module {
    single { MovieRepository(get()) }
}