package com.example.nanitbirthday.data

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.okio.OkioSerializer
import com.example.nanitbirthday.domain.BabyProfile
import kotlinx.serialization.json.Json
import okio.BufferedSink
import okio.BufferedSource

class ProfileListSerializer(private val json: Json) : OkioSerializer<List<BabyProfile>> {

    override val defaultValue: List<BabyProfile> = emptyList()

    override suspend fun readFrom(source: BufferedSource): List<BabyProfile> =
        try {
            json.decodeFromString(source.readUtf8())
        } catch (e: IllegalArgumentException) {
            throw CorruptionException("Stored profiles could not be read", e)
        }

    override suspend fun writeTo(t: List<BabyProfile>, sink: BufferedSink) {
        sink.writeUtf8(json.encodeToString(t))
    }
}
