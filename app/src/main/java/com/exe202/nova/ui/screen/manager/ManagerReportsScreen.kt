package com.exe202.nova.ui.screen.manager

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exe202.nova.data.model.RevenueData
import com.exe202.nova.data.model.ServiceType
import com.exe202.nova.ui.viewmodel.ManagerReportsViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagerReportsScreen(
    viewModel: ManagerReportsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val tabs = listOf("Doanh thu", "Booking")

    Scaffold(
        topBar = { TopAppBar(title = { Text("Báo cáo") }) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
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
                    0 -> RevenueTab(data = uiState.revenueData)
                    1 -> BookingStatsTab(stats = uiState.bookingStats)
                }
            }
        }
    }
}

@Composable
private fun RevenueTab(data: List<RevenueData>) {
    val format = NumberFormat.getNumberInstance(Locale("vi", "VN"))
    val maxAmount = data.maxOfOrNull { it.amount } ?: 1.0

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Summary cards
        if (data.size >= 2) {
            item {
                val current = data.last()
                val previous = data[data.size - 2]
                val growthPct = if (previous.amount > 0) ((current.amount - previous.amount) / previous.amount * 100) else 0.0
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SummaryCard(
                        label = "Tháng này",
                        value = "${format.format(current.amount.toLong())}đ",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    SummaryCard(
                        label = "Tháng trước",
                        value = "${format.format(previous.amount.toLong())}đ",
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.height(4.dp))
                SummaryCard(
                    label = "Tăng trưởng",
                    value = String.format("%.1f%%", growthPct),
                    color = if (growthPct >= 0) Color(0xFF66BB6A) else Color(0xFFEF5350),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Bar chart
        item {
            Text("Biểu đồ doanh thu", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Spacer(Modifier.height(8.dp))
        }
        items(data) { item ->
            RevenueBar(
                month = item.month,
                amount = item.amount,
                maxAmount = maxAmount,
                format = format
            )
        }
    }
}

@Composable
private fun RevenueBar(month: String, amount: Double, maxAmount: Double, format: NumberFormat) {
    val fraction = (amount / maxAmount).toFloat().coerceIn(0.05f, 1f)
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(month, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("${format.format(amount.toLong())}đ", fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.small)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.primary, MaterialTheme.shapes.small)
            )
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun SummaryCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = color)
        }
    }
}

@Composable
private fun BookingStatsTab(stats: List<com.exe202.nova.data.model.BookingStatsData>) {
    val total = stats.sumOf { it.count }.toFloat().coerceAtLeast(1f)
    val colors = listOf(
        Color(0xFF1976D2),
        Color(0xFFFF8F00),
        Color(0xFF388E3C)
    )
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Thống kê đặt chỗ", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Spacer(Modifier.height(8.dp))
        }
        items(stats.mapIndexed { i, s -> Pair(i, s) }) { (index, stat) ->
            val label = when (stat.serviceType) {
                ServiceType.SWIMMING_POOL -> "Hồ bơi"
                ServiceType.BBQ -> "BBQ"
                ServiceType.PARKING -> "Bãi xe"
            }
            val fraction = (stat.count / total).coerceIn(0.05f, 1f)
            val color = colors.getOrElse(index) { MaterialTheme.colorScheme.primary }
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Text("${stat.count} lượt", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                        .background(color.copy(alpha = 0.1f), MaterialTheme.shapes.small)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction)
                            .fillMaxHeight()
                            .background(color, MaterialTheme.shapes.small)
                    )
                    Text(
                        "${(fraction * 100).toInt()}%",
                        fontSize = 11.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 8.dp)
                    )
                }
            }
        }
    }
}
