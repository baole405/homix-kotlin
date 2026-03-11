package com.exe202.nova.ui.screen.resident

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exe202.nova.data.model.BookingStatus
import com.exe202.nova.data.model.ServiceType
import com.exe202.nova.ui.component.BookingStatusChip
import com.exe202.nova.ui.component.EmptyState
import com.exe202.nova.ui.component.ErrorScreen
import com.exe202.nova.ui.component.LoadingScreen
import com.exe202.nova.ui.viewmodel.BookingStatusFilter
import com.exe202.nova.ui.viewmodel.MyBookingsViewModel
import com.exe202.nova.util.toDisplayDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBookingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: MyBookingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val filtered = viewModel.filteredBookings()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lịch sử đặt chỗ") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            LoadingScreen(modifier = Modifier.padding(innerPadding))
            return@Scaffold
        }

        uiState.error?.let { error ->
            if (uiState.allBookings.isEmpty()) {
                ErrorScreen(message = error, onRetry = viewModel::loadBookings, modifier = Modifier.padding(innerPadding))
                return@Scaffold
            }
        }

        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            val filters = BookingStatusFilter.entries.toList()
            val filterLabels = listOf("Tất cả", "Chờ duyệt", "Đã xác nhận", "Bị từ chối", "Đã hủy")

            LazyRow(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filters.size) { index ->
                    FilterChip(
                        selected = uiState.selectedFilter == filters[index],
                        onClick = { viewModel.selectFilter(filters[index]) },
                        label = { Text(filterLabels[index]) }
                    )
                }
            }

            PullToRefreshBox(
                isRefreshing = uiState.isRefreshing,
                onRefresh = viewModel::refresh,
                modifier = Modifier.fillMaxSize()
            ) {
                if (filtered.isEmpty()) {
                    EmptyState("Không có đặt chỗ nào")
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filtered, key = { it.id }) { booking ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            serviceTypeLabel(booking.serviceType),
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        BookingStatusChip(booking.status)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    booking.slotNumber?.let {
                                        Text("Vị trí: $it", style = MaterialTheme.typography.bodySmall)
                                    }
                                    Text(
                                        "Ngày: ${booking.date.toDisplayDate()} | ${booking.startTime} - ${booking.endTime}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    booking.numberOfParticipants?.let {
                                        Text("Số người: $it", style = MaterialTheme.typography.bodySmall)
                                    }
                                    if (booking.status == BookingStatus.PENDING) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        OutlinedButton(
                                            onClick = { viewModel.cancelBooking(booking.id) },
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = ButtonDefaults.outlinedButtonColors(
                                                contentColor = MaterialTheme.colorScheme.error
                                            ),
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                                        ) {
                                            Text("Hủy đặt chỗ")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun serviceTypeLabel(type: ServiceType): String = when (type) {
    ServiceType.SWIMMING_POOL -> "Bể bơi"
    ServiceType.BBQ -> "BBQ"
    ServiceType.PARKING -> "Bãi xe"
}
