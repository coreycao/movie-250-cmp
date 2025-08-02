package me.demo.dou.db

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import co.touchlab.kermit.Logger
import kotlinx.coroutines.Dispatchers

/**
 * @author Yeung
 * @date 2025/8/2
 */

fun createDatabase(ctx: Context): AppDatabase {
    Logger.d{"create database on Android"}
    val appContext = ctx.applicationContext
    val dbFile = appContext.getDatabasePath(DB_FILE_NAME)
    val builder = Room.databaseBuilder<AppDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
    return builder.setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}