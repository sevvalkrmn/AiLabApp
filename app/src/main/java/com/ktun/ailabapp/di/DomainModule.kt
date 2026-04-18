package com.ktun.ailabapp.di

import com.ktun.ailabapp.data.repository.AdminScoreRepository
import com.ktun.ailabapp.data.repository.AnnouncementRepository
import com.ktun.ailabapp.data.repository.AuthRepository
import com.ktun.ailabapp.data.repository.ProjectRepository
import com.ktun.ailabapp.data.repository.TaskRepository
import com.ktun.ailabapp.data.repository.UserRepository
import com.ktun.ailabapp.domain.repository.IAdminScoreRepository
import com.ktun.ailabapp.domain.repository.IAnnouncementRepository
import com.ktun.ailabapp.domain.repository.IAuthRepository
import com.ktun.ailabapp.domain.repository.IProjectRepository
import com.ktun.ailabapp.domain.repository.ITaskRepository
import com.ktun.ailabapp.domain.repository.IUserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DomainModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepository): IAuthRepository

    @Binds
    @Singleton
    abstract fun bindTaskRepository(impl: TaskRepository): ITaskRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepository): IUserRepository

    @Binds
    @Singleton
    abstract fun bindProjectRepository(impl: ProjectRepository): IProjectRepository

    @Binds
    @Singleton
    abstract fun bindAnnouncementRepository(impl: AnnouncementRepository): IAnnouncementRepository

    @Binds
    @Singleton
    abstract fun bindAdminScoreRepository(impl: AdminScoreRepository): IAdminScoreRepository
}
