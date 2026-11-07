package com.example.nanitbirthday.ui.details

import kotlinx.datetime.LocalDate

sealed interface ProfileDetailsUiState {
    data object Loading : ProfileDetailsUiState

    data class Ready(
        val name: String,
        val birthday: LocalDate?,
        val latestBirthday: LocalDate,
        val canShowBirthday: Boolean,
    ) : ProfileDetailsUiState
}
