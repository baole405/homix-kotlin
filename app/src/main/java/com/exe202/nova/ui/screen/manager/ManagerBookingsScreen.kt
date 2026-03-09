package com.exe202.nova.ui.screen.manager

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilterList
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
import com.exe202.nova.data.model.BookingStatus
import com.exe202.nova.data.model.ManagerBooking
import com.exe202.nova.data.model.ServiceType
import com.exe202.nova.ui.component.BookingStatusChip
import com.exe202.nova.ui.component.ManagerBookingCard
import com.exe202.nova.ui.theme.StatusOverdue
import com.exe202.nova.ui.viewmodel.ManagerBookingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagerBookingsScreen(
    viewModel: ManagerBookingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var isRefreshing by remember { mutableStateOf(false) }

    val statusOptions = listOf(null to "Tất cả") + BookingStatus.entries.map { it to when (it) {
        BookingStatus.PENDING -> "Chờ duyệt"
        BookingStatus.CONFIRMED -> "Đã xác nhận"
        BookingStatus.REJECTED -> "Từ chối"
        BookingStatus.CANCELLED -> "Đã hủy"
    }}
    val serviceOptions = listOf(null to "Tất cả") + ServiceType.entries.map { it to when (it) {
        ServiceType.SWIMMING_POOL -> "Hồ bơi"
        ServiceType.BBQ -> "BBQ"
        ServiceType.PARKING -> "Bãi xe"
    }}

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Quản lý Đặt chỗ") })
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                viewModel.loadBookings()
                isRefreshing = false
            },
            modifier = Modifier.padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Status filter
                item {
                    Text("Trạng thái", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(statusOptions) { (status, label) ->
                            FilterChip(
                                selected = uiState.statusFilter == status,
                                onClick = { viewModel.setStatusFilter(status) },
                                label = { Text(label) }
                            )
                        }
                    }
                }
                // Service type filter
                item {
                    Text("Loại dịch vụ", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(serviceOptions) { (type, label) ->
                            FilterChip(
                                selected = uiState.serviceTypeFilter == type,
                                onClick = { viewModel.setServiceTypeFilter(type) },
                                label = { Text(label) }
                            )
                        }
                    }
                }

                if (uiState.isLoading) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                } else {
                    items(uiState.filteredBookings, key = { it.id }) { booking ->
                        ManagerBookingCard(
                            booking = booking,
                            onClick = { viewModel.selectBooking(booking) }
                        )
                        if (booking.status == BookingStatus.PENDING) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp, bottom = 8.dp),
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
                }
            }
        }
    }

    // Bottom sheet for booking detail
    uiState.selectedBooking?.let { booking ->
        BookingDetailSheet(
            booking = booking,
            onDismiss = { viewModel.selectBooking(null) },
            onApprove = { viewModel.approveBooking(booking.id) },
            onReject = { viewModel.rejectBooking(booking.id) },
            onCancel = { viewModel.cancelBooking(booking.id) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookingDetailSheet(
    booking: ManagerBooking,
    onDismiss: () -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onCancel: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Chi tiết đặt chỗ", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            HorizontalDivider()
            DetailRow("Cư dân", booking.residentName)
            DetailRow("Phòng", booking.apartmentUnit)
            DetailRow("Dịch vụ", when (booking.serviceType) {
                com.exe202.nova.data.model.ServiceType.SWIMMING_POOL -> "Hồ bơi"
                com.exe202.nova.data.model.ServiceType.BBQ -> "BBQ"
                com.exe202.nova.data.model.ServiceType.PARKING -> "Bãi xe"
            })
            booking.slotNumber?.let { DetailRow("Vị trí", it) }
            DetailRow("Ngày", booking.date)
            DetailRow("Giờ", "${booking.startTime} - ${booking.endTime}")
            booking.numberOfParticipants?.let { DetailRow("Số người", it.toString()) }
            booking.notes?.let { DetailRow("Ghi chú", it) }
            DetailRow("Ngày tạo", booking.createdAt)
            Row(modifier = Modifier.fillMaxWidth()) {
                BookingStatusChip(status = booking.status)
            }
            if (booking.status == BookingStatus.PENDING) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { onReject(); onDismiss() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusOverdue)
                    ) { Text("Từ chối") }
                    Button(
                        onClick = { onApprove(); onDismiss() },
                        modifier = Modifier.weight(1f)
                    ) { Text("Duyệt") }
                }
            }
            if (booking.status == BookingStatus.CONFIRMED) {
                OutlinedButton(
                    onClick = { onCancel(); onDismiss() },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Hủy đặt chỗ") }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
        Text(value, fontWeight = FontWeight.Medium, fontSize = 14.sp)
    }
}
