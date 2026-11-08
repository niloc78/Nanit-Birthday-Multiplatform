package com.example.nanitbirthday.di

import com.example.nanitbirthday.data.FilePictureStore
import com.example.nanitbirthday.data.PICTURE_DIRECTORY
import com.example.nanitbirthday.data.PROFILE_DATA_STORE_FILE
import com.example.nanitbirthday.data.PictureStore
import com.example.nanitbirthday.data.createProfileDataStore
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.io.files.Path
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

actual val platformModule: Module = module {
    single { createProfileDataStore(get()) { documentsDirectory() + "/" + PROFILE_DATA_STORE_FILE } }
    single<PictureStore> { FilePictureStore(Path(documentsDirectory(), PICTURE_DIRECTORY)) }
}

@OptIn(ExperimentalForeignApi::class)
private fun documentsDirectory(): String {
    val url = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return requireNotNull(url?.path)
}
