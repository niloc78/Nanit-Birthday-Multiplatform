package com.example.nanitbirthday.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nanitbirthday.data.BabyProfileRepository
import com.example.nanitbirthday.domain.BabyProfile
import com.example.nanitbirthday.domain.TodayProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class ProfileDetailsViewModel(
    private val profileId: String,
    private val repository: BabyProfileRepository,
    private val todayProvider: TodayProvider,
) : ViewModel() {

    private var profile: BabyProfile? = null
    private val _uiState = MutableStateFlow<ProfileDetailsUiState>(ProfileDetailsUiState.Loading)
    val uiState: StateFlow<ProfileDetailsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val stored = repository.profiles.first().firstOrNull { it.id == profileId }
            show(stored ?: BabyProfile(id = profileId))
        }
    }

    fun onNameChange(name: String) {
        edit { copy(name = name) }
    }

    fun onBirthdayChange(birthday: LocalDate) {
        edit { copy(birthday = birthday) }
    }

    fun onDelete() {
        viewModelScope.launch { repository.delete(profileId) }
    }

    private fun edit(change: BabyProfile.() -> BabyProfile) {
        val updated = profile?.change() ?: return
        show(updated)
        viewModelScope.launch { repository.save(updated) }
    }

    private fun show(current: BabyProfile) {
        profile = current
        _uiState.value = ProfileDetailsUiState.Ready(
            name = current.name,
            birthday = current.birthday,
            latestBirthday = todayProvider.today(),
            canShowBirthday = current.isComplete,
        )
    }
}
