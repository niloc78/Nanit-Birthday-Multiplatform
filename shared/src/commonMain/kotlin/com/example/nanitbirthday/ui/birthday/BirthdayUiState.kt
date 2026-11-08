package com.example.nanitbirthday.ui.birthday

import com.example.nanitbirthday.domain.Age

sealed interface BirthdayUiState {
    data object Loading : BirthdayUiState

    data class Ready(
        val name: String,
        val age: Age,
        val picturePath: String?,
        val theme: BirthdayTheme,
    ) : BirthdayUiState
}
