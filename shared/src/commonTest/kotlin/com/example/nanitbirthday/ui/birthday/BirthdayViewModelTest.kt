package com.example.nanitbirthday.ui.birthday

import app.cash.turbine.test
import com.example.nanitbirthday.data.BabyProfileRepository
import com.example.nanitbirthday.data.PictureStore
import com.example.nanitbirthday.domain.Age
import com.example.nanitbirthday.domain.BabyProfile
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.mock
import kotlin.random.Random
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Clock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn

@OptIn(ExperimentalCoroutinesApi::class)
class BirthdayViewModelTest {

    private val today = LocalDate(2026, 11, 7)
    private val profile = BabyProfile(
        id = "baby-1",
        name = "Mia",
        birthday = LocalDate(2026, 6, 7),
        pictureFile = "mia.jpg",
    )
    private val storedProfiles = MutableStateFlow(listOf(profile))
    private val clock = mock<Clock> {
        every { now() } returns today.atStartOfDayIn(TimeZone.currentSystemDefault())
    }
    private val pictureStore = mock<PictureStore> {
        every { pathOf(any()) } calls { (fileName: String) -> "/pictures/$fileName" }
    }
    private val repository = mock<BabyProfileRepository> {
        every { profiles } returns storedProfiles
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
    fun readyStateMapsProfileAgeAndPicture() = runTest {
        viewModel(Random(1)).uiState.test {
            assertEquals(BirthdayUiState.Loading, awaitItem())
            assertEquals(
                BirthdayUiState.Ready(
                    name = "Mia",
                    age = Age.Months(5),
                    picturePath = "/pictures/mia.jpg",
                    theme = BirthdayTheme.entries.random(Random(1)),
                ),
                awaitItem(),
            )
        }
    }

    @Test
    fun missingProfileStaysLoading() = runTest {
        storedProfiles.value = emptyList()

        viewModel(Random(1)).uiState.test {
            assertEquals(BirthdayUiState.Loading, awaitItem())
            expectNoEvents()
        }
    }

    @Test
    fun pictureChangeIsReflected() = runTest {
        viewModel(Random(1)).uiState.test {
            awaitItem()
            assertEquals("/pictures/mia.jpg", (awaitItem() as BirthdayUiState.Ready).picturePath)

            storedProfiles.value = listOf(profile.copy(pictureFile = "new.jpg"))

            assertEquals("/pictures/new.jpg", (awaitItem() as BirthdayUiState.Ready).picturePath)
        }
    }

    private fun viewModel(random: Random) = BirthdayViewModel(
        profileId = "baby-1",
        repository = repository,
        clock = clock,
        pictureStore = pictureStore,
        random = random,
    )
}
