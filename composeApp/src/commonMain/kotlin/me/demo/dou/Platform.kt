package me.demo.dou

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform