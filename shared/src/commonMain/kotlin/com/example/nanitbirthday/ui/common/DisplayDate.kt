package com.example.nanitbirthday.ui.common

import kotlinx.datetime.LocalDate
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char

private val displayDateFormat = LocalDate.Format {
    monthName(MonthNames.ENGLISH_ABBREVIATED)
    char(' ')
    day()
    chars(", ")
    year()
}

fun LocalDate.toDisplayText(): String = format(displayDateFormat)
