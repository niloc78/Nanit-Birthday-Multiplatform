package com.example.nanitbirthday.platform

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.delete
import io.github.vinceglb.filekit.dialogs.FileKitDialogException
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openCameraPicker
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.readBytes

class FileKitImagePicker : ImagePicker {

    override suspend fun pickFromGallery(): ByteArray? = unlessDismissed {
        FileKit.openFilePicker(type = FileKitType.Image)?.readBytes()
    }

    override suspend fun takePhoto(): ByteArray? = unlessDismissed {
        FileKit.openCameraPicker()?.let { capture ->
            val bytes = capture.readBytes()
            capture.delete()
            bytes
        }
    }

    private suspend fun unlessDismissed(pick: suspend () -> ByteArray?): ByteArray? =
        try {
            pick()
        } catch (e: FileKitDialogException) {
            null
        }
}
