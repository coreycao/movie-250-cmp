package me.demo.dou.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import me.demo.dou.data.Movie

/**
 * @author Yeung
 * @date 2025/8/2
 */
@Entity
data class MovieEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val rank: String,
    val pic: String,
    val name: String,
    val link: String,
    val score: String,
    val movieId: String
)

fun MovieEntity.toModel(): Movie = Movie(
    rank = this.rank,
    pic = this.pic,
    name = this.name,
    link = this.link,
    score = this.score,
    id = this.movieId
)