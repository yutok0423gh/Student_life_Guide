package com.chujichengnianren.guide.di

import android.content.Context
import androidx.room.Room
import com.chujichengnianren.guide.data.local.GuideDatabase
import com.chujichengnianren.guide.data.local.dao.ContentDao
import com.chujichengnianren.guide.data.local.dao.UserStateDao
import com.chujichengnianren.guide.data.repository.EpochMillisProvider
import com.chujichengnianren.guide.data.repository.GuideRepository
import com.chujichengnianren.guide.data.repository.LocalGuideRepository
import com.chujichengnianren.guide.data.repository.LocalUserStateRepository
import com.chujichengnianren.guide.data.repository.SystemEpochMillisProvider
import com.chujichengnianren.guide.data.repository.UserStateRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindGuideRepository(implementation: LocalGuideRepository): GuideRepository

    @Binds
    @Singleton
    abstract fun bindUserStateRepository(implementation: LocalUserStateRepository): UserStateRepository

    @Binds
    @Singleton
    abstract fun bindEpochMillisProvider(implementation: SystemEpochMillisProvider): EpochMillisProvider
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): GuideDatabase =
        Room.databaseBuilder(
            context,
            GuideDatabase::class.java,
            GuideDatabase.DATABASE_NAME,
        ).build()

    @Provides
    fun provideContentDao(database: GuideDatabase): ContentDao = database.contentDao()

    @Provides
    fun provideUserStateDao(database: GuideDatabase): UserStateDao = database.userStateDao()
}
