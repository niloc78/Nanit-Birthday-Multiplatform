package com.example.nanitbirthday.di

import com.example.nanitbirthday.data.FilePictureStore
import com.example.nanitbirthday.data.PICTURE_DIRECTORY
import com.example.nanitbirthday.data.PROFILE_DATA_STORE_FILE
import com.example.nanitbirthday.data.PictureStore
import com.example.nanitbirthday.data.createProfileDataStore
import kotlinx.io.files.Path
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single {
        createProfileDataStore(get()) {
            androidContext().filesDir.resolve(PROFILE_DATA_STORE_FILE).absolutePath
        }
    }
    single<PictureStore> { FilePictureStore(Path(androidContext().filesDir.absolutePath, PICTURE_DIRECTORY)) }
}
