package com.example.nanitbirthday.ui.details

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.example.nanitbirthday.data.BabyProfileRepository
import com.example.nanitbirthday.data.InMemoryBabyProfileRepository
import com.example.nanitbirthday.domain.BabyProfile
import com.example.nanitbirthday.domain.TodayProvider
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileDetailsViewModelTest {

    private val today = LocalDate(2026, 11, 7)
    private val birthday = LocalDate(2026, 2, 1)
    private val todayProvider = TodayProvider { today }

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun newProfileLoadsEmptyWithButtonDisabled() = runTest {
        viewModel(InMemoryBabyProfileRepository()).uiState.test {
            assertEquals(ProfileDetailsUiState.Loading, awaitItem())

            val ready = awaitReady()
            assertEquals("", ready.name)
            assertNull(ready.birthday)
            assertEquals(today, ready.latestBirthday)
            assertFalse(ready.canShowBirthday)
        }
    }

    @Test
    fun existingProfileIsLoaded() = runTest {
        val stored = BabyProfile(id = "baby-1", name = "Mia", birthday = birthday)

        viewModel(InMemoryBabyProfileRepository(listOf(stored))).uiState.test {
            assertEquals(ProfileDetailsUiState.Loading, awaitItem())

            val ready = awaitReady()
            assertEquals("Mia", ready.name)
            assertEquals(birthday, ready.birthday)
            assertTrue(ready.canShowBirthday)
        }
    }

    @Test
    fun nameAloneKeepsButtonDisabled() = runTest {
        val viewModel = viewModel(InMemoryBabyProfileRepository())

        viewModel.uiState.test {
            skipLoading()

            viewModel.onNameChange("Mia")

            val ready = awaitReady()
            assertEquals("Mia", ready.name)
            assertFalse(ready.canShowBirthday)
        }
    }

    @Test
    fun birthdayAloneKeepsButtonDisabled() = runTest {
        val viewModel = viewModel(InMemoryBabyProfileRepository())

        viewModel.uiState.test {
            skipLoading()

            viewModel.onBirthdayChange(birthday)

            val ready = awaitReady()
            assertEquals(birthday, ready.birthday)
            assertFalse(ready.canShowBirthday)
        }
    }

    @Test
    fun buttonFollowsNameAndBirthday() = runTest {
        val viewModel = viewModel(InMemoryBabyProfileRepository())

        viewModel.uiState.test {
            skipLoading()

            viewModel.onNameChange("Mia")
            assertFalse(awaitReady().canShowBirthday)

            viewModel.onBirthdayChange(birthday)
            assertTrue(awaitReady().canShowBirthday)

            viewModel.onNameChange("   ")
            assertFalse(awaitReady().canShowBirthday)
        }
    }

    @Test
    fun everyChangeIsSaved() = runTest {
        val repository = InMemoryBabyProfileRepository()
        val viewModel = viewModel(repository)
        advanceUntilIdle()

        repository.profiles.test {
            assertEquals(emptyList(), awaitItem())

            viewModel.onNameChange("Mia")
            assertEquals(listOf(BabyProfile(id = "baby-1", name = "Mia")), awaitItem())

            viewModel.onBirthdayChange(birthday)
            assertEquals(listOf(BabyProfile(id = "baby-1", name = "Mia", birthday = birthday)), awaitItem())
        }
    }

    @Test
    fun savingKeepsOtherProfilesUntouched() = runTest {
        val other = BabyProfile(id = "baby-2", name = "Noah")
        val repository = InMemoryBabyProfileRepository(listOf(other))
        val viewModel = viewModel(repository)
        advanceUntilIdle()

        repository.profiles.test {
            assertEquals(listOf(other), awaitItem())

            viewModel.onNameChange("Mia")

            assertEquals(listOf(other, BabyProfile(id = "baby-1", name = "Mia")), awaitItem())
        }
    }

    @Test
    fun changesAreWrittenThroughTheRepository() = runTest {
        val repository = mock<BabyProfileRepository>(MockMode.autoUnit) {
            every { profiles } returns flowOf(emptyList())
        }
        val viewModel = viewModel(repository)
        advanceUntilIdle()

        viewModel.onNameChange("Mia")
        viewModel.onBirthdayChange(birthday)
        advanceUntilIdle()

        verifySuspend(VerifyMode.order) {
            repository.save(BabyProfile(id = "baby-1", name = "Mia"))
            repository.save(BabyProfile(id = "baby-1", name = "Mia", birthday = birthday))
        }
    }

    @Test
    fun changesBeforeLoadingFinishesAreIgnored() = runTest {
        val repository = mock<BabyProfileRepository>(MockMode.autoUnit) {
            every { profiles } returns flowOf(emptyList())
        }
        val viewModel = viewModel(repository)

        viewModel.onNameChange("Mia")
        advanceUntilIdle()

        verifySuspend(VerifyMode.not) { repository.save(any()) }
    }

    @Test
    fun deleteRemovesOnlyThisProfile() = runTest {
        val other = BabyProfile(id = "baby-2", name = "Noah")
        val mine = BabyProfile(id = "baby-1", name = "Mia")
        val repository = InMemoryBabyProfileRepository(listOf(other, mine))
        val viewModel = viewModel(repository)
        advanceUntilIdle()

        repository.profiles.test {
            assertEquals(listOf(other, mine), awaitItem())

            viewModel.onDelete()

            assertEquals(listOf(other), awaitItem())
        }
    }

    private fun viewModel(repository: BabyProfileRepository) =
        ProfileDetailsViewModel("baby-1", repository, todayProvider)

    private suspend fun ReceiveTurbine<ProfileDetailsUiState>.awaitReady(): ProfileDetailsUiState.Ready =
        assertIs<ProfileDetailsUiState.Ready>(awaitItem())

    private suspend fun ReceiveTurbine<ProfileDetailsUiState>.skipLoading() {
        assertEquals(ProfileDetailsUiState.Loading, awaitItem())
        awaitReady()
    }
}
