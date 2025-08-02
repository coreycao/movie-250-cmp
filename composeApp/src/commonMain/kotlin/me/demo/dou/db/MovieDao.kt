package me.demo.dou.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

/**
 * @author Yeung
 * @date 2025/8/2
 */

@Dao
interface MovieDao {
    @Insert
    suspend fun insert(item: MovieEntity)

    @Insert(onConflict = REPLACE)
    suspend fun insertList(movies: List<MovieEntity>): List<Long>

    @Query("DELETE FROM MovieEntity")
    suspend fun deleteAll()

    @Transaction
    suspend fun replaceAll(movies: List<MovieEntity>) {
        deleteAll()
        insertList(movies)
    }

    @Query("SELECT * FROM MovieEntity")
    fun getAllAsFlow(): Flow<List<MovieEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM MovieEntity LIMIT 1)")
    suspend fun isNotEmpty(): Boolean
}