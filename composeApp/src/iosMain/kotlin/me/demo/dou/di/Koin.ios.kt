package me.demo.dou.di

import me.demo.dou.db.createDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * @author Yeung
 * @date 2025/8/2
 */

actual fun platformModule(): Module {
    return databaseModule
}

val databaseModule = module {
    single { createDatabase() }
}