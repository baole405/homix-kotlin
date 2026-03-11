package com.exe202.nova.ui.screen.manager

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exe202.nova.ui.component.ManagerBookingCard
import com.exe202.nova.ui.component.ManagerBillRow
import com.exe202.nova.ui.component.StatCard
import com.exe202.nova.ui.theme.StatusOverdue
import com.exe202.nova.ui.theme.StatusPending
import com.exe202.nova.ui.theme.StatusConfirmed
import com.exe202.nova.data.model.DashboardStats
import com.exe202.nova.ui.viewmodel.ManagerDashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagerDashboardScreen(
    viewModel: ManagerDashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PullToRefreshBox(
        isRefreshing = uiState.isRefreshing,
        onRefresh = { viewModel.refresh() }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Quản lý NOVA", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text("Tổng quan hệ thống", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(
                            Icons.Outlined.ManageAccounts,
                            contentDescription = null,
                            modifier = Modifier.padding(10.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // Stats grid
            item {
                val stats = uiState.stats ?: DashboardStats(0, 0, 0, 0, 0, 0)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatCard(
                            icon = Icons.Outlined.Apartment,
                            label = "Căn hộ",
                            value = "${stats.occupiedApartments}/${stats.totalApartments}",
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            icon = Icons.Outlined.Receipt,
                            label = "HĐ chờ",
                            value = stats.pendingBills.toString(),
                            color = StatusPending,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatCard(
                            icon = Icons.Outlined.CalendarMonth,
                            label = "Đặt chỗ chờ",
                            value = stats.pendingBookings.toString(),
                            color = StatusConfirmed,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            icon = Icons.Outlined.ReportProblem,
                            label = "Khiếu nại",
                            value = stats.pendingComplaints.toString(),
                            color = StatusOverdue,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Pending bookings section
            if (uiState.pendingBookings.isNotEmpty()) {
                item {
                    Text("Booking cần duyệt", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
                items(uiState.pendingBookings) { booking ->
                    ManagerBookingCard(
                        booking = booking,
                        onClick = {}
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.rejectBooking(booking.id) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusOverdue)
                        ) {
                            Text("Từ chối")
                        }
                        Button(
                            onClick = { viewModel.approveBooking(booking.id) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Duyệt")
                        }
                    }
                }
            }

            // Overdue bills section
            if (uiState.overdueBills.isNotEmpty()) {
                item {
                    Text("Hóa đơn quá hạn", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
                items(uiState.overdueBills) { bill ->
                    ManagerBillRow(bill = bill, onClick = {})
                }
            }

            if (uiState.isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}
