package com.example.nanitbirthday.ui.profiles

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.nanitbirthday.ui.common.ProfilePicture
import com.example.nanitbirthday.ui.common.toDisplayText
import kotlinx.datetime.LocalDate
import nanitbirthday.shared.generated.resources.Res
import nanitbirthday.shared.generated.resources.add_baby
import nanitbirthday.shared.generated.resources.app_title
import nanitbirthday.shared.generated.resources.birthday_not_set
import nanitbirthday.shared.generated.resources.no_babies_body
import nanitbirthday.shared.generated.resources.no_babies_title
import nanitbirthday.shared.generated.resources.unnamed_baby
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileListScreen(
    onOpenProfile: (String) -> Unit,
    viewModel: ProfileListViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    ProfileListScreen(
        state = state,
        onAddBaby = { onOpenProfile(viewModel.newProfileId()) },
        onOpenProfile = onOpenProfile,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileListScreen(
    state: ProfileListUiState,
    onAddBaby: () -> Unit,
    onOpenProfile: (String) -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text(stringResource(Res.string.app_title)) })
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddBaby,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text(stringResource(Res.string.add_baby)) },
            )
        },
    ) { padding ->
        if (state.profiles.isEmpty()) {
            EmptyProfiles(padding)
        } else {
            ProfileList(state.profiles, padding, onOpenProfile)
        }
    }
}

@Composable
private fun ProfileList(
    profiles: List<ProfileListItem>,
    padding: PaddingValues,
    onOpenProfile: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = padding.calculateTopPadding(),
            bottom = padding.calculateBottomPadding() + 88.dp,
        ),
    ) {
        items(profiles, key = { it.id }) { profile ->
            ProfileRow(profile, onClick = { onOpenProfile(profile.id) })
            HorizontalDivider()
        }
    }
}

@Composable
private fun ProfileRow(profile: ProfileListItem, onClick: () -> Unit) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        leadingContent = { ProfilePicture(picturePath = profile.picturePath, size = 48.dp) },
        headlineContent = {
            Text(profile.name.ifBlank { stringResource(Res.string.unnamed_baby) })
        },
        supportingContent = {
            Text(profile.birthday?.toDisplayText() ?: stringResource(Res.string.birthday_not_set))
        },
    )
}

@Composable
private fun EmptyProfiles(padding: PaddingValues) {
    Column(
        modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.no_babies_title),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(Res.string.no_babies_body),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview
@Composable
private fun ProfileListScreenPreview() {
    MaterialTheme {
        ProfileListScreen(
            state = ProfileListUiState(
                profiles = listOf(
                    ProfileListItem(id = "a", name = "Mia", birthday = LocalDate(2026, 2, 1), picturePath = null),
                    ProfileListItem(id = "b", name = "", birthday = null, picturePath = null),
                ),
            ),
            onAddBaby = {},
            onOpenProfile = {},
        )
    }
}

@Preview
@Composable
private fun EmptyProfileListScreenPreview() {
    MaterialTheme {
        ProfileListScreen(state = ProfileListUiState(), onAddBaby = {}, onOpenProfile = {})
    }
}
