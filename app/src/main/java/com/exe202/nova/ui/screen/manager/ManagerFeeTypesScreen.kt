package com.exe202.nova.ui.screen.manager

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exe202.nova.data.model.FeeType
import com.exe202.nova.ui.component.EmptyState
import com.exe202.nova.ui.component.ErrorScreen
import com.exe202.nova.ui.component.LoadingScreen
import com.exe202.nova.ui.viewmodel.ManagerFeeTypesViewModel
import java.text.NumberFormat
import java.util.Locale

private val feeFormatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagerFeeTypesScreen(
    viewModel: ManagerFeeTypesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isLoading) {
        LoadingScreen()
        return
    }
    uiState.error?.let { err ->
        ErrorScreen(message = err, onRetry = viewModel::loadFeeTypes)
        return
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = viewModel::showCreateDialog) {
                Icon(Icons.Outlined.Add, contentDescription = "Thêm loại phí")
            }
        }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = viewModel::refresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.feeTypes.isEmpty()) {
                EmptyState(message = "Chưa có loại phí nào")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.feeTypes, key = { it.id }) { feeType ->
                        FeeTypeCard(
                            feeType = feeType,
                            onEdit = { viewModel.showEditDialog(feeType) },
                            onDelete = { viewModel.deleteFeeType(feeType.id) }
                        )
                    }
                }
            }
        }
    }

    if (uiState.showCreateDialog) {
        FeeTypeDialog(
            isEditing = uiState.editingFeeType != null,
            name = uiState.formName,
            description = uiState.formDescription,
            unitPrice = uiState.formUnitPrice,
            measureUnit = uiState.formMeasureUnit,
            isRecurring = uiState.formIsRecurring,
            error = uiState.formError,
            isSaving = uiState.isSaving,
            onNameChange = viewModel::onFormNameChange,
            onDescriptionChange = viewModel::onFormDescriptionChange,
            onUnitPriceChange = viewModel::onFormUnitPriceChange,
            onMeasureUnitChange = viewModel::onFormMeasureUnitChange,
            onIsRecurringChange = viewModel::onFormIsRecurringChange,
            onSave = viewModel::saveFeeType,
            onDismiss = viewModel::dismissDialog
        )
    }
}

@Composable
private fun FeeTypeCard(
    feeType: FeeType,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(feeType.name, style = MaterialTheme.typography.titleSmall)
                feeType.description?.let {
                    Text(
                        it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    "${feeFormatter.format(feeType.unitPrice)}đ${feeType.measureUnit?.let { "/$it" } ?: ""}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                if (feeType.isRecurring) {
                    Text(
                        "Định kỳ",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Outlined.Edit, contentDescription = "Sửa")
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Outlined.Delete,
                        contentDescription = "Xóa",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun FeeTypeDialog(
    isEditing: Boolean,
    name: String,
    description: String,
    unitPrice: String,
    measureUnit: String,
    isRecurring: Boolean,
    error: String?,
    isSaving: Boolean,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onUnitPriceChange: (String) -> Unit,
    onMeasureUnitChange: (String) -> Unit,
    onIsRecurringChange: (Boolean) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEditing) "Sửa loại phí" else "Thêm loại phí") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text("Tên *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    label = { Text("Mô tả") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = unitPrice,
                    onValueChange = onUnitPriceChange,
                    label = { Text("Đơn giá *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = measureUnit,
                    onValueChange = onMeasureUnitChange,
                    label = { Text("Đơn vị") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isRecurring, onCheckedChange = onIsRecurringChange)
                    Text("Định kỳ hàng tháng")
                }
                error?.let {
                    Text(
                        it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = onSave, enabled = !isSaving) {
                if (isSaving) CircularProgressIndicator(modifier = Modifier.size(16.dp))
                else Text("Lưu")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Hủy") }
        }
    )
}
