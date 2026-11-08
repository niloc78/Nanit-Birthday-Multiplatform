package com.example.nanitbirthday.data

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.core.okio.OkioStorage
import com.example.nanitbirthday.domain.BabyProfile
import kotlinx.serialization.json.Json
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.SYSTEM

const val PROFILE_DATA_STORE_FILE = "profiles.json"
const val PICTURE_DIRECTORY = "pictures"

fun createProfileDataStore(json: Json, producePath: () -> String): DataStore<List<BabyProfile>> =
    DataStoreFactory.create(
        storage = OkioStorage(
            fileSystem = FileSystem.SYSTEM,
            serializer = ProfileListSerializer(json),
            producePath = { producePath().toPath() },
        ),
        corruptionHandler = ReplaceFileCorruptionHandler { emptyList() },
    )
