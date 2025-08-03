package me.demo.dou.di

import me.demo.dou.db.createDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

/**
 * @author Yeung
 * @date 2025/8/2
 */
actual fun platformModule(): Module {
    return module {
        single { createDatabase(androidContext()) }
    }
}

fun initKoinAndroid(koinAppDeclaration: KoinAppDeclaration) {
    startKoin {
        koinAppDeclaration()
        modules(sharedModules())
    }
}