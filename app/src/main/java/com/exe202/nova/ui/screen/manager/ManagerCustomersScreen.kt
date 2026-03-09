package com.exe202.nova.ui.screen.manager

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.exe202.nova.ui.component.CustomerCard
import com.exe202.nova.ui.viewmodel.ManagerCustomersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagerCustomersScreen(
    onNavigateToDetail: (String) -> Unit,
    viewModel: ManagerCustomersViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Quản lý Cư dân") }) }
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
                    placeholder = { Text("Tìm theo tên, email, phòng...") },
                    leadingIcon = { Icon(Icons.Outlined.Search, null) },
                    singleLine = true
                )
            }
            if (uiState.isLoading) {
                item {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            } else {
                items(uiState.filteredCustomers, key = { it.id }) { customer ->
                    CustomerCard(
                        customer = customer,
                        onClick = { onNavigateToDetail(customer.id) }
                    )
                }
            }
        }
    }
}
