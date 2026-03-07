package com.exe202.nova.ui.screen.resident

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exe202.nova.data.model.BillStatus
import com.exe202.nova.ui.component.BillStatusChip
import com.exe202.nova.ui.component.ErrorScreen
import com.exe202.nova.ui.component.LoadingScreen
import com.exe202.nova.ui.viewmodel.BillDetailViewModel
import com.exe202.nova.util.toDisplayDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: BillDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showMarkPaidSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(uiState.paymentUrl) {
        uiState.paymentUrl?.let { url ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
            viewModel.clearPaymentUrl()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiet hoa don") },
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
            if (uiState.bill == null) {
                ErrorScreen(message = error, onRetry = viewModel::loadBillDetail, modifier = Modifier.padding(innerPadding))
                return@Scaffold
            }
        }

        val bill = uiState.bill ?: return@Scaffold

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                // Status + amount
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(bill.title, style = MaterialTheme.typography.headlineSmall)
                    BillStatusChip(bill.status)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    bill.amount,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            item {
                // Bill info
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    bill.apartment?.let { apt ->
                        Text("Can ho: ${apt.unitNumber} - Tang ${apt.floor} - ${apt.block}", style = MaterialTheme.typography.bodyMedium)
                    }
                    Text("Ky: ${bill.period}", style = MaterialTheme.typography.bodyMedium)
                    Text("Han thanh toan: ${bill.dueDate.toDisplayDate()}", style = MaterialTheme.typography.bodyMedium)
                    Text("Ngay tao: ${bill.createdAt.toDisplayDate()}", style = MaterialTheme.typography.bodyMedium)
                    bill.paidAt?.let {
                        Text("Ngay thanh toan: ${it.toDisplayDate()}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            item {
                HorizontalDivider()
                Spacer(modifier = Modifier.height(4.dp))
                Text("Chi tiet hoa don", style = MaterialTheme.typography.titleMedium)
            }

            items(bill.items) { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.title, style = MaterialTheme.typography.bodyMedium)
                        item.usage?.let { Text("Su dung: $it ${item.measureUnit ?: ""}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        item.unitPrice?.let { Text("Don gia: $it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                    }
                    Text(item.amount, style = MaterialTheme.typography.bodyMedium)
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            }

            // Payment buttons
            if (bill.status == BillStatus.PENDING || bill.status == BillStatus.OVERDUE) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = viewModel::createPaymentLink,
                        enabled = !uiState.isCreatingPaymentLink,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (uiState.isCreatingPaymentLink) "Dang xu ly..." else "Thanh toan online (VNPay)")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { showMarkPaidSheet = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Danh dau da tra")
                    }
                }
            }
        }

        if (showMarkPaidSheet) {
            MarkPaidBottomSheet(
                sheetState = sheetState,
                isLoading = uiState.isMarkingPaid,
                onDismiss = { showMarkPaidSheet = false },
                onConfirm = { method, ref, notes ->
                    viewModel.markAsPaid(method, ref, notes)
                    showMarkPaidSheet = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MarkPaidBottomSheet(
    sheetState: androidx.compose.material3.SheetState,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String, String?, String?) -> Unit
) {
    val methods = listOf("bank_transfer" to "Chuyen khoan", "cash" to "Tien mat", "e_wallet" to "Vi dien tu", "credit_card" to "The tin dung")
    var selectedMethod by remember { mutableStateOf(methods[0].first) }
    var transactionRef by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Chon phuong thuc thanh toan", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))

            methods.forEach { (value, label) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RadioButton(selected = selectedMethod == value, onClick = { selectedMethod = value })
                    Text(label, modifier = Modifier.padding(start = 8.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = transactionRef,
                onValueChange = { transactionRef = it },
                label = { Text("Ma giao dich (tuy chon)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Ghi chu (tuy chon)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onConfirm(selectedMethod, transactionRef.ifBlank { null }, notes.ifBlank { null }) },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Xac nhan")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
