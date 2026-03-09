package com.exe202.nova.ui.screen.manager

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
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
import com.exe202.nova.data.model.BillStatus
import com.exe202.nova.ui.component.BillStatusChip
import com.exe202.nova.ui.component.ManagerBillRow
import com.exe202.nova.ui.viewmodel.ManagerBillingViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagerBillingScreen(
    viewModel: ManagerBillingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var isRefreshing by remember { mutableStateOf(false) }

    val statusOptions = listOf(null to "Tất cả") + BillStatus.entries.map { it to when (it) {
        BillStatus.PENDING -> "Chờ TT"
        BillStatus.PAID -> "Đã TT"
        BillStatus.OVERDUE -> "Quá hạn"
        BillStatus.CANCELLED -> "Đã hủy"
    }}

    Scaffold(
        topBar = { TopAppBar(title = { Text("Quản lý Hóa đơn") }) }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                viewModel.loadBills()
                isRefreshing = false
            },
            modifier = Modifier.padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Tìm theo tên, phòng, tiêu đề...") },
                        leadingIcon = { Icon(Icons.Outlined.Search, null) },
                        singleLine = true
                    )
                }
                item {
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
                if (uiState.isLoading) {
                    item {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                } else {
                    items(uiState.filteredBills, key = { it.id }) { bill ->
                        ManagerBillRow(bill = bill, onClick = { viewModel.selectBill(bill) })
                    }
                }
            }
        }
    }

    uiState.selectedBill?.let { bill ->
        val sheetState = rememberModalBottomSheetState()
        ModalBottomSheet(
            onDismissRequest = { viewModel.selectBill(null) },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Chi tiết hóa đơn", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                HorizontalDivider()
                BillDetailRow("Cư dân", bill.residentName)
                BillDetailRow("Phòng", "${bill.apartmentUnit} - Block ${bill.apartmentBlock}")
                BillDetailRow("Tiêu đề", bill.title)
                BillDetailRow("Kỳ", bill.period)
                BillDetailRow("Hạn nộp", bill.dueDate)
                val format = NumberFormat.getNumberInstance(Locale("vi", "VN"))
                BillDetailRow("Số tiền", "${format.format(bill.amount.toLong())}đ")
                Row { BillStatusChip(status = bill.status) }
                if (bill.status != BillStatus.PAID) {
                    Button(
                        onClick = { viewModel.markBillPaid(bill.id) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Đánh dấu đã thanh toán")
                    }
                }
            }
        }
    }
}

@Composable
private fun BillDetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
        Text(value, fontWeight = FontWeight.Medium, fontSize = 14.sp)
    }
}
