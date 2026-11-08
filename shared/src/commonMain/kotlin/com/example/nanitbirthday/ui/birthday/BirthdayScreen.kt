package com.example.nanitbirthday.ui.birthday

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.nanitbirthday.domain.Age
import com.example.nanitbirthday.ui.details.PicturePickerSheet
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlinx.coroutines.launch
import nanitbirthday.shared.generated.resources.Res
import nanitbirthday.shared.generated.resources.baby_picture
import nanitbirthday.shared.generated.resources.baby_placeholder
import nanitbirthday.shared.generated.resources.change_picture
import nanitbirthday.shared.generated.resources.close
import nanitbirthday.shared.generated.resources.ic_camera_add
import nanitbirthday.shared.generated.resources.months_old
import nanitbirthday.shared.generated.resources.nanit_logo
import nanitbirthday.shared.generated.resources.number_0
import nanitbirthday.shared.generated.resources.number_1
import nanitbirthday.shared.generated.resources.number_10
import nanitbirthday.shared.generated.resources.number_11
import nanitbirthday.shared.generated.resources.number_12
import nanitbirthday.shared.generated.resources.number_2
import nanitbirthday.shared.generated.resources.number_3
import nanitbirthday.shared.generated.resources.number_4
import nanitbirthday.shared.generated.resources.number_5
import nanitbirthday.shared.generated.resources.number_6
import nanitbirthday.shared.generated.resources.number_7
import nanitbirthday.shared.generated.resources.number_8
import nanitbirthday.shared.generated.resources.number_9
import nanitbirthday.shared.generated.resources.share_the_news
import nanitbirthday.shared.generated.resources.swirls_left
import nanitbirthday.shared.generated.resources.today_name_is
import nanitbirthday.shared.generated.resources.years_old
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

private val TextColor = Color(0xFF394562)
private val TitleStyle = TextStyle(
    color = TextColor,
    fontSize = 21.sp,
    fontWeight = FontWeight.Medium,
    letterSpacing = (-0.42).sp,
    textAlign = TextAlign.Center,
)
private val HeaderMinTopGap = 20.dp
private val HeaderHorizontalPadding = 56.dp
private val TitleToNumeral = 13.dp
private val NumeralToUnit = 14.dp
private val SwirlToNumeral = 22.dp
private val FaceSize = 113.dp
private val RingStroke = 5.38.dp
private val PictureSize = 200.dp
private val PictureMinTopGap = 15.dp
private val PictureMinSidePadding = 50.dp
private val FrameWidth = 360.dp
private val FrameHeight = 728.dp
private val PictureToLogo = 15.dp
private val CameraBadgeSize = 36.dp
private const val CameraBadgeAngle = PI / 4
private val CloseButtonPadding = 8.dp
private val ShareButtonBottomPadding = 16.dp

private val numerals = listOf(
    Res.drawable.number_0, Res.drawable.number_1, Res.drawable.number_2, Res.drawable.number_3,
    Res.drawable.number_4, Res.drawable.number_5, Res.drawable.number_6, Res.drawable.number_7,
    Res.drawable.number_8, Res.drawable.number_9, Res.drawable.number_10, Res.drawable.number_11,
    Res.drawable.number_12,
)

private fun numeralsFor(value: Int): List<DrawableResource> =
    if (value < numerals.size) listOf(numerals[value]) else value.toString().map { numerals[it.digitToInt()] }

@Composable
fun BirthdayScreen(
    profileId: String,
    onClose: () -> Unit,
    viewModel: BirthdayViewModel = koinViewModel { parametersOf(profileId) },
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    BirthdayScreen(
        state = state,
        onClose = onClose,
        onPickFromGallery = viewModel::onPickFromGallery,
        onTakePhoto = viewModel::onTakePhoto,
        onShare = viewModel::onShare,
    )
}

