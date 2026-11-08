package com.example.nanitbirthday.ui.profiles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nanitbirthday.data.BabyProfileRepository
import com.example.nanitbirthday.data.PictureStore
import com.example.nanitbirthday.domain.BabyProfile
import kotlin.uuid.Uuid
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ProfileListViewModel(
    repository: BabyProfileRepository,
    private val pictureStore: PictureStore,
) : ViewModel() {

    val uiState: StateFlow<ProfileListUiState> = repository.profiles
        .map { profiles -> ProfileListUiState(profiles = profiles.map(::toItem)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProfileListUiState())

    fun newProfileId(): String = Uuid.random().toString()

    private fun toItem(profile: BabyProfile) = ProfileListItem(
        id = profile.id,
        name = profile.name,
        birthday = profile.birthday,
        picturePath = profile.pictureFile?.let(pictureStore::pathOf),
    )
}
