package com.example.nanitbirthday.domain

import kotlinx.datetime.LocalDate

data class BabyProfile(
    val id: String,
    val name: String = "",
    val birthday: LocalDate? = null,
    val pictureFile: String? = null,
) {
    val isComplete: Boolean
        get() = name.isNotBlank() && birthday != null
}
