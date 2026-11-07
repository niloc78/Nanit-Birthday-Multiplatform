package com.example.nanitbirthday.ui.profiles

import app.cash.turbine.test
import com.example.nanitbirthday.data.InMemoryBabyProfileRepository
import com.example.nanitbirthday.domain.BabyProfile
import com.example.nanitbirthday.domain.ProfileIdGenerator
import dev.mokkery.answering.returns
import dev.mokkery.answering.sequentially
import dev.mokkery.every
import dev.mokkery.mock
import dev.mokkery.verify
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileListViewModelTest {

    private val idGenerator = mock<ProfileIdGenerator>()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun startsEmptyThenShowsExistingProfiles() = runTest {
        val existing = listOf(BabyProfile(id = "a", name = "Mia"), BabyProfile(id = "b"))
        val viewModel = ProfileListViewModel(InMemoryBabyProfileRepository(existing), idGenerator)

        viewModel.uiState.test {
            assertEquals(emptyList(), awaitItem().profiles)
            assertEquals(existing, awaitItem().profiles)
        }
    }

    @Test
    fun listReactsToRepositoryChanges() = runTest {
        val repository = InMemoryBabyProfileRepository()
        val viewModel = ProfileListViewModel(repository, idGenerator)

        viewModel.uiState.test {
            assertEquals(emptyList(), awaitItem().profiles)

            val mia = BabyProfile(id = "a", name = "Mia")
            repository.save(mia)
            assertEquals(listOf(mia), awaitItem().profiles)

            repository.delete("a")
            assertEquals(emptyList(), awaitItem().profiles)
        }
    }

    @Test
    fun eachNewProfileGetsItsOwnId() = runTest {
        every { idGenerator.next() } sequentially {
            returns("id-1")
            returns("id-2")
        }
        val viewModel = ProfileListViewModel(InMemoryBabyProfileRepository(), idGenerator)

        assertEquals("id-1", viewModel.newProfileId())
        assertEquals("id-2", viewModel.newProfileId())
        verify { idGenerator.next() }
    }
}
