package com.example.nanitbirthday.ui.details

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.example.nanitbirthday.data.BabyProfileRepository
import com.example.nanitbirthday.data.PictureStore
import com.example.nanitbirthday.domain.BabyProfile
import com.example.nanitbirthday.platform.ImagePicker
import dev.mokkery.MockMode
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlin.test.AfterTest
import kotlin.time.Clock
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileDetailsViewModelTest {

    private val today = LocalDate(2026, 11, 7)
    private val birthday = LocalDate(2026, 2, 1)
    private val clock = mock<Clock> {
        every { now() } returns today.atStartOfDayIn(TimeZone.currentSystemDefault())
    }
    private val imagePicker = mock<ImagePicker>()
    private val pictureStore = mock<PictureStore>(MockMode.autoUnit) {
        every { pathOf(any()) } calls { (fileName: String) -> "/pictures/$fileName" }
    }
    private val repository = mock<BabyProfileRepository>(MockMode.autoUnit) {
        every { profiles } returns flowOf(emptyList())
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
    fun newProfileLoadsEmptyWithButtonDisabled() = runTest {
        viewModel().uiState.test {
            assertEquals(ProfileDetailsUiState.Loading, awaitItem())

            val ready = awaitReady()
            assertEquals("", ready.name)
            assertNull(ready.birthday)
            assertNull(ready.picturePath)
            assertEquals(today, ready.latestBirthday)
            assertFalse(ready.canShowBirthday)
        }
    }

    @Test
    fun existingProfileIsLoaded() = runTest {
        val stored = BabyProfile(id = "baby-1", name = "Mia", birthday = birthday)
        every { repository.profiles } returns flowOf(listOf(stored))

        viewModel().uiState.test {
            assertEquals(ProfileDetailsUiState.Loading, awaitItem())

            val ready = awaitReady()
            assertEquals("Mia", ready.name)
            assertEquals(birthday, ready.birthday)
            assertTrue(ready.canShowBirthday)
        }
    }

    @Test
    fun otherProfilesAreNotLoaded() = runTest {
        every { repository.profiles } returns flowOf(listOf(BabyProfile(id = "baby-2", name = "Noah")))

        viewModel().uiState.test {
            assertEquals(ProfileDetailsUiState.Loading, awaitItem())

            val ready = awaitReady()
            assertEquals("", ready.name)
            assertNull(ready.birthday)
        }
    }

    @Test
    fun nameAloneKeepsButtonDisabled() = runTest {
        val viewModel = viewModel()

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
        val viewModel = viewModel()

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
        val viewModel = viewModel()

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
        val viewModel = viewModel()
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
        val viewModel = viewModel()

        viewModel.onNameChange("Mia")
        advanceUntilIdle()

        verifySuspend(VerifyMode.not) { repository.save(any()) }
    }

    @Test
    fun pickedPictureIsStoredAndSaved() = runTest {
        val bytes = byteArrayOf(1, 2, 3)
        everySuspend { imagePicker.pickFromGallery() } returns bytes
        everySuspend { pictureStore.save(bytes) } returns "mia.jpg"
        val viewModel = viewModel()
        advanceUntilIdle()

        viewModel.uiState.test {
            assertNull(awaitReady().picturePath)

            viewModel.onPickFromGallery()

            assertEquals("/pictures/mia.jpg", awaitReady().picturePath)
        }
        verifySuspend { repository.save(BabyProfile(id = "baby-1", pictureFile = "mia.jpg")) }
    }

    @Test
    fun takenPhotoReplacesAndDeletesPreviousPicture() = runTest {
        val bytes = byteArrayOf(4, 5, 6)
        everySuspend { imagePicker.takePhoto() } returns bytes
        everySuspend { pictureStore.save(bytes) } returns "new.jpg"
        val stored = BabyProfile(id = "baby-1", name = "Mia", pictureFile = "old.jpg")
        every { repository.profiles } returns flowOf(listOf(stored))
        val viewModel = viewModel()
        advanceUntilIdle()

        viewModel.onTakePhoto()
        advanceUntilIdle()

        assertEquals("/pictures/new.jpg", viewModel.ready().picturePath)
        verifySuspend { repository.save(stored.copy(pictureFile = "new.jpg")) }
        verifySuspend { pictureStore.delete("old.jpg") }
    }

    @Test
    fun cancelledPickChangesNothing() = runTest {
        everySuspend { imagePicker.pickFromGallery() } returns null
        val viewModel = viewModel()
        advanceUntilIdle()

        viewModel.onPickFromGallery()
        advanceUntilIdle()

        assertNull(viewModel.ready().picturePath)
        verifySuspend(VerifyMode.not) { pictureStore.save(any()) }
        verifySuspend(VerifyMode.not) { repository.save(any()) }
    }

    @Test
    fun pictureChangedElsewhereIsShown() = runTest {
        val stored = MutableStateFlow(listOf(BabyProfile(id = "baby-1", name = "Mia", pictureFile = "old.jpg")))
        every { repository.profiles } returns stored
        val viewModel = viewModel()

        viewModel.uiState.test {
            assertEquals(ProfileDetailsUiState.Loading, awaitItem())
            assertEquals("/pictures/old.jpg", awaitReady().picturePath)

            stored.value = listOf(BabyProfile(id = "baby-1", name = "Mia", pictureFile = "new.jpg"))

            val ready = awaitReady()
            assertEquals("/pictures/new.jpg", ready.picturePath)
            assertEquals("Mia", ready.name)
        }
    }

    @Test
    fun deleteRemovesPictureThenProfile() = runTest {
        val mine = BabyProfile(id = "baby-1", name = "Mia", pictureFile = "mia.jpg")
        every { repository.profiles } returns flowOf(listOf(mine))
        val viewModel = viewModel()
        advanceUntilIdle()

        viewModel.onDelete()
        advanceUntilIdle()

        verifySuspend(VerifyMode.order) {
            pictureStore.delete("mia.jpg")
            repository.delete("baby-1")
        }
    }

    private fun viewModel() =
        ProfileDetailsViewModel("baby-1", repository, clock, imagePicker, pictureStore)

    private fun ProfileDetailsViewModel.ready(): ProfileDetailsUiState.Ready =
        assertIs<ProfileDetailsUiState.Ready>(uiState.value)

    private suspend fun ReceiveTurbine<ProfileDetailsUiState>.awaitReady(): ProfileDetailsUiState.Ready =
        assertIs<ProfileDetailsUiState.Ready>(awaitItem())

    private suspend fun ReceiveTurbine<ProfileDetailsUiState>.skipLoading() {
        assertEquals(ProfileDetailsUiState.Loading, awaitItem())
        awaitReady()
    }
}
