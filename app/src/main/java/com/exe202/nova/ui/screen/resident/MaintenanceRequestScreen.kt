package com.exe202.nova.ui.screen.resident

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exe202.nova.data.model.MaintenanceRequest
import com.exe202.nova.data.model.MaintenanceStatus
import com.exe202.nova.ui.viewmodel.MaintenanceRequestViewModel
import com.exe202.nova.util.toDisplayDate

@Composable
fun MaintenanceRequestScreen(
    viewModel: MaintenanceRequestViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = viewModel::showForm) {
                Icon(Icons.Outlined.Add, contentDescription = "Tạo yêu cầu")
            }
        }
    ) { innerPadding ->
        if (uiState.requests.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("Chưa có yêu cầu nào", style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.requests, key = { it.id }) { request ->
                    MaintenanceCard(request)
                }
            }
        }
    }

    if (uiState.showForm) {
        AlertDialog(
            onDismissRequest = viewModel::hideForm,
            title = { Text("Yêu cầu sửa chữa mới") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = uiState.formTitle, onValueChange = viewModel::onTitleChange,
                        label = { Text("Tiêu đề *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    OutlinedTextField(value = uiState.formLocation, onValueChange = viewModel::onLocationChange,
                        label = { Text("Vị trí *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    OutlinedTextField(value = uiState.formDescription, onValueChange = viewModel::onDescriptionChange,
                        label = { Text("Mô tả *") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                    uiState.formError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            confirmButton = { Button(onClick = viewModel::submit) { Text("Gửi") } },
            dismissButton = { TextButton(onClick = viewModel::hideForm) { Text("Hủy") } }
        )
    }
}

@Composable
private fun MaintenanceCard(request: MaintenanceRequest) {
    val (statusLabel, statusColor) = when (request.status) {
        MaintenanceStatus.PENDING -> "Chờ xử lý" to MaterialTheme.colorScheme.error
        MaintenanceStatus.IN_PROGRESS -> "Đang xử lý" to MaterialTheme.colorScheme.primary
        MaintenanceStatus.DONE -> "Hoàn thành" to MaterialTheme.colorScheme.tertiary
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(request.title, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                Text(statusLabel, style = MaterialTheme.typography.labelSmall, color = statusColor)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(request.description, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
            Text("📍 ${request.location}", style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(request.createdAt.toDisplayDate(), style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
