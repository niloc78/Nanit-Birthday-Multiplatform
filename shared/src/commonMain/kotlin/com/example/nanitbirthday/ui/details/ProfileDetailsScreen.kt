package com.example.nanitbirthday.ui.details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.nanitbirthday.ui.common.ProfilePicture
import com.example.nanitbirthday.ui.common.toDisplayText
import kotlinx.datetime.LocalDate
import nanitbirthday.shared.generated.resources.Res
import nanitbirthday.shared.generated.resources.app_title
import nanitbirthday.shared.generated.resources.back
import nanitbirthday.shared.generated.resources.birthday_label
import nanitbirthday.shared.generated.resources.delete_baby
import nanitbirthday.shared.generated.resources.name_label
import nanitbirthday.shared.generated.resources.select_birthday
import nanitbirthday.shared.generated.resources.show_birthday_screen
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDetailsScreen(
    profileId: String,
    onBack: () -> Unit,
    onShowBirthday: () -> Unit,
    viewModel: ProfileDetailsViewModel = koinViewModel { parametersOf(profileId) },
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.app_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.back),
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.onDelete()
                            onBack()
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(Res.string.delete_baby),
                        )
                    }
                },
            )
        },
    ) { padding ->
        when (val current = state) {
            ProfileDetailsUiState.Loading -> Box(Modifier.fillMaxSize().padding(padding))
            is ProfileDetailsUiState.Ready -> ProfileDetailsContent(
                state = current,
                onNameChange = viewModel::onNameChange,
                onBirthdayChange = viewModel::onBirthdayChange,
                onShowBirthday = onShowBirthday,
                modifier = Modifier.padding(padding),
            )
        }
    }
}

@Composable
private fun ProfileDetailsContent(
    state: ProfileDetailsUiState.Ready,
    onNameChange: (String) -> Unit,
    onBirthdayChange: (LocalDate) -> Unit,
    onShowBirthday: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showBirthdayPicker by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ProfilePicture(size = 120.dp)
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            value = state.name,
            onValueChange = onNameChange,
            label = { Text(stringResource(Res.string.name_label)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(16.dp))
        BirthdayField(
            birthday = state.birthday,
            onClick = { showBirthdayPicker = true },
        )
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = onShowBirthday,
            enabled = state.canShowBirthday,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(Res.string.show_birthday_screen))
        }
    }

    if (showBirthdayPicker) {
        BirthdayPickerDialog(
            initialBirthday = state.birthday,
            latestBirthday = state.latestBirthday,
            onConfirm = onBirthdayChange,
            onDismiss = { showBirthdayPicker = false },
        )
    }
}

@Composable
private fun BirthdayField(birthday: LocalDate?, onClick: () -> Unit) {
    Box(Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = birthday?.toDisplayText().orEmpty(),
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(Res.string.birthday_label)) },
            placeholder = { Text(stringResource(Res.string.select_birthday)) },
            trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Box(
            Modifier
                .matchParentSize()
                .clickable(onClick = onClick),
        )
    }
}
