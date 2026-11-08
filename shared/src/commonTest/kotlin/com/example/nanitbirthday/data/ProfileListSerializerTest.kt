package com.example.nanitbirthday.data

import androidx.datastore.core.CorruptionException
import com.example.nanitbirthday.domain.BabyProfile
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlinx.serialization.json.Json
import okio.Buffer

class ProfileListSerializerTest {

    private val serializer = ProfileListSerializer(Json { ignoreUnknownKeys = true })

    @Test
    fun roundTripKeepsEveryField() = runTest {
        val profiles = listOf(
            BabyProfile(id = "a", name = "Mia", birthday = LocalDate(2025, 3, 14), pictureFile = "mia"),
            BabyProfile(id = "b"),
        )
        val buffer = Buffer()

        serializer.writeTo(profiles, buffer)

        assertEquals(profiles, serializer.readFrom(buffer))
    }

    @Test
    fun malformedTextIsReportedAsCorruption() = runTest {
        assertFailsWith<CorruptionException> { serializer.readFrom(Buffer().writeUtf8("not json")) }
        assertFailsWith<CorruptionException> {
            serializer.readFrom(Buffer().writeUtf8("""[{"id":"a","birthday":"yesterday"}]"""))
        }
    }

    @Test
    fun unknownFieldsAreIgnored() = runTest {
        val decoded = serializer.readFrom(Buffer().writeUtf8("""[{"id":"a","name":"Mia","mood":"happy"}]"""))

        assertEquals(listOf(BabyProfile(id = "a", name = "Mia")), decoded)
    }

    @Test
    fun emptyStoreHasNoProfiles() {
        assertEquals(emptyList(), serializer.defaultValue)
    }
}
