package app.eventbuzz.di

import app.eventbuzz.data.FakeEventRepository
import app.eventbuzz.domain.repository.EventRepository
import app.eventbuzz.domain.usecase.GetEventDetailUseCase
import app.eventbuzz.domain.usecase.GetNearbyEventsUseCase
import app.eventbuzz.domain.usecase.SearchEventsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideEventRepository(
        fakeRepository: FakeEventRepository,
    ): EventRepository {
        return fakeRepository
    }

    @Provides
    @Singleton
    fun provideGetNearbyEventsUseCase(
        repository: EventRepository,
    ): GetNearbyEventsUseCase {
        return GetNearbyEventsUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideSearchEventsUseCase(
        repository: EventRepository,
    ): SearchEventsUseCase {
        return SearchEventsUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetEventDetailUseCase(
        repository: EventRepository,
    ): GetEventDetailUseCase {
        return GetEventDetailUseCase(repository)
    }
}
