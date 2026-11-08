package com.example.nanitbirthday.ui.profiles

import app.cash.turbine.test
import com.example.nanitbirthday.data.BabyProfileRepository
import com.example.nanitbirthday.data.PictureStore
import com.example.nanitbirthday.domain.BabyProfile
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileListViewModelTest {

    private val repository = mock<BabyProfileRepository> {
        every { profiles } returns flowOf(emptyList())
    }
    private val pictureStore = mock<PictureStore> {
        every { pathOf(any()) } calls { (fileName: String) -> "/pictures/$fileName" }
    }

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun startsEmptyThenShowsExistingProfilesWithPicturePaths() = runTest {
        val birthday = LocalDate(2026, 2, 1)
        every { repository.profiles } returns flowOf(
            listOf(BabyProfile(id = "a", name = "Mia", birthday = birthday, pictureFile = "mia"), BabyProfile(id = "b")),
        )
        val viewModel = ProfileListViewModel(repository, pictureStore)

        viewModel.uiState.test {
            assertEquals(emptyList(), awaitItem().profiles)
            assertEquals(
                listOf(
                    ProfileListItem(id = "a", name = "Mia", birthday = birthday, picturePath = "/pictures/mia"),
                    ProfileListItem(id = "b", name = "", birthday = null, picturePath = null),
                ),
                awaitItem().profiles,
            )
        }
    }

    @Test
    fun listReactsToRepositoryChanges() = runTest {
        val profiles = MutableStateFlow(emptyList<BabyProfile>())
        every { repository.profiles } returns profiles
        val viewModel = ProfileListViewModel(repository, pictureStore)

        viewModel.uiState.test {
            assertEquals(emptyList(), awaitItem().profiles)

            profiles.value = listOf(BabyProfile(id = "a", name = "Mia"))
            assertEquals(listOf(ProfileListItem(id = "a", name = "Mia", birthday = null, picturePath = null)), awaitItem().profiles)

            profiles.value = emptyList()
            assertEquals(emptyList(), awaitItem().profiles)
        }
    }
}
