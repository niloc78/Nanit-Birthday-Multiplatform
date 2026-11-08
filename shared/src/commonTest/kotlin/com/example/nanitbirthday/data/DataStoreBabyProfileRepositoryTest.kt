package com.example.nanitbirthday.data

import com.example.nanitbirthday.domain.BabyProfile
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.Uuid
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okio.FileSystem
import okio.SYSTEM

class DataStoreBabyProfileRepositoryTest {

    private val file = FileSystem.SYSTEM_TEMPORARY_DIRECTORY / ("profiles-" + Uuid.random() + ".json")
    private val repository = DataStoreBabyProfileRepository(createProfileDataStore(Json) { file.toString() })

    @AfterTest
    fun removeFile() {
        FileSystem.SYSTEM.delete(file, mustExist = false)
    }

    @Test
    fun saveAppendsNewProfilesAndReplacesById() = runTest {
        val mia = BabyProfile(id = "a", name = "Mia")
        val noah = BabyProfile(id = "b", name = "Noah")

        repository.save(mia)
        repository.save(noah)
        repository.save(mia.copy(name = "Mila"))

        assertEquals(listOf(mia.copy(name = "Mila"), noah), repository.profiles.first())
    }

    @Test
    fun deleteRemovesOnlyThatProfile() = runTest {
        repository.save(BabyProfile(id = "a", name = "Mia"))
        repository.save(BabyProfile(id = "b", name = "Noah"))

        repository.delete("a")

        assertEquals(listOf(BabyProfile(id = "b", name = "Noah")), repository.profiles.first())
    }

    @Test
    fun corruptFileReadsAsEmptyList() = runTest {
        FileSystem.SYSTEM.write(file) { writeUtf8("not json") }

        assertEquals(emptyList(), repository.profiles.first())
    }
}
