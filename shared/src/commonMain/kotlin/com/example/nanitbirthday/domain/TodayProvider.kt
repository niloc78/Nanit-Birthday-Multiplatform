package com.example.nanitbirthday.domain

import kotlin.time.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun interface TodayProvider {
    fun today(): LocalDate
}

class SystemTodayProvider : TodayProvider {
    override fun today(): LocalDate =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
}
