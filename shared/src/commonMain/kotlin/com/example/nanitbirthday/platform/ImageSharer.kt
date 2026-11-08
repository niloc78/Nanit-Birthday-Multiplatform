package com.example.nanitbirthday.platform

import androidx.compose.ui.graphics.ImageBitmap
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.cacheDir
import io.github.vinceglb.filekit.dialogs.shareFile
import io.github.vinceglb.filekit.write

interface ImageSharer {
    suspend fun share(image: ImageBitmap)
}

class FileKitImageSharer : ImageSharer {
    override suspend fun share(image: ImageBitmap) {
        val file = PlatformFile(FileKit.cacheDir, SHARE_FILE_NAME)
        file.write(image.encodePng())
        FileKit.shareFile(file)
    }
}

private const val SHARE_FILE_NAME = "birthday.png"
