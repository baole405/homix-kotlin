package com.exe202.nova.ui.screen.manager

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exe202.nova.data.model.BBQSlot
import com.exe202.nova.data.model.ParkingSlot
import com.exe202.nova.data.model.PoolSlot
import com.exe202.nova.data.model.SlotStatus
import com.exe202.nova.data.model.VehicleType
import com.exe202.nova.ui.viewmodel.ManagerFacilitiesViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagerFacilitiesScreen(
    viewModel: ManagerFacilitiesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val tabs = listOf("Bãi xe", "BBQ", "Hồ bơi")

    Scaffold(
        topBar = { TopAppBar(title = { Text("Quản lý Tiện ích") }) }
    ) { padding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {
            TabRow(selectedTabIndex = uiState.selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = uiState.selectedTab == index,
                        onClick = { viewModel.selectTab(index) },
                        text = { Text(title) }
                    )
                }
            }
            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                when (uiState.selectedTab) {
                    0 -> ParkingTab(slots = uiState.parkingSlots)
                    1 -> BbqTab(slots = uiState.bbqSlots)
                    2 -> uiState.pool?.let { PoolTab(pool = it) }
                }
            }
        }
    }
}

@Composable
private fun ParkingTab(slots: List<ParkingSlot>) {
    val format = NumberFormat.getNumberInstance(Locale("vi", "VN"))
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(slots, key = { it.id }) { slot ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(slot.label, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        Text("Tầng ${slot.floor} • ${if (slot.type == VehicleType.CAR) "Ô tô" else "Xe máy"}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${format.format(slot.pricePerMonth.toLong())}đ/tháng", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                    }
                    SlotStatusBadge(slot.status)
                }
            }
        }
    }
}

@Composable
private fun BbqTab(slots: List<BBQSlot>) {
    val format = NumberFormat.getNumberInstance(Locale("vi", "VN"))
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(slots, key = { it.id }) { slot ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(slot.name, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        Text("Sức chứa: ${slot.capacity} người", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${format.format(slot.pricePerHour.toLong())}đ/giờ", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                    }
                    SlotStatusBadge(slot.status)
                }
            }
        }
    }
}

@Composable
private fun PoolTab(pool: PoolSlot) {
    val format = NumberFormat.getNumberInstance(Locale("vi", "VN"))
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(pool.name, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                HorizontalDivider()
                PoolInfoRow("Vị trí", pool.location)
                PoolInfoRow("Sức chứa", "${pool.capacity} người")
                PoolInfoRow("Giá", "${format.format(pool.pricePerHour.toLong())}đ/giờ")
                PoolInfoRow("Giờ mở cửa", "${pool.openTime} - ${pool.closeTime}")
                PoolInfoRow("Thời gian tối đa", "${pool.maxDurationHours} giờ")
                Row { SlotStatusBadge(pool.status) }
            }
        }
    }
}

@Composable
private fun PoolInfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
        Text(value, fontWeight = FontWeight.Medium, fontSize = 14.sp)
    }
}

@Composable
private fun SlotStatusBadge(status: SlotStatus) {
    val (text, color) = when (status) {
        SlotStatus.AVAILABLE -> "Trống" to MaterialTheme.colorScheme.primary
        SlotStatus.OCCUPIED -> "Đang dùng" to MaterialTheme.colorScheme.error
        SlotStatus.MAINTENANCE -> "Bảo trì" to MaterialTheme.colorScheme.tertiary
    }
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}
