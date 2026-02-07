package app.eventbuzz.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import app.eventbuzz.core.database.dao.EventDao
import app.eventbuzz.core.database.entity.CategoryEntity
import app.eventbuzz.core.database.entity.EventEntity

@Database(
    entities = [
        EventEntity::class,
        CategoryEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class EventBuzzDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
}
