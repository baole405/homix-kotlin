package com.exe202.nova.ui.screen.resident

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.LocalParking
import androidx.compose.material.icons.outlined.Pool
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exe202.nova.data.model.SlotStatus
import com.exe202.nova.ui.viewmodel.FacilitiesViewModel
import com.exe202.nova.util.toVndFormat

@Composable
fun FacilitiesScreen(
    onNavigateToBooking: () -> Unit,
    viewModel: FacilitiesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Pool
        FacilitySectionCard(
            title = "Hồ bơi",
            icon = { Icon(Icons.Outlined.Pool, contentDescription = null) },
            available = uiState.poolSlots.count { it.status == SlotStatus.AVAILABLE },
            total = uiState.poolSlots.size,
            detail = uiState.poolSlots.firstOrNull()?.let {
                "${it.openTime} – ${it.closeTime} • ${it.pricePerHour.toVndFormat()}/giờ"
            } ?: "",
            onBook = onNavigateToBooking
        )

        // BBQ
        FacilitySectionCard(
            title = "Khu BBQ",
            icon = { Icon(Icons.Outlined.CalendarMonth, contentDescription = null) },
            available = uiState.bbqSlots.count { it.status == SlotStatus.AVAILABLE },
            total = uiState.bbqSlots.size,
            detail = uiState.bbqSlots.firstOrNull()?.let {
                "${it.pricePerHour.toVndFormat()}/giờ"
            } ?: "",
            onBook = onNavigateToBooking
        )

        // Parking
        FacilitySectionCard(
            title = "Bãi đỗ xe",
            icon = { Icon(Icons.Outlined.LocalParking, contentDescription = null) },
            available = uiState.parkingSlots.count { it.status == SlotStatus.AVAILABLE },
            total = uiState.parkingSlots.size,
            detail = uiState.parkingSlots.firstOrNull()?.let {
                "${it.pricePerDay.toVndFormat()}/ngày"
            } ?: "",
            onBook = onNavigateToBooking
        )
    }
}

@Composable
private fun FacilitySectionCard(
    title: String,
    icon: @Composable () -> Unit,
    available: Int,
    total: Int,
    detail: String,
    onBook: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    icon()
                    Text(title, style = MaterialTheme.typography.titleMedium)
                }
                Text(
                    "$available/$total trống",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (available > 0) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.error
                )
            }
            if (detail.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(detail, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(12.dp))
            FilledTonalButton(
                onClick = onBook,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Đặt chỗ")
            }
        }
    }
}
