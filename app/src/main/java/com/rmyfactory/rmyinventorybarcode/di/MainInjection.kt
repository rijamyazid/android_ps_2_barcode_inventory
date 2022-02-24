package com.rmyfactory.rmyinventorybarcode.di

import android.content.Context
import androidx.room.Room
import com.rmyfactory.rmyinventorybarcode.model.data.local.LocalDataSource
import com.rmyfactory.rmyinventorybarcode.model.data.local.dao.ItemDao
import com.rmyfactory.rmyinventorybarcode.model.data.local.database.MainDatabase
import com.rmyfactory.rmyinventorybarcode.model.repository.MainRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MainInjection {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): MainDatabase {
        return Room.databaseBuilder(context, MainDatabase::class.java, "db_main").build()
    }

    @Provides
    fun provideItemDao(mainDatabase: MainDatabase): ItemDao {
        return mainDatabase.itemDao()
    }

    @Singleton
    @Provides
    fun provideLocalDataSource(itemDao: ItemDao): LocalDataSource {
        return LocalDataSource(itemDao)
    }

    @Singleton
    @Provides
    fun provideRepository(localDataSource: LocalDataSource): MainRepository {
        return MainRepository(localDataSource)
    }

}