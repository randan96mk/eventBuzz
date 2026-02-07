package app.eventbuzz.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.eventbuzz.core.database.entity.EventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Query("SELECT * FROM events ORDER BY start_time ASC")
    fun getAll(): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE id = :eventId")
    fun getById(eventId: String): Flow<EventEntity?>

    @Query(
        """
        SELECT * FROM events
        WHERE latitude BETWEEN :minLat AND :maxLat
        AND longitude BETWEEN :minLng AND :maxLng
        ORDER BY start_time ASC
        """
    )
    fun getNearby(
        minLat: Double,
        maxLat: Double,
        minLng: Double,
        maxLng: Double,
    ): Flow<List<EventEntity>>

    @Query(
        """
        SELECT * FROM events
        WHERE title LIKE '%' || :query || '%'
        OR description LIKE '%' || :query || '%'
        ORDER BY start_time ASC
        """
    )
    fun search(query: String): Flow<List<EventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<EventEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: EventEntity)

    @Query("DELETE FROM events WHERE cached_at < :timestamp")
    suspend fun deleteOlderThan(timestamp: Long)

    @Query("DELETE FROM events")
    suspend fun deleteAll()
}
