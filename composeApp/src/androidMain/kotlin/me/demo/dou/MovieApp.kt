package me.demo.dou

import android.app.Application
import me.demo.dou.di.initKoinAndroid
import org.koin.android.ext.koin.androidContext

/**
 * @author Yeung
 * @date 2025/8/2
 */
class MovieApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoinAndroid {
            androidContext(this@MovieApp)
        }
    }
}