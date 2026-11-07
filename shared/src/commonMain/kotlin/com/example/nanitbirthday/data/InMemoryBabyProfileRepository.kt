package com.example.nanitbirthday.data

import com.example.nanitbirthday.domain.BabyProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

// TODO Step 2 - Persistence via DataStore
class InMemoryBabyProfileRepository(
    initial: List<BabyProfile> = emptyList(),
) : BabyProfileRepository {

    private val state = MutableStateFlow(initial)

    override val profiles: Flow<List<BabyProfile>> = state

    override suspend fun save(profile: BabyProfile) {
        state.update { current ->
            if (current.any { it.id == profile.id }) {
                current.map { if (it.id == profile.id) profile else it }
            } else {
                current + profile
            }
        }
    }

    override suspend fun delete(id: String) {
        state.update { current -> current.filterNot { it.id == id } }
    }
}
