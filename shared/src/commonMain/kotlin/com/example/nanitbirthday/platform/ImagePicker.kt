package com.example.nanitbirthday.platform

interface ImagePicker {
    suspend fun pickFromGallery(): ByteArray?
    suspend fun takePhoto(): ByteArray?
}
