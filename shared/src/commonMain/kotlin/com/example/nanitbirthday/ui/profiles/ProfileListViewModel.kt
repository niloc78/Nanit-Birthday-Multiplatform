package com.example.nanitbirthday.ui.profiles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nanitbirthday.data.BabyProfileRepository
import com.example.nanitbirthday.domain.ProfileIdGenerator
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ProfileListViewModel(
    repository: BabyProfileRepository,
    private val idGenerator: ProfileIdGenerator,
) : ViewModel() {

    val uiState: StateFlow<ProfileListUiState> = repository.profiles
        .map { profiles -> ProfileListUiState(profiles = profiles) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProfileListUiState())

    fun newProfileId(): String = idGenerator.next()
}
