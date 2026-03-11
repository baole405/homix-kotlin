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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exe202.nova.data.model.SlotStatus
import com.exe202.nova.data.model.VehicleType
import com.exe202.nova.ui.component.LoadingScreen
import com.exe202.nova.ui.viewmodel.BookingViewModel
import com.exe202.nova.ui.viewmodel.TimeSlot
import com.exe202.nova.util.toVndFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    onNavigateToMyBookings: () -> Unit,
    viewModel: BookingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isLoading) {
        LoadingScreen()
        return
    }

    val tabs = listOf("Bể bơi", "BBQ", "Bãi xe")

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

        when (uiState.selectedTab) {
            0 -> PoolTab(viewModel = viewModel)
            1 -> BbqTab(viewModel = viewModel)
            2 -> ParkingTab(viewModel = viewModel)
        }

        TextButton(
            onClick = onNavigateToMyBookings,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            Text("Xem lịch sử đặt chỗ của tôi")
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PoolTab(viewModel: BookingViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pool = uiState.pool

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        pool.pool?.let { p ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(p.name, style = MaterialTheme.typography.titleMedium)
                    Text(p.location, style = MaterialTheme.typography.bodySmall)
                    Text("Sức chứa: ${p.capacity} người | Giờ mở cửa: ${p.openTime} - ${p.closeTime}", style = MaterialTheme.typography.bodySmall)
                    Text("Giá: ${p.pricePerHour.toVndFormat()}/giờ", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        Text("Chọn khung giờ:", style = MaterialTheme.typography.titleSmall)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            pool.timeSlots.forEach { slot ->
                FilterChip(
                    selected = pool.selectedSlot == slot,
                    onClick = { if (!slot.isBooked) viewModel.selectTimeSlot(slot) },
                    label = { Text("${slot.startTime}-${slot.endTime}") },
                    enabled = !slot.isBooked
                )
            }
        }

        OutlinedTextField(
            value = pool.participants,
            onValueChange = viewModel::updatePoolParticipants,
            label = { Text("Số người tham gia") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = pool.notes,
            onValueChange = viewModel::updatePoolNotes,
            label = { Text("Ghi chú (tùy chọn)") },
            modifier = Modifier.fillMaxWidth()
        )

        pool.selectedSlot?.let { slot ->
            val hours = 2
            val price = (pool.pool?.pricePerHour ?: 0.0) * hours
            Text("Tóm tắt: ${slot.startTime} - ${slot.endTime} | Tổng tiền: ${price.toVndFormat()}", style = MaterialTheme.typography.bodyMedium)
        }

        pool.error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
        if (pool.submitSuccess) Text("Đặt chỗ thành công!", color = MaterialTheme.colorScheme.primary)

        Button(
            onClick = viewModel::submitPoolBooking,
            enabled = pool.selectedSlot != null && pool.participants.isNotBlank() && !pool.isSubmitting,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (pool.isSubmitting) "Đang xử lý..." else "Đặt chỗ")
        }
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

            OutlinedCard(
                onClick = { if (!isDisabled) viewModel.expandBbqSlot(if (isExpanded) null else slot.id) },
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
                        if (isDisabled) Text("Bao tri", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
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
                FilterChip(
                    selected = isSelected,
                    onClick = { if (!isOccupied) viewModel.toggleParkingSlot(slot.id) },
                    label = { Text("${slot.label}\n${slot.floor}") },
                    enabled = !isOccupied
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
