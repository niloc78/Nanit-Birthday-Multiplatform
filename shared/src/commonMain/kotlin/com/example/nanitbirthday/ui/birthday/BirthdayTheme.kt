package com.example.nanitbirthday.ui.birthday

import androidx.compose.ui.graphics.Color
import nanitbirthday.shared.generated.resources.Res
import nanitbirthday.shared.generated.resources.bg_elephant
import nanitbirthday.shared.generated.resources.bg_fox
import nanitbirthday.shared.generated.resources.bg_pelican
import org.jetbrains.compose.resources.DrawableResource

enum class BirthdayTheme(
    val background: Color,
    val pictureFill: Color,
    val accent: Color,
    val illustration: DrawableResource,
) {
    Fox(
        background = Color(0xFFC5E8DF),
        pictureFill = Color(0xFFA9DCCF),
        accent = Color(0xFF6FC5AF),
        illustration = Res.drawable.bg_fox,
    ),
    Elephant(
        background = Color(0xFFFEEFCD),
        pictureFill = Color(0xFFFEE7B7),
        accent = Color(0xFFFEBE21),
        illustration = Res.drawable.bg_elephant,
    ),
    Pelican(
        background = Color(0xFFDAF1F6),
        pictureFill = Color(0xFFB9E5EF),
        accent = Color(0xFF8BD3E4),
        illustration = Res.drawable.bg_pelican,
    ),
}
