package com.rmyfactory.rmyinventorybarcode.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.rmyfactory.rmyinventorybarcode.model.data.local.LocalDataSource
import com.rmyfactory.rmyinventorybarcode.model.data.local.dao.*
import com.rmyfactory.rmyinventorybarcode.model.data.local.database.MainDatabase
import com.rmyfactory.rmyinventorybarcode.model.data.local.model.UnitModel
import com.rmyfactory.rmyinventorybarcode.model.repository.MainRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MainInjection {

    @Volatile
    private var INSTANCE: MainDatabase? = null

    private class MainDatabaseCallback
        (private val scope: CoroutineScope): RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let {
                scope.launch {
                    it.unitDao().insertUnits(
                        listOf(
                            UnitModel(unitId = "Buah"),
                            UnitModel(unitId = "Sachet"),
                            UnitModel(unitId = "Renceng"),
                            UnitModel(unitId = "PCS"),
                            UnitModel(unitId = "Box"),
                            UnitModel(unitId = "Dus"),
                        )
                    )
                }
            }
        }
    }

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): MainDatabase {
        return INSTANCE ?: synchronized(this) {
            val scope = CoroutineScope(Dispatchers.IO)
            val instance = Room.databaseBuilder(
                context, MainDatabase::class.java, "db_main")
                .addCallback(MainDatabaseCallback(scope))
                .build()
                .also { INSTANCE = it }
            instance
        }
    }

    @Provides
    fun provideItemDao(mainDatabase: MainDatabase): ItemDao {
        return mainDatabase.itemDao()
    }

    @Provides
    fun provideOrderDao(mainDatabase: MainDatabase): OrderDao {
        return mainDatabase.orderDao()
    }

    @Provides
    fun provideOrderItemDao(mainDatabase: MainDatabase): OrderItemDao {
        return mainDatabase.orderItemDao()
    }

    @Provides
    fun provideUnitDao(mainDatabase: MainDatabase): UnitDao {
        return mainDatabase.unitDao()
    }

    @Provides
    fun provideItemUnitDao(mainDatabase: MainDatabase): ItemUnitDao {
        return mainDatabase.itemUnitDao()
    }

    @Singleton
    @Provides
    fun provideLocalDataSource(
        itemDao: ItemDao,
        orderDao: OrderDao,
        orderItemDao: OrderItemDao,
        unitDao: UnitDao,
        itemUnitDao: ItemUnitDao
    ): LocalDataSource {
        return LocalDataSource(itemDao, orderDao, orderItemDao, unitDao, itemUnitDao)
    }

    @Singleton
    @Provides
    fun provideRepository(localDataSource: LocalDataSource): MainRepository {
        return MainRepository(localDataSource)
    }

}