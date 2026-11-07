package com.example.nanitbirthday

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.nanitbirthday.ui.birthday.BirthdayScreen
import com.example.nanitbirthday.ui.details.ProfileDetailsScreen
import com.example.nanitbirthday.ui.navigation.BirthdayRoute
import com.example.nanitbirthday.ui.navigation.ProfileDetailsRoute
import com.example.nanitbirthday.ui.navigation.ProfileListRoute
import com.example.nanitbirthday.ui.profiles.ProfileListScreen

@Composable
fun App() {
    MaterialTheme {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = ProfileListRoute) {
            composable<ProfileListRoute> {
                ProfileListScreen(
                    onOpenProfile = { profileId ->
                        navController.navigate(ProfileDetailsRoute(profileId))
                    },
                )
            }
            composable<ProfileDetailsRoute> { entry ->
                val route = entry.toRoute<ProfileDetailsRoute>()
                ProfileDetailsScreen(
                    profileId = route.profileId,
                    onBack = { navController.popBackStack() },
                    onShowBirthday = { navController.navigate(BirthdayRoute(route.profileId)) },
                )
            }
            composable<BirthdayRoute> {
                BirthdayScreen(onClose = { navController.popBackStack() })
            }
        }
    }
}
