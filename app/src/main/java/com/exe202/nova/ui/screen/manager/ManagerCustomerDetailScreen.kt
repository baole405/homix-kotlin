package com.exe202.nova.ui.screen.manager

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exe202.nova.data.mock.MOCK_CUSTOMERS
import com.exe202.nova.data.mock.MOCK_MANAGER_APARTMENTS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagerCustomerDetailScreen(
    customerId: String,
    onNavigateBack: () -> Unit
) {
    val customer = remember(customerId) { MOCK_CUSTOMERS.find { it.id == customerId } }
    val apartment = remember(customer) {
        customer?.apartmentUnit?.let { unit ->
            MOCK_MANAGER_APARTMENTS.find { it.unitNumber == unit }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(customer?.name ?: "Cư dân") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { padding ->
        if (customer == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Không tìm thấy cư dân")
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Avatar + name
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(64.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = customer.name.take(1).uppercase(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(customer.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(customer.role.name, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                }
            }

            // Profile info
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Thông tin liên hệ", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    HorizontalDivider()
                    InfoRow("Email", customer.email)
                    customer.phone?.let { InfoRow("Điện thoại", it) }
                }
            }

            // Apartment info
            apartment?.let { apt ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Thông tin căn hộ", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        HorizontalDivider()
                        InfoRow("Số phòng", apt.unitNumber)
                        InfoRow("Block", apt.block)
                        InfoRow("Tầng", apt.floor.toString())
                        InfoRow("Diện tích", "${apt.area}m²")
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
        Text(value, fontWeight = FontWeight.Medium, fontSize = 14.sp)
    }
}
