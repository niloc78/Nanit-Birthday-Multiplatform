package com.example.nanitbirthday.data

import kotlin.uuid.Uuid
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

interface PictureStore {
    suspend fun save(bytes: ByteArray): String
    suspend fun delete(fileName: String)
    fun pathOf(fileName: String): String
}

suspend fun PictureStore.replace(previous: String?, bytes: ByteArray, store: suspend (fileName: String) -> Unit) {
    store(save(bytes))
    previous?.let { delete(it) }
}

class FilePictureStore(
    private val directory: Path,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : PictureStore {

    override suspend fun save(bytes: ByteArray): String {
        val fileName = Uuid.random().toString()
        withContext(dispatcher) {
            SystemFileSystem.createDirectories(directory)
            SystemFileSystem.sink(Path(directory, fileName)).buffered().use { it.write(bytes) }
        }
        return fileName
    }

    override suspend fun delete(fileName: String) {
        withContext(dispatcher) {
            SystemFileSystem.delete(Path(directory, fileName), mustExist = false)
        }
    }

    override fun pathOf(fileName: String): String = Path(directory, fileName).toString()
}
