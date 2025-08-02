package me.demo.dou.data

import kotlinx.serialization.Serializable
import me.demo.dou.db.MovieEntity

/**
 * @author Yeung
 * @date 2025/8/1
 */
@Serializable
data class Movie(
    val rank: String,
    val pic: String,
    val name: String,
    val link: String,
    val score: String,
    val id: String
)

fun Movie.toEntity(): MovieEntity = MovieEntity(
    rank = this.rank,
    pic = this.pic,
    name = this.name,
    link = this.link,
    score = this.score,
    movieId = this.id
)