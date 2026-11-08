package com.example.nanitbirthday.domain

import kotlinx.datetime.LocalDate
import kotlinx.datetime.monthsUntil

sealed interface Age {
    val value: Int

    data class Months(override val value: Int) : Age

    data class Years(override val value: Int) : Age
}

fun LocalDate.ageOn(today: LocalDate): Age {
    val months = monthsUntil(today).coerceAtLeast(0)
    return if (months < 12) Age.Months(months) else Age.Years(months / 12)
}
