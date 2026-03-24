package com.exe202.nova.ui.screen.resident

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MapScreen() {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(24.dp),
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Text("Ban do tam thoi dang tat", style = MaterialTheme.typography.titleMedium)
		Text(
			"Tinh nang ban do se duoc bat lai sau.",
			style = MaterialTheme.typography.bodyMedium,
			modifier = Modifier.padding(top = 8.dp)
		)
	}
}
