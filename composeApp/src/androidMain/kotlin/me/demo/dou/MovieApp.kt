package me.demo.dou

import android.app.Application
import me.demo.dou.di.commonModule
import me.demo.dou.di.databaseModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

/**
 * @author Yeung
 * @date 2025/8/2
 */
class MovieApp: Application(){
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MovieApp)
            modules(
                databaseModule,
                commonModule
            )
        }
    }
}