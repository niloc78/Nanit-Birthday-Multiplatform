package com.example.nanitbirthday.ui.profiles

import kotlinx.datetime.LocalDate

data class ProfileListUiState(
    val profiles: List<ProfileListItem> = emptyList(),
)

data class ProfileListItem(
    val id: String,
    val name: String,
    val birthday: LocalDate?,
    val picturePath: String?,
)
