package com.exe202.nova.ui.screen.manager

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagerMoreScreen(
    onNavigateToApartments: () -> Unit,
    onNavigateToFacilities: () -> Unit,
    onNavigateToAnnouncements: () -> Unit,
    onNavigateToReports: () -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Thêm") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            MoreMenuItem(
                icon = Icons.Outlined.Apartment,
                label = "Quản lý Căn hộ",
                onClick = onNavigateToApartments
            )
            HorizontalDivider()
            MoreMenuItem(
                icon = Icons.Outlined.Pool,
                label = "Quản lý Tiện ích",
                onClick = onNavigateToFacilities
            )
            HorizontalDivider()
            MoreMenuItem(
                icon = Icons.Outlined.Campaign,
                label = "Thông báo",
                onClick = onNavigateToAnnouncements
            )
            HorizontalDivider()
            MoreMenuItem(
                icon = Icons.Outlined.BarChart,
                label = "Báo cáo",
                onClick = onNavigateToReports
            )
        }
    }
}

@Composable
private fun MoreMenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 16.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(16.dp))
        Text(label, fontSize = 15.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
        Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
