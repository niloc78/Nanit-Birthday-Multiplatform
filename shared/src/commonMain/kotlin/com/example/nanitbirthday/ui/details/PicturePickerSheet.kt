package com.example.nanitbirthday.ui.details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import nanitbirthday.shared.generated.resources.Res
import nanitbirthday.shared.generated.resources.choose_from_gallery
import nanitbirthday.shared.generated.resources.ic_photo_camera
import nanitbirthday.shared.generated.resources.ic_photo_library
import nanitbirthday.shared.generated.resources.take_photo
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PicturePickerSheet(
    onPickFromGallery: () -> Unit,
    onTakePhoto: () -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        PictureSources(
            onPickFromGallery = {
                onDismiss()
                onPickFromGallery()
            },
            onTakePhoto = {
                onDismiss()
                onTakePhoto()
            },
        )
    }
}

@Composable
private fun PictureSources(onPickFromGallery: () -> Unit, onTakePhoto: () -> Unit) {
    Column {
        PictureSourceItem(
            label = Res.string.choose_from_gallery,
            icon = Res.drawable.ic_photo_library,
            onClick = onPickFromGallery,
        )
        PictureSourceItem(
            label = Res.string.take_photo,
            icon = Res.drawable.ic_photo_camera,
            onClick = onTakePhoto,
        )
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun PictureSourceItem(label: StringResource, icon: DrawableResource, onClick: () -> Unit) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        leadingContent = { Icon(painterResource(icon), contentDescription = null) },
        headlineContent = { Text(stringResource(label)) },
    )
}

@Preview
@Composable
private fun PictureSourcesPreview() {
    MaterialTheme {
        PictureSources(onPickFromGallery = {}, onTakePhoto = {})
    }
}