@Composable
private fun BirthdayScreen(
    state: BirthdayUiState,
    onClose: () -> Unit,
    onPickFromGallery: () -> Unit,
    onTakePhoto: () -> Unit,
    onShare: (ImageBitmap) -> Unit,
) {
    var showPicturePicker by rememberSaveable { mutableStateOf(false) }
    var pictureBounds by remember { mutableStateOf<IntRect?>(null) }
    val cardLayer = rememberGraphicsLayer()
    val scope = rememberCoroutineScope()
    val background = (state as? BirthdayUiState.Ready)?.theme?.background ?: MaterialTheme.colorScheme.background
    Box(modifier = Modifier.fillMaxSize().background(background)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .drawWithContent {
                    cardLayer.record { this@drawWithContent.drawContent() }
                    drawLayer(cardLayer)
                }
                .background(background),
        ) {
            if (state is BirthdayUiState.Ready) {
                BirthdayContent(state = state, onPicturePlaced = { pictureBounds = it })
            }
        }
        if (state is BirthdayUiState.Ready) {
            CardControls(
                theme = state.theme,
                pictureBounds = pictureBounds,
                onChangePicture = { showPicturePicker = true },
                onShare = { scope.launch { onShare(cardLayer.toImageBitmap()) } },
            )
        }
        IconButton(
            onClick = onClose,
            modifier = Modifier.align(Alignment.TopStart).safeDrawingPadding().padding(CloseButtonPadding),
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(Res.string.close),
                tint = TextColor,
            )
        }
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
private fun BirthdayContent(state: BirthdayUiState.Ready, onPicturePlaced: (IntRect) -> Unit) {
    Image(
        painter = painterResource(state.theme.illustration),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize(),
        alignment = Alignment.BottomCenter,
    )
    CardScale {
        BirthdayCard(state = state, onPicturePlaced = onPicturePlaced)
    }
}

@Composable
private fun CardScale(content: @Composable () -> Unit) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
        val scale = minOf(1f, maxWidth / FrameWidth, maxHeight / FrameHeight)
        val density = LocalDensity.current
        CompositionLocalProvider(LocalDensity provides Density(density.density * scale, density.fontScale), content)
    }
}

@Composable
private fun CardControls(
    theme: BirthdayTheme,
    pictureBounds: IntRect?,
    onChangePicture: () -> Unit,
    onShare: () -> Unit,
) {
    CardScale {
        if (pictureBounds != null) {
            CameraBadge(
                theme = theme,
                onClick = onChangePicture,
                modifier = Modifier.offset { cameraBadgeOffset(pictureBounds) },
            )
        }
    }
    Box(modifier = Modifier.fillMaxSize().navigationBarsPadding().padding(bottom = ShareButtonBottomPadding)) {
        ExtendedFloatingActionButton(
            onClick = onShare,
            containerColor = theme.accent,
            contentColor = Color.White,
            icon = { Icon(imageVector = Icons.Default.Share, contentDescription = null) },
            text = { Text(stringResource(Res.string.share_the_news)) },
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

private fun Density.cameraBadgeOffset(picture: IntRect): IntOffset {
    val ringRadius = (picture.width - RingStroke.toPx()) / 2
    val badge = CameraBadgeSize.toPx()
    return IntOffset(
        (picture.center.x + ringRadius * cos(CameraBadgeAngle) - badge / 2).roundToInt(),
        (picture.center.y - ringRadius * sin(CameraBadgeAngle) - badge / 2).roundToInt(),
    )
}

@Composable
private fun BirthdayCard(state: BirthdayUiState.Ready, onPicturePlaced: (IntRect) -> Unit) {
    Layout(
        contents = listOf(
            { AgeHeader(name = state.name, age = state.age) },
            {
                BabyPicture(
                    picturePath = state.picturePath,
                    theme = state.theme,
                    modifier = Modifier.onPlaced { onPicturePlaced(IntRect(it.positionInParent().round(), it.size)) },
                )
            },
            { Image(painter = painterResource(Res.drawable.nanit_logo), contentDescription = null) },
        ),
        modifier = Modifier.fillMaxSize(),
    ) { (header, picture, logo), constraints ->
        val loose = constraints.copy(minWidth = 0, minHeight = 0)
        val pictureSize = minOf(PictureSize.roundToPx(), constraints.maxWidth - 2 * PictureMinSidePadding.roundToPx())
        val headerPlaceable = header.first().measure(loose)
        val picturePlaceable = picture.first().measure(Constraints.fixed(pictureSize, pictureSize))
        val logoPlaceable = logo.first().measure(loose)
        val pictureWithLogoHeight = pictureSize + PictureToLogo.roundToPx() + logoPlaceable.height
        val pictureY = maxOf(
            (constraints.maxHeight - pictureWithLogoHeight) / 2,
            HeaderMinTopGap.roundToPx() + headerPlaceable.height + PictureMinTopGap.roundToPx(),
        )
        val headerY = (pictureY - headerPlaceable.height) / 2
        layout(constraints.maxWidth, constraints.maxHeight) {
            headerPlaceable.placeRelative((constraints.maxWidth - headerPlaceable.width) / 2, headerY)
            picturePlaceable.placeRelative((constraints.maxWidth - pictureSize) / 2, pictureY)
            logoPlaceable.placeRelative(
                (constraints.maxWidth - logoPlaceable.width) / 2,
                pictureY + pictureSize + PictureToLogo.roundToPx(),
            )
        }
    }
}

@Composable
private fun AgeHeader(name: String, age: Age) {
    Column(
        modifier = Modifier.padding(horizontal = HeaderHorizontalPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.today_name_is, name.uppercase()),
            style = TitleStyle,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(TitleToNumeral))
        Row(
            horizontalArrangement = Arrangement.spacedBy(SwirlToNumeral),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.semantics { contentDescription = age.value.toString() },
        ) {
            Image(painter = painterResource(Res.drawable.swirls_left), contentDescription = null)
            Row {
                numeralsFor(age.value).forEach { numeral ->
                    Image(painter = painterResource(numeral), contentDescription = null)
                }
            }
            Image(
                painter = painterResource(Res.drawable.swirls_left),
                contentDescription = null,
                modifier = Modifier.scale(scaleX = -1f, scaleY = 1f),
            )
        }
        Spacer(Modifier.height(NumeralToUnit))
        Text(
            text = pluralStringResource(if (age is Age.Months) Res.plurals.months_old else Res.plurals.years_old, age.value),
            style = TitleStyle,
        )
    }
}

@Composable
private fun BabyPicture(picturePath: String?, theme: BirthdayTheme, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize().clip(CircleShape).background(theme.pictureFill),
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
            Image(
                painter = painterResource(Res.drawable.baby_placeholder),
                contentDescription = stringResource(Res.string.baby_picture),
                colorFilter = ColorFilter.tint(theme.accent),
                modifier = Modifier.fillMaxSize(FaceSize / PictureSize),
            )
        }
        Box(Modifier.matchParentSize().border(RingStroke, theme.accent, CircleShape))
    }
}

