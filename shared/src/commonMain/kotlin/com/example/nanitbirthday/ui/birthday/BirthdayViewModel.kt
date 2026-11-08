package com.example.nanitbirthday.ui.birthday

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nanitbirthday.data.BabyProfileRepository
import com.example.nanitbirthday.data.PictureStore
import com.example.nanitbirthday.domain.BabyProfile
import com.example.nanitbirthday.domain.ageOn
import kotlin.random.Random
import kotlin.time.Clock
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

class BirthdayViewModel(
    profileId: String,
    repository: BabyProfileRepository,
    private val clock: Clock,
    private val pictureStore: PictureStore,
    random: Random,
) : ViewModel() {

    private val theme = BirthdayTheme.entries.random(random)

    val uiState: StateFlow<BirthdayUiState> = repository.profiles
        .map { profiles -> profiles.firstOrNull { it.id == profileId }?.toUiState() ?: BirthdayUiState.Loading }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BirthdayUiState.Loading)

    private fun BabyProfile.toUiState(): BirthdayUiState? {
        val birthday = birthday ?: return null
        return BirthdayUiState.Ready(
            name = name,
            age = birthday.ageOn(clock.todayIn(TimeZone.currentSystemDefault())),
            picturePath = pictureFile?.let(pictureStore::pathOf),
            theme = theme,
        )
    }
}
