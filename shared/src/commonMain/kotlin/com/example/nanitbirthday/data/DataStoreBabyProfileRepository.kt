package com.example.nanitbirthday.data

import androidx.datastore.core.DataStore
import com.example.nanitbirthday.domain.BabyProfile
import kotlinx.coroutines.flow.Flow

class DataStoreBabyProfileRepository(
    private val dataStore: DataStore<List<BabyProfile>>,
) : BabyProfileRepository {

    override val profiles: Flow<List<BabyProfile>> = dataStore.data

    override suspend fun save(profile: BabyProfile) {
        dataStore.updateData { current ->
            if (current.any { it.id == profile.id }) {
                current.map { if (it.id == profile.id) profile else it }
            } else {
                current + profile
            }
        }
    }

    override suspend fun delete(id: String) {
        dataStore.updateData { current -> current.filterNot { it.id == id } }
    }
}
