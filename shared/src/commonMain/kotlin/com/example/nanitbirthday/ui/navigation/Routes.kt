package com.example.nanitbirthday.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
data object ProfileListRoute

@Serializable
data class ProfileDetailsRoute(val profileId: String)

@Serializable
data class BirthdayRoute(val profileId: String)
