package com.exe202.nova

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var themePreferenceManager: ThemePreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
}
