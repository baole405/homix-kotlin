package com.exe202.nova.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.exe202.nova.ui.screen.auth.LoginScreen
import com.exe202.nova.ui.screen.auth.RegisterScreen
import com.exe202.nova.ui.screen.auth.SplashScreen
import com.exe202.nova.ui.screen.manager.ManagerMainScreen
import com.exe202.nova.ui.screen.resident.ResidentMainScreen

@Composable
fun NovaNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = SplashRoute) {
        composable<SplashRoute> {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(LoginRoute) {
                        popUpTo(SplashRoute) { inclusive = true }
                    }
                },
                onNavigateToResident = {
                    navController.navigate(ResidentMainRoute) {
                        popUpTo(SplashRoute) { inclusive = true }
                    }
                },
                onNavigateToManager = {
                    navController.navigate(ManagerMainRoute) {
                        popUpTo(SplashRoute) { inclusive = true }
                    }
                }
            )
        }

        composable<LoginRoute> {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(RegisterRoute) },
                onNavigateToResident = {
                    navController.navigate(ResidentMainRoute) {
                        popUpTo(LoginRoute) { inclusive = true }
                    }
                },
                onNavigateToManager = {
                    navController.navigate(ManagerMainRoute) {
                        popUpTo(LoginRoute) { inclusive = true }
                    }
                }
            )
        }

        composable<RegisterRoute> {
            RegisterScreen(
                onNavigateToLogin = { navController.popBackStack() },
                onNavigateToResident = {
                    navController.navigate(ResidentMainRoute) {
                        popUpTo(LoginRoute) { inclusive = true }
                    }
                }
            )
        }

        composable<ResidentMainRoute> {
            ResidentMainScreen(
                onLogout = {
                    navController.navigate(LoginRoute) {
                        popUpTo(ResidentMainRoute) { inclusive = true }
                    }
                }
            )
        }

        composable<ManagerMainRoute> {
            ManagerMainScreen(onLogout = {
                navController.navigate(LoginRoute) {
                    popUpTo(ManagerMainRoute) { inclusive = true }
                }
            })
        }
    }
}
