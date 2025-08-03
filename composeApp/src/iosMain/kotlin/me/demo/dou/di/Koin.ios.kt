package me.demo.dou.di

import me.demo.dou.db.createDatabase
import me.demo.dou.net.NetworkHelper
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * @author Yeung
 * @date 2025/8/2
 */

actual fun platformModule(): Module {
    return module {
        single { createDatabase() }
        single<NetworkHelper> { get<IosApplicationComponent>().networkHelper }
    }
}

fun initKoinIos(appComponent: IosApplicationComponent) {
    startKoin {
        modules(sharedModules() + module { single { appComponent } })
    }
}

class IosApplicationComponent(val networkHelper: NetworkHelper)