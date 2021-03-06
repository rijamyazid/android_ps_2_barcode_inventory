package com.rmyfactory.inventorybarcode.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.rmyfactory.inventorybarcode.model.data.local.LocalDataSource
import com.rmyfactory.inventorybarcode.model.data.local.dao.*
import com.rmyfactory.inventorybarcode.model.data.local.database.MainDatabase
import com.rmyfactory.inventorybarcode.model.data.local.model.UnitModel
import com.rmyfactory.inventorybarcode.model.repository.MainRepository
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
    fun provideProductDao(mainDatabase: MainDatabase): ProductDao {
        return mainDatabase.productDao()
    }

    @Provides
    fun provideOrderDao(mainDatabase: MainDatabase): OrderDao {
        return mainDatabase.orderDao()
    }

    @Provides
    fun provideOrderProductDao(mainDatabase: MainDatabase): OrderProductDao {
        return mainDatabase.orderProductDao()
    }

    @Provides
    fun provideUnitDao(mainDatabase: MainDatabase): UnitDao {
        return mainDatabase.unitDao()
    }

    @Provides
    fun provideProductUnitDao(mainDatabase: MainDatabase): ProductUnitDao {
        return mainDatabase.productUnitDao()
    }

    @Singleton
    @Provides
    fun provideLocalDataSource(
        productDao: ProductDao,
        orderDao: OrderDao,
        orderProductDao: OrderProductDao,
        unitDao: UnitDao,
        productUnitDao: ProductUnitDao
    ): LocalDataSource {
        return LocalDataSource(productDao, orderDao, orderProductDao, unitDao, productUnitDao)
    }

    @Singleton
    @Provides
    fun provideRepository(localDataSource: LocalDataSource): MainRepository {
        return MainRepository(localDataSource)
    }

}