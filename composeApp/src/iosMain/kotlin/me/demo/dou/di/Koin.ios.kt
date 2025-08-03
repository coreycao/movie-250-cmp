package me.demo.dou.di

import me.demo.dou.db.createDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * @author Yeung
 * @date 2025/8/2
 */

actual fun platformModule(): Module {
    return module {
        single { createDatabase() }
    }
}

fun initKoinIos() {
    org.koin.core.context.startKoin {
        modules(sharedModules())
    }
}