package id.co.minumin.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import id.co.minumin.core.DiffCallback
import id.co.minumin.data.local.AppDatabase
import id.co.minumin.data.mapper.DataMapper
import id.co.minumin.data.preference.UserPreferenceManager
import id.co.minumin.domain.datasource.LocalDataSource
import id.co.minumin.domain.repository.AppRepository
import id.co.minumin.notifications.helper.AlarmHelper
import id.co.minumin.notifications.helper.NotificationHelper
import id.co.minumin.util.LocaleHelper
import javax.inject.Singleton

/**
 * Created by pertadima on 31,January,2021
 */

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun providesDataMapper() = DataMapper

    @Singleton
    @Provides
    fun providesUserPreferenceManager(
        @ApplicationContext appContext: Context,
        dataMapper: DataMapper
    ) = UserPreferenceManager(appContext, dataMapper)

    @Singleton
    @Provides
    fun providesDiffCallback() = DiffCallback()

    @Singleton
    @Provides
    fun providesRoomDatabase(@ApplicationContext appContext: Context) =
        Room.databaseBuilder(appContext, AppDatabase::class.java, "minumin_db")
            .fallbackToDestructiveMigration()
            .build()

    @Singleton
    @Provides
    fun providesLocalDataSource(appDatabase: AppDatabase) = LocalDataSource(appDatabase)

    @Singleton
    @Provides
    fun providesRepository(
        localDataSource: LocalDataSource,
        dataMapper: DataMapper
    ) = AppRepository(localDataSource, dataMapper)

    @Singleton
    @Provides
    fun providesAlarmHelper() = AlarmHelper()

    @Singleton
    @Provides
    fun provideNotificationHelper(
        @ApplicationContext appContext: Context,
        userPreferenceManager: UserPreferenceManager
    ) = NotificationHelper(appContext, userPreferenceManager)

    @Singleton
    @Provides
    fun providesLocalHelper() = LocaleHelper()

}