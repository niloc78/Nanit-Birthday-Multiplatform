package com.example.nanitbirthday.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.datetime.LocalDate

class AgeTest {

    private val today = LocalDate(2026, 11, 7)

    @Test
    fun bornTodayIsZeroMonths() {
        assertEquals(Age.Months(0), today.ageOn(today))
    }

    @Test
    fun exactlyOneMonth() {
        assertEquals(Age.Months(1), LocalDate(2026, 10, 7).ageOn(today))
    }

    @Test
    fun oneMonthAndTwentyNineDaysRoundsDown() {
        assertEquals(Age.Months(1), LocalDate(2026, 9, 8).ageOn(today))
    }

    @Test
    fun dayBeforeFirstBirthdayIsElevenMonths() {
        assertEquals(Age.Months(11), LocalDate(2025, 11, 8).ageOn(today))
    }

    @Test
    fun firstBirthdayIsOneYear() {
        assertEquals(Age.Years(1), LocalDate(2025, 11, 7).ageOn(today))
    }

    @Test
    fun twentyThreeMonthsIsOneYear() {
        assertEquals(Age.Years(1), LocalDate(2024, 12, 7).ageOn(today))
    }

    @Test
    fun twentyFourMonthsIsTwoYears() {
        assertEquals(Age.Years(2), LocalDate(2024, 11, 7).ageOn(today))
    }

    @Test
    fun endOfJanuaryToEndOfFebruaryIsZeroMonths() {
        assertEquals(Age.Months(0), LocalDate(2026, 1, 31).ageOn(LocalDate(2026, 2, 28)))
    }

    @Test
    fun leapDayBirthdayBeforeAnniversary() {
        assertEquals(Age.Months(11), LocalDate(2024, 2, 29).ageOn(LocalDate(2025, 2, 28)))
    }

    @Test
    fun leapDayBirthdayAfterAnniversary() {
        assertEquals(Age.Years(1), LocalDate(2024, 2, 29).ageOn(LocalDate(2025, 3, 1)))
    }

    @Test
    fun futureBirthdayClampsToZeroMonths() {
        assertEquals(Age.Months(0), LocalDate(2026, 11, 8).ageOn(today))
    }
}
