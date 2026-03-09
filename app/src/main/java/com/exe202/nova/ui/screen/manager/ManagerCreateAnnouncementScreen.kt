package com.exe202.nova.ui.screen.manager

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exe202.nova.data.model.AnnouncementCategory
import com.exe202.nova.data.model.AnnouncementPriority
import com.exe202.nova.ui.viewmodel.ManagerAnnouncementsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagerCreateAnnouncementScreen(
    onNavigateBack: () -> Unit,
    viewModel: ManagerAnnouncementsViewModel = hiltViewModel()
) {
    val createState by viewModel.createState.collectAsStateWithLifecycle()

    LaunchedEffect(createState.submitted) {
        if (createState.submitted) {
            viewModel.resetCreateForm()
            onNavigateBack()
        }
    }

    var categoryExpanded by remember { mutableStateOf(false) }
    var priorityExpanded by remember { mutableStateOf(false) }

    val categoryLabels = mapOf(
        AnnouncementCategory.MAINTENANCE to "Bảo trì",
        AnnouncementCategory.EVENT to "Sự kiện",
        AnnouncementCategory.POLICY to "Quy định",
        AnnouncementCategory.EMERGENCY to "Khẩn cấp",
        AnnouncementCategory.GENERAL to "Chung"
    )
    val priorityLabels = mapOf(
        AnnouncementPriority.NORMAL to "Bình thường",
        AnnouncementPriority.IMPORTANT to "Quan trọng",
        AnnouncementPriority.URGENT to "Khẩn cấp"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tạo thông báo") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = createState.title,
                onValueChange = { viewModel.updateTitle(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Tiêu đề") },
                singleLine = true
            )

            // Category dropdown
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = it }
            ) {
                OutlinedTextField(
                    value = categoryLabels[createState.category] ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Danh mục") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    categoryLabels.forEach { (cat, label) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                viewModel.updateCategory(cat)
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            // Priority dropdown
            ExposedDropdownMenuBox(
                expanded = priorityExpanded,
                onExpandedChange = { priorityExpanded = it }
            ) {
                OutlinedTextField(
                    value = priorityLabels[createState.priority] ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Mức độ ưu tiên") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = priorityExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )
                ExposedDropdownMenu(
                    expanded = priorityExpanded,
                    onDismissRequest = { priorityExpanded = false }
                ) {
                    priorityLabels.forEach { (pri, label) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                viewModel.updatePriority(pri)
                                priorityExpanded = false
                            }
                        )
                    }
                }
            }

            // Pin toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Ghim thông báo", fontSize = 15.sp, fontWeight = FontWeight.Medium)
                Switch(
                    checked = createState.pinned,
                    onCheckedChange = { viewModel.updatePinned(it) }
                )
            }

            OutlinedTextField(
                value = createState.content,
                onValueChange = { viewModel.updateContent(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                label = { Text("Nội dung") },
                maxLines = 10
            )

            createState.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
            }

            Button(
                onClick = { viewModel.submitAnnouncement() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !createState.isSubmitting
            ) {
                if (createState.isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Text("Đăng thông báo")
                }
            }
        }
    }
}
