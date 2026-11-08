package com.example.nanitbirthday.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import nanitbirthday.shared.generated.resources.Res
import nanitbirthday.shared.generated.resources.baby_picture
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProfilePicture(picturePath: String?, size: Dp, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        if (picturePath != null) {
            AsyncImage(
                model = picturePath,
                contentDescription = stringResource(Res.string.baby_picture),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Icon(
                imageVector = Icons.Default.Face,
                contentDescription = stringResource(Res.string.baby_picture),
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.fillMaxSize(0.6f),
            )
        }
    }
}

@Preview
@Composable
private fun ProfilePicturePlaceholderPreview() {
    MaterialTheme {
        ProfilePicture(picturePath = null, size = 120.dp)
    }
}
