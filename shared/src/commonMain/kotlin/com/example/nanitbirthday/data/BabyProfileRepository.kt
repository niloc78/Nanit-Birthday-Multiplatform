package com.example.nanitbirthday.data

import com.example.nanitbirthday.domain.BabyProfile
import kotlinx.coroutines.flow.Flow

interface BabyProfileRepository {
    val profiles: Flow<List<BabyProfile>>
    suspend fun save(profile: BabyProfile)
    suspend fun delete(id: String)
}
