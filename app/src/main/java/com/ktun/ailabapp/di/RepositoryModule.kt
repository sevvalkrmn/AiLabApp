package com.ktun.ailabapp.di

import com.ktun.ailabapp.data.local.datastore.PreferencesManager
import com.ktun.ailabapp.data.remote.api.*
import com.ktun.ailabapp.data.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

import com.ktun.ailabapp.util.FirebaseAuthManager // ✅ Import

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        authApi: AuthApi,
        preferencesManager: PreferencesManager,
        authManager: FirebaseAuthManager // ✅ Inject FirebaseAuthManager
    ): AuthRepository {
        return AuthRepository(authApi, preferencesManager, authManager) // ✅ Pass to constructor
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

    @Provides
    @Singleton
    fun provideBugReportRepository(
        bugReportApi: BugReportApi
    ): BugReportRepository {
        return BugReportRepository(bugReportApi)
    }
}
