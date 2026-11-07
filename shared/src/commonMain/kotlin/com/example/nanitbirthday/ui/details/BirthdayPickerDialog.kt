package com.example.nanitbirthday.ui.details

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import nanitbirthday.shared.generated.resources.Res
import nanitbirthday.shared.generated.resources.cancel
import nanitbirthday.shared.generated.resources.ok
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BirthdayPickerDialog(
    initialBirthday: LocalDate?,
    latestBirthday: LocalDate,
    onConfirm: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
) {
    val latestMillis = latestBirthday.toEpochMillis()
    val pickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialBirthday?.toEpochMillis(),
        yearRange = (latestBirthday.year - 100)..latestBirthday.year,
        selectableDates = remember(latestMillis) {
            object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean = utcTimeMillis <= latestMillis
                override fun isSelectableYear(year: Int): Boolean = year <= latestBirthday.year
            }
        },
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    pickerState.selectedDateMillis?.let { onConfirm(it.toLocalDate()) }
                    onDismiss()
                },
                enabled = pickerState.selectedDateMillis != null,
            ) {
                Text(stringResource(Res.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.cancel))
            }
        },
    ) {
        DatePicker(state = pickerState)
    }
}

private fun LocalDate.toEpochMillis(): Long = atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()

private fun Long.toLocalDate(): LocalDate =
    Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.UTC).date
