package app.eventbuzz.core.database.di

import android.content.Context
import androidx.room.Room
import app.eventbuzz.core.database.EventBuzzDatabase
import app.eventbuzz.core.database.dao.EventDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): EventBuzzDatabase {
        return Room.databaseBuilder(
            context,
            EventBuzzDatabase::class.java,
            "eventbuzz.db",
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideEventDao(database: EventBuzzDatabase): EventDao {
        return database.eventDao()
    }
}
