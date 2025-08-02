package me.demo.dou.data

import me.demo.dou.net.MovieApi

/**
 * @author Yeung
 * @date 2025/8/1
 */
class MovieRepository(private val movieApi: MovieApi) {

    fun observeMovieList() = movieApi.fetchMovieList()
    
}