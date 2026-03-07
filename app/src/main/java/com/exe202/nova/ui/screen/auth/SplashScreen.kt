package com.exe202.nova.ui.screen.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exe202.nova.ui.viewmodel.SplashDestination
import com.exe202.nova.ui.viewmodel.SplashViewModel

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToResident: () -> Unit,
    onNavigateToManager: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val destination by viewModel.destination.collectAsStateWithLifecycle()

    LaunchedEffect(destination) {
        when (destination) {
            SplashDestination.Login -> onNavigateToLogin()
            SplashDestination.Resident -> onNavigateToResident()
            SplashDestination.Manager -> onNavigateToManager()
            SplashDestination.Loading -> { /* wait */ }
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}
