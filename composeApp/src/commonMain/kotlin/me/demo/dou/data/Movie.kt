package me.demo.dou.data

import kotlinx.serialization.Serializable

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

/**
 *
 * {
 *     "rank": "1",
 *     "pic": "https://img3.doubanio.com/view/photo/s_ratio_poster/public/p480747492.jpg",
 *     "name": "肖申克的救赎",
 *     "link": "https://movie.douban.com/subject/1292052",
 *     "score": "9.7",
 *     "id": "1292052"
 *   }
 */