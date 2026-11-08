package com.example.nanitbirthday.ui.details

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.nanitbirthday.ui.common.ProfilePicture
import com.example.nanitbirthday.ui.common.toDisplayText
import kotlinx.datetime.LocalDate
import nanitbirthday.shared.generated.resources.Res
import nanitbirthday.shared.generated.resources.app_title
import nanitbirthday.shared.generated.resources.back
import nanitbirthday.shared.generated.resources.birthday_label
import nanitbirthday.shared.generated.resources.change_picture
import nanitbirthday.shared.generated.resources.delete_baby
import nanitbirthday.shared.generated.resources.ic_photo_camera
import nanitbirthday.shared.generated.resources.name_label
import nanitbirthday.shared.generated.resources.select_birthday
import nanitbirthday.shared.generated.resources.show_birthday_screen
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ProfileDetailsScreen(
    profileId: String,
    onBack: () -> Unit,
    onShowBirthday: () -> Unit,
    viewModel: ProfileDetailsViewModel = koinViewModel { parametersOf(profileId) },
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    ProfileDetailsScreen(
        state = state,
        onBack = onBack,
        onDelete = {
            viewModel.onDelete()
            onBack()
        },
        onNameChange = viewModel::onNameChange,
        onBirthdayChange = viewModel::onBirthdayChange,
        onPickFromGallery = viewModel::onPickFromGallery,
        onTakePhoto = viewModel::onTakePhoto,
        onShowBirthday = onShowBirthday,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileDetailsScreen(
    state: ProfileDetailsUiState,
    onBack: () -> Unit,
    onDelete: () -> Unit,
    onNameChange: (String) -> Unit,
    onBirthdayChange: (LocalDate) -> Unit,
    onPickFromGallery: () -> Unit,
    onTakePhoto: () -> Unit,
    onShowBirthday: () -> Unit,
) {
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
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(Res.string.delete_baby),
                        )
                    }
                },
            )
        },
    ) { padding ->
        when (state) {
            ProfileDetailsUiState.Loading -> Box(Modifier.fillMaxSize().padding(padding))
            is ProfileDetailsUiState.Ready -> ProfileDetailsContent(
                state = state,
                onNameChange = onNameChange,
                onBirthdayChange = onBirthdayChange,
                onPickFromGallery = onPickFromGallery,
                onTakePhoto = onTakePhoto,
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
    onPickFromGallery: () -> Unit,
    onTakePhoto: () -> Unit,
    onShowBirthday: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showBirthdayPicker by rememberSaveable { mutableStateOf(false) }
    var showPicturePicker by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        EditablePicture(
            picturePath = state.picturePath,
            onClick = { showPicturePicker = true },
        )
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

    if (showPicturePicker) {
        PicturePickerSheet(
            onPickFromGallery = onPickFromGallery,
            onTakePhoto = onTakePhoto,
            onDismiss = { showPicturePicker = false },
        )
    }
}

@Composable
private fun EditablePicture(picturePath: String?, onClick: () -> Unit) {
    val changePicture = stringResource(Res.string.change_picture)
    Box {
        ProfilePicture(
            picturePath = picturePath,
            size = 120.dp,
            modifier = Modifier
                .clip(CircleShape)
                .clickable(onClickLabel = changePicture, onClick = onClick)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
        )
        Surface(
            onClick = onClick,
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 4.dp)
                .size(32.dp),
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_photo_camera),
                contentDescription = changePicture,
                modifier = Modifier.padding(7.dp),
            )
        }
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

@Preview
@Composable
private fun NewProfileDetailsScreenPreview() {
    MaterialTheme {
        ProfileDetailsScreen(
            state = ProfileDetailsUiState.Ready(
                name = "",
                birthday = null,
                picturePath = null,
                latestBirthday = LocalDate(2026, 11, 7),
                canShowBirthday = false,
            ),
            onBack = {},
            onDelete = {},
            onNameChange = {},
            onBirthdayChange = {},
            onPickFromGallery = {},
            onTakePhoto = {},
            onShowBirthday = {},
        )
    }
}

@Preview
@Composable
private fun CompleteProfileDetailsScreenPreview() {
    MaterialTheme {
        ProfileDetailsScreen(
            state = ProfileDetailsUiState.Ready(
                name = "Mia",
                birthday = LocalDate(2026, 2, 1),
                picturePath = null,
                latestBirthday = LocalDate(2026, 11, 7),
                canShowBirthday = true,
            ),
            onBack = {},
            onDelete = {},
            onNameChange = {},
            onBirthdayChange = {},
            onPickFromGallery = {},
            onTakePhoto = {},
            onShowBirthday = {},
        )
    }
}
