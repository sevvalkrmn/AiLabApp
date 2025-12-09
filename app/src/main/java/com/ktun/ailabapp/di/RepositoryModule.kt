package com.ktunailab.ailabapp.di


import com.ktunailab.ailabapp.data.local.datastore.PreferencesManager
import com.ktunailab.ailabapp.data.remote.api.AnnouncementApi
import com.ktunailab.ailabapp.data.remote.api.AuthApi
import com.ktunailab.ailabapp.data.remote.api.ProjectApi
import com.ktunailab.ailabapp.data.remote.api.RoomsApi
import com.ktunailab.ailabapp.data.remote.api.TaskApi
import com.ktunailab.ailabapp.data.repository.AnnouncementRepository
import com.ktunailab.ailabapp.data.repository.AuthRepository
import com.ktunailab.ailabapp.data.repository.LabStatsRepository
import com.ktunailab.ailabapp.data.repository.LabStatsRepositoryImpl
import com.ktunailab.ailabapp.data.repository.ProjectRepository
import com.ktunailab.ailabapp.data.repository.TaskRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

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
}