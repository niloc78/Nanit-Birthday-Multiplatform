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
import androidx.compose.ui.unit.Dp
import nanitbirthday.shared.generated.resources.Res
import nanitbirthday.shared.generated.resources.baby_picture
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProfilePicture(size: Dp, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Default.Face,
            contentDescription = stringResource(Res.string.baby_picture),
            tint = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.fillMaxSize(0.6f),
        )
    }
}
