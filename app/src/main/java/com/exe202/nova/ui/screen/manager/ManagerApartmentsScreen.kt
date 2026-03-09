package com.exe202.nova.ui.screen.manager

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exe202.nova.data.model.ApartmentStatus
import com.exe202.nova.ui.component.ApartmentCard
import com.exe202.nova.ui.viewmodel.ManagerApartmentsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagerApartmentsScreen(
    viewModel: ManagerApartmentsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val statusOptions = listOf(null to "Tất cả") + ApartmentStatus.entries.map { it to when (it) {
        ApartmentStatus.OCCUPIED -> "Có người ở"
        ApartmentStatus.VACANT -> "Trống"
        ApartmentStatus.MAINTENANCE -> "Bảo trì"
    }}

    Scaffold(
        topBar = { TopAppBar(title = { Text("Quản lý Căn hộ") }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Tìm theo số phòng, tên cư dân...") },
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
                items(uiState.filteredApartments, key = { it.id }) { apartment ->
                    ApartmentCard(apartment = apartment)
                }
            }
        }
    }
}
