package me.demo.dou

import android.app.Application
import me.demo.dou.di.initKoin

/**
 * @author Yeung
 * @date 2025/8/2
 */
class MovieApp: Application(){
    override fun onCreate() {
        super.onCreate()
        // Initialize Koin or any other dependency injection framework here
         initKoin()
    }
}