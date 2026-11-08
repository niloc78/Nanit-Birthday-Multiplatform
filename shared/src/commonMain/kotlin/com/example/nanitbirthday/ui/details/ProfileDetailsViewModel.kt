package com.example.nanitbirthday.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nanitbirthday.data.BabyProfileRepository
import com.example.nanitbirthday.data.PictureStore
import com.example.nanitbirthday.data.replace
import com.example.nanitbirthday.domain.BabyProfile
import com.example.nanitbirthday.platform.ImagePicker
import kotlin.time.Clock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

class ProfileDetailsViewModel(
    private val profileId: String,
    private val repository: BabyProfileRepository,
    private val clock: Clock,
    private val imagePicker: ImagePicker,
    private val pictureStore: PictureStore,
) : ViewModel() {

    private var profile: BabyProfile? = null
    private val _uiState = MutableStateFlow<ProfileDetailsUiState>(ProfileDetailsUiState.Loading)
    val uiState: StateFlow<ProfileDetailsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.profiles.collect { profiles ->
                val stored = profiles.firstOrNull { it.id == profileId }
                val current = profile
                when {
                    current == null -> show(stored ?: BabyProfile(id = profileId))
                    stored != null && stored.pictureFile != current.pictureFile ->
                        show(current.copy(pictureFile = stored.pictureFile))
                }
            }
        }
    }

    fun onNameChange(name: String) {
        edit { copy(name = name) }
    }

    fun onBirthdayChange(birthday: LocalDate) {
        edit { copy(birthday = birthday) }
    }

    fun onPickFromGallery() {
        replacePicture { imagePicker.pickFromGallery() }
    }

    fun onTakePhoto() {
        replacePicture { imagePicker.takePhoto() }
    }

    fun onDelete() {
        val pictureFile = profile?.pictureFile
        viewModelScope.launch {
            pictureFile?.let { pictureStore.delete(it) }
            repository.delete(profileId)
        }
    }

    private fun replacePicture(pick: suspend () -> ByteArray?) {
        viewModelScope.launch {
            val bytes = pick() ?: return@launch
            pictureStore.replace(profile?.pictureFile, bytes) { fileName -> edit { copy(pictureFile = fileName) } }
        }
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
            picturePath = current.pictureFile?.let(pictureStore::pathOf),
            latestBirthday = clock.todayIn(TimeZone.currentSystemDefault()),
            canShowBirthday = current.isComplete,
        )
    }
}
