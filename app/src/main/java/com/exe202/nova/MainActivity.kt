package com.exe202.nova

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exe202.nova.data.local.ThemePreferenceManager
import com.exe202.nova.ui.navigation.NovaNavHost
import com.exe202.nova.ui.theme.LocalIsDarkTheme
import com.exe202.nova.ui.theme.LocalToggleTheme
import com.exe202.nova.ui.theme.NovaTheme
import com.exe202.nova.util.SystemNotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var themePreferenceManager: ThemePreferenceManager

    @Inject
    lateinit var systemNotificationHelper: SystemNotificationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        systemNotificationHelper.ensureChannels()
        requestNotificationPermissionIfNeeded()
        enableEdgeToEdge()
        setContent {
            val isDarkTheme by themePreferenceManager.isDarkTheme
                .collectAsStateWithLifecycle(initialValue = false)
            val scope = rememberCoroutineScope()

            CompositionLocalProvider(
                LocalIsDarkTheme provides isDarkTheme,
                LocalToggleTheme provides { scope.launch { themePreferenceManager.setDarkTheme(!isDarkTheme) } }
            ) {
                NovaTheme(isDarkTheme = isDarkTheme) {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        NovaNavHost()
                    }
                }
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        val granted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        if (!granted) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                REQUEST_NOTIFICATIONS_CODE
            )
        }
    }

    companion object {
        private const val REQUEST_NOTIFICATIONS_CODE = 1001
    }
}
