package com.exe202.nova.ui.screen.resident

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exe202.nova.data.model.BookingStatus
import com.exe202.nova.data.model.SlotStatus
import com.exe202.nova.data.model.VehicleType
import com.exe202.nova.ui.component.LoadingScreen
import com.exe202.nova.ui.viewmodel.BookingViewModel
import com.exe202.nova.util.toVndFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    onNavigateToMyBookings: () -> Unit,
    viewModel: BookingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.navigateToHistory) {
        if (uiState.navigateToHistory) {
            viewModel.consumeNavigateToHistory()
            onNavigateToMyBookings()
        }
    }

    if (uiState.isLoading) {
        LoadingScreen()
        return
    }

    val tabs = listOf("BBQ", "Bãi xe", "Bể bơi")

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = uiState.selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = uiState.selectedTab == index,
                    onClick = { viewModel.selectTab(index) },
                    text = { Text(title) }
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            OutlinedButton(onClick = viewModel::loadBookedSlotsForSelectedTab) {
                Text("Các slot đã được đặt")
            }
        }

        when (uiState.selectedTab) {
            0 -> BbqTab(viewModel = viewModel)
            1 -> ParkingTab(viewModel = viewModel)
            2 -> PoolTab()
        }
    }

    if (uiState.bookedSlots.showDialog) {
        AlertDialog(
            onDismissRequest = viewModel::dismissBookedSlotsDialog,
            title = { Text("Các slot đã được đặt") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "${uiState.bookedSlots.serviceTypeLabel} - ${uiState.bookedSlots.selectedDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (uiState.bookedSlots.isLoading) {
                        CircularProgressIndicator()
                    } else if (uiState.bookedSlots.error != null) {
                        Text(
                            uiState.bookedSlots.error ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    } else if (uiState.bookedSlots.bookings.isEmpty()) {
                        Text("Chưa có slot nào được đặt")
                    } else {
                        LazyColumn(modifier = Modifier.heightIn(max = 280.dp)) {
                            items(uiState.bookedSlots.bookings, key = { it.id }) { booking ->
                                Column(modifier = Modifier.padding(vertical = 6.dp)) {
                                    Text(
                                        "Slot: ${booking.slotNumber ?: "-"}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        "Giờ: ${booking.startTime} - ${booking.endTime}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        "Trạng thái: ${bookingStatusLabel(booking.status)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = viewModel::dismissBookedSlotsDialog) {
                    Text("Đóng")
                }
            }
        )
    }
}

private fun bookingStatusLabel(status: BookingStatus): String = when (status) {
    BookingStatus.PENDING -> "Chờ duyệt"
    BookingStatus.CONFIRMED -> "Đã xác nhận"
    BookingStatus.REJECTED -> "Bị từ chối"
    BookingStatus.CANCELLED -> "Đã hủy"
}

@Composable
private fun PoolTab() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Bể bơi đang bảo trì",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun BbqTab(viewModel: BookingViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val bbq = uiState.bbq

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(bbq.slots, key = { it.id }) { slot ->
            val isExpanded = bbq.expandedSlotId == slot.id
            val isDisabled = slot.status == SlotStatus.MAINTENANCE
            val isBooked = slot.id in bbq.bookedSlotIds
            val isUnavailable = isDisabled || isBooked

            OutlinedCard(
                onClick = { if (!isUnavailable) viewModel.expandBbqSlot(if (isExpanded) null else slot.id) },
                modifier = Modifier.fillMaxWidth(),
                border = if (isExpanded) BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                         else CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(slot.name, style = MaterialTheme.typography.titleSmall)
                            Text("Sức chứa: ${slot.capacity} người | ${slot.pricePerHour.toVndFormat()}/giờ", style = MaterialTheme.typography.bodySmall)
                        }
                        when {
                            isDisabled -> Text("Bảo trì", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                            isBooked -> Text("Đã được đặt", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    if (isExpanded) {
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = bbq.startTime,
                            onValueChange = viewModel::updateBbqStartTime,
                            label = { Text("Giờ bắt đầu (HH:mm)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = bbq.endTime,
                            onValueChange = viewModel::updateBbqEndTime,
                            label = { Text("Giờ kết thúc (HH:mm)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = bbq.participants,
                            onValueChange = viewModel::updateBbqParticipants,
                            label = { Text("Số người tham gia") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = bbq.notes,
                            onValueChange = viewModel::updateBbqNotes,
                            label = { Text("Ghi chú (tùy chọn)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        bbq.error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
                        if (bbq.submitSuccess) Text("Đặt chỗ thành công!", color = MaterialTheme.colorScheme.primary)

                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = viewModel::submitBbqBooking,
                            enabled = bbq.startTime.isNotBlank() && bbq.endTime.isNotBlank() && bbq.participants.isNotBlank() && !bbq.isSubmitting,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (bbq.isSubmitting) "Đang xử lý..." else "Đặt chỗ")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ParkingTab(viewModel: BookingViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val parking = uiState.parking
    val filtered = viewModel.filteredParkingSlots()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Daily/Monthly toggle
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = !parking.isMonthly, onClick = { viewModel.toggleParkingMode(false) }, label = { Text("Theo ngày") })
            FilterChip(selected = parking.isMonthly, onClick = { viewModel.toggleParkingMode(true) }, label = { Text("Theo tháng") })
        }

        // Vehicle filter
        Text("Loại xe:", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = parking.vehicleFilter == null, onClick = { viewModel.setParkingVehicleFilter(null) }, label = { Text("Tất cả") })
            FilterChip(selected = parking.vehicleFilter == VehicleType.CAR, onClick = { viewModel.setParkingVehicleFilter(VehicleType.CAR) }, label = { Text("Ô tô") })
            FilterChip(selected = parking.vehicleFilter == VehicleType.MOTORBIKE, onClick = { viewModel.setParkingVehicleFilter(VehicleType.MOTORBIKE) }, label = { Text("Xe máy") })
            FilterChip(selected = parking.vehicleFilter == VehicleType.BICYCLE, onClick = { viewModel.setParkingVehicleFilter(VehicleType.BICYCLE) }, label = { Text("Xe đạp") })
        }

        // Slot grid
        Text("Chọn ô đỗ xe:", style = MaterialTheme.typography.titleSmall)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            filtered.forEach { slot ->
                val isSelected = slot.id in parking.selectedSlotIds
                val isOccupied = slot.status == SlotStatus.OCCUPIED
                val isBooked = slot.id in parking.bookedSlotIds
                FilterChip(
                    selected = isSelected,
                    onClick = { if (!isOccupied && !isBooked) viewModel.toggleParkingSlot(slot.id) },
                    label = { Text("${slot.label}\n${slot.floor}") },
                    enabled = !isOccupied && !isBooked
                )
            }
        }

        if (parking.selectedSlotIds.isNotEmpty()) {
            val pricePerUnit = filtered.filter { it.id in parking.selectedSlotIds }
                .sumOf { if (parking.isMonthly) it.pricePerMonth else it.pricePerDay }
            Text("Đã chọn ${parking.selectedSlotIds.size} ô | Tổng: ${pricePerUnit.toVndFormat()}", style = MaterialTheme.typography.bodyMedium)
        }

        OutlinedTextField(
            value = parking.notes,
            onValueChange = viewModel::updateParkingNotes,
            label = { Text("Ghi chú (tùy chọn)") },
            modifier = Modifier.fillMaxWidth()
        )

        parking.error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
        if (parking.submitSuccess) Text("Đặt chỗ thành công!", color = MaterialTheme.colorScheme.primary)

        Button(
            onClick = viewModel::submitParkingBooking,
            enabled = parking.selectedSlotIds.isNotEmpty() && !parking.isSubmitting,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (parking.isSubmitting) "Đang xử lý..." else "Đặt chỗ")
        }
    }
}
