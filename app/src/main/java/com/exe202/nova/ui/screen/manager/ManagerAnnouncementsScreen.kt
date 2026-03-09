package com.exe202.nova.ui.screen.manager

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exe202.nova.data.model.Announcement
import com.exe202.nova.data.model.AnnouncementCategory
import com.exe202.nova.data.model.AnnouncementPriority
import com.exe202.nova.ui.viewmodel.ManagerAnnouncementsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagerAnnouncementsScreen(
    onNavigateToCreate: () -> Unit,
    viewModel: ManagerAnnouncementsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var deleteTarget by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Thông báo BQL") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreate) {
                Icon(Icons.Outlined.Add, contentDescription = "Tạo thông báo")
            }
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.announcements, key = { it.id }) { announcement ->
                    AnnouncementCard(
                        announcement = announcement,
                        onDelete = { deleteTarget = announcement.id }
                    )
                }
            }
        }
    }

    deleteTarget?.let { id ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("Xác nhận xóa") },
            text = { Text("Bạn có chắc muốn xóa thông báo này?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteAnnouncement(id)
                    deleteTarget = null
                }) { Text("Xóa", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) { Text("Hủy") }
            }
        )
    }
}

@Composable
private fun AnnouncementCard(
    announcement: Announcement,
    onDelete: () -> Unit
) {
    val categoryLabel = when (announcement.category) {
        AnnouncementCategory.MAINTENANCE -> "Bảo trì"
        AnnouncementCategory.EVENT -> "Sự kiện"
        AnnouncementCategory.POLICY -> "Quy định"
        AnnouncementCategory.EMERGENCY -> "Khẩn cấp"
        AnnouncementCategory.GENERAL -> "Chung"
    }
    val priorityColor = when (announcement.priority) {
        AnnouncementPriority.URGENT -> MaterialTheme.colorScheme.error
        AnnouncementPriority.IMPORTANT -> MaterialTheme.colorScheme.tertiary
        AnnouncementPriority.NORMAL -> MaterialTheme.colorScheme.primary
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = priorityColor.copy(alpha = 0.15f),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            categoryLabel,
                            color = priorityColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    if (announcement.pinned) {
                        Text("📌", fontSize = 14.sp)
                    }
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Outlined.Delete, contentDescription = "Xóa", tint = MaterialTheme.colorScheme.error)
                }
            }
            Text(announcement.title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Text(
                announcement.content.take(100) + if (announcement.content.length > 100) "..." else "",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(announcement.createdAt, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