@Composable
private fun CameraBadge(theme: BirthdayTheme, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(Res.drawable.ic_camera_add),
        contentDescription = stringResource(Res.string.change_picture),
        modifier = modifier
            .size(CameraBadgeSize)
            .clip(CircleShape)
            .background(theme.accent)
            .clickable(role = Role.Button, onClick = onClick),
    )
}

@Preview(name = "Figma frame", widthDp = 360, heightDp = 728)
@Preview(name = "Small phone", widthDp = 360, heightDp = 640)
@Preview(name = "Compact phone", widthDp = 320, heightDp = 568)
@Preview(name = "Narrow phone", widthDp = 280, heightDp = 653)
private annotation class BirthdayScreenPreviews

@BirthdayScreenPreviews
@Composable
private fun BirthdayScreenFoxPreview() {
    MaterialTheme {
        BirthdayScreen(
            state = BirthdayUiState.Ready("Mia", Age.Months(5), null, BirthdayTheme.Fox),
            onClose = {},
            onPickFromGallery = {},
            onTakePhoto = {},
            onShare = {},
        )
    }
}

@BirthdayScreenPreviews
@Composable
private fun BirthdayScreenElephantPreview() {
    MaterialTheme {
        BirthdayScreen(
            state = BirthdayUiState.Ready("Alexander Benjamin", Age.Years(1), null, BirthdayTheme.Elephant),
            onClose = {},
            onPickFromGallery = {},
            onTakePhoto = {},
            onShare = {},
        )
    }
}

@BirthdayScreenPreviews
@Composable
private fun BirthdayScreenPelicanPreview() {
    MaterialTheme {
        BirthdayScreen(
            state = BirthdayUiState.Ready("Noa", Age.Years(13), null, BirthdayTheme.Pelican),
            onClose = {},
            onPickFromGallery = {},
            onTakePhoto = {},
            onShare = {},
        )
    }
}
