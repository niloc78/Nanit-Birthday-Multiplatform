package com.example.nanitbirthday.ui.profiles

import com.example.nanitbirthday.domain.BabyProfile

data class ProfileListUiState(
    val profiles: List<BabyProfile> = emptyList(),
)
