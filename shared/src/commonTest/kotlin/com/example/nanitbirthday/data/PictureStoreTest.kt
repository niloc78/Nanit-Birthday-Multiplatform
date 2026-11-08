package com.example.nanitbirthday.data

import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlin.uuid.Uuid
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.files.SystemTemporaryDirectory
import kotlinx.io.readByteArray

@OptIn(ExperimentalCoroutinesApi::class)
class PictureStoreTest {

    private val directory = Path(SystemTemporaryDirectory, "pictures-" + Uuid.random())

    @AfterTest
    fun removeDirectory() {
        if (SystemFileSystem.exists(directory)) {
            SystemFileSystem.list(directory).forEach { SystemFileSystem.delete(it) }
            SystemFileSystem.delete(directory)
        }
    }

    @Test
    fun saveWritesTheBytesInsideTheDirectory() = runTest {
        val store = store()

        val fileName = store.save(byteArrayOf(9, 8, 7))

        val path = Path(store.pathOf(fileName))
        assertTrue(path.toString().startsWith(directory.toString()))
        assertContentEquals(byteArrayOf(9, 8, 7), SystemFileSystem.source(path).buffered().use { it.readByteArray() })
    }

    @Test
    fun eachSaveGetsItsOwnName() = runTest {
        val store = store()

        val first = store.save(byteArrayOf(1))
        val second = store.save(byteArrayOf(2))

        assertNotEquals(first, second)
        assertTrue(SystemFileSystem.list(directory).size == 2)
    }

    @Test
    fun deleteRemovesTheFile() = runTest {
        val store = store()
        val fileName = store.save(byteArrayOf(1))

        store.delete(fileName)

        assertFalse(SystemFileSystem.exists(Path(store.pathOf(fileName))))
    }

    @Test
    fun deletingAMissingFileIsIgnored() = runTest {
        store().delete("missing")
    }

    @Test
    fun replaceStoresTheNewNameBeforeDeletingThePrevious() = runTest {
        val store = store()
        val previous = store.save(byteArrayOf(1))
        var stored: String? = null

        store.replace(previous, byteArrayOf(2)) { fileName ->
            stored = fileName
            assertTrue(SystemFileSystem.exists(Path(directory, previous)))
        }

        assertContentEquals(byteArrayOf(2), SystemFileSystem.source(Path(directory, stored!!)).buffered().use { it.readByteArray() })
        assertFalse(SystemFileSystem.exists(Path(directory, previous)))
    }

    private fun TestScope.store() = FilePictureStore(directory, StandardTestDispatcher(testScheduler))
}
