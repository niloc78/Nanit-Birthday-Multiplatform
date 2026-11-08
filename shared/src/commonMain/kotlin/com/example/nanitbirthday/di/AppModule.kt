package com.example.nanitbirthday.di

import com.example.nanitbirthday.data.BabyProfileRepository
import com.example.nanitbirthday.data.DataStoreBabyProfileRepository
import com.example.nanitbirthday.platform.FileKitImagePicker
import com.example.nanitbirthday.platform.ImagePicker
import com.example.nanitbirthday.ui.details.ProfileDetailsViewModel
import com.example.nanitbirthday.ui.profiles.ProfileListViewModel
import kotlin.time.Clock
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    single { Json { ignoreUnknownKeys = true } }
    singleOf(::DataStoreBabyProfileRepository) bind BabyProfileRepository::class
    singleOf(::FileKitImagePicker) bind ImagePicker::class
    single<Clock> { Clock.System }
    viewModelOf(::ProfileListViewModel)
    viewModel { (profileId: String) ->
        ProfileDetailsViewModel(profileId, get(), get(), get(), get())
    }
}
