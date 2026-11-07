package com.example.nanitbirthday.di

import com.example.nanitbirthday.data.BabyProfileRepository
import com.example.nanitbirthday.data.InMemoryBabyProfileRepository
import com.example.nanitbirthday.domain.ProfileIdGenerator
import com.example.nanitbirthday.domain.SystemTodayProvider
import com.example.nanitbirthday.domain.TodayProvider
import com.example.nanitbirthday.domain.UuidProfileIdGenerator
import com.example.nanitbirthday.ui.details.ProfileDetailsViewModel
import com.example.nanitbirthday.ui.profiles.ProfileListViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    // TODO Step 2 - Persistence via DataStore
    single<BabyProfileRepository> { InMemoryBabyProfileRepository() }
    singleOf(::SystemTodayProvider) bind TodayProvider::class
    singleOf(::UuidProfileIdGenerator) bind ProfileIdGenerator::class
    viewModelOf(::ProfileListViewModel)
    viewModel { (profileId: String) -> ProfileDetailsViewModel(profileId, get(), get()) }
}
