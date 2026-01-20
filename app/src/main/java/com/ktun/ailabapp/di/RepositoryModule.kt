package com.ktun.ailabapp.di


import com.ktun.ailabapp.data.local.datastore.PreferencesManager
import com.ktun.ailabapp.data.remote.api.AnnouncementApi
import com.ktun.ailabapp.data.remote.api.AuthApi
import com.ktun.ailabapp.data.remote.api.ProjectApi
import com.ktun.ailabapp.data.remote.api.RoomsApi
import com.ktun.ailabapp.data.remote.api.TaskApi
import com.ktun.ailabapp.data.repository.AnnouncementRepository
import com.ktun.ailabapp.data.repository.AuthRepository
import com.ktun.ailabapp.data.repository.LabStatsRepository
import com.ktun.ailabapp.data.repository.LabStatsRepositoryImpl
import com.ktun.ailabapp.data.repository.ProjectRepository
import com.ktun.ailabapp.data.repository.TaskRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

import com.ktun.ailabapp.data.remote.api.AdminScoreApi // ✅ Import
import com.ktun.ailabapp.data.repository.AdminScoreRepository // ✅ Import

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        authApi: AuthApi,
        preferencesManager: PreferencesManager
    ): AuthRepository {
        return AuthRepository(authApi, preferencesManager)
    }

    @Provides
    @Singleton
    fun provideProjectRepository(
        projectApi: ProjectApi
    ): ProjectRepository {
        return ProjectRepository(projectApi)
    }

    @Provides
    @Singleton
    fun provideTaskRepository(
        taskApi: TaskApi
    ): TaskRepository {
        return TaskRepository(taskApi)
    }

    @Provides
    @Singleton
    fun provideAnnouncementRepository(
        announcementApi: AnnouncementApi
    ): AnnouncementRepository {
        return AnnouncementRepository(announcementApi)
    }

    @Provides
    @Singleton
    fun provideLabStatsRepository(
        roomsApi: RoomsApi
    ): LabStatsRepository {
        return LabStatsRepositoryImpl(roomsApi)
    }

    @Provides
    @Singleton
    fun provideAdminScoreRepository(
        adminScoreApi: AdminScoreApi
    ): AdminScoreRepository {
        return AdminScoreRepository(adminScoreApi)
    }
}