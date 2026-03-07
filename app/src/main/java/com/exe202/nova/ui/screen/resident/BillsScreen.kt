package com.exe202.nova.ui.screen.resident

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exe202.nova.ui.component.BillStatusChip
import com.exe202.nova.ui.component.EmptyState
import com.exe202.nova.ui.component.ErrorScreen
import com.exe202.nova.ui.component.LoadingScreen
import com.exe202.nova.ui.viewmodel.BillFilter
import com.exe202.nova.ui.viewmodel.BillsViewModel
import com.exe202.nova.util.toDisplayDate
import com.exe202.nova.util.toVndFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillsScreen(
    onNavigateToBillDetail: (Int) -> Unit,
    viewModel: BillsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val filteredBills = viewModel.filteredBills()

    val tabs = listOf("Tat ca", "Cho thanh toan", "Da thanh toan", "Qua han")
    val filters = BillFilter.entries.toList()

    if (uiState.isLoading) {
        LoadingScreen()
        return
    }

    uiState.error?.let { error ->
        if (uiState.allBills.isEmpty()) {
            ErrorScreen(message = error, onRetry = viewModel::loadBills)
            return
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = filters.indexOf(uiState.selectedTab)) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = uiState.selectedTab == filters[index],
                    onClick = { viewModel.selectTab(filters[index]) },
                    text = { Text(title, style = MaterialTheme.typography.labelLarge) }
                )
            }
        }

        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = viewModel::refresh,
            modifier = Modifier.fillMaxSize()
        ) {
            if (filteredBills.isEmpty()) {
                EmptyState("Khong co hoa don")
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredBills, key = { it.id }) { bill ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToBillDetail(bill.id) },
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        bill.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.weight(1f)
                                    )
                                    BillStatusChip(bill.status)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    bill.amount.toVndFormat(),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    "Ky: ${bill.period}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    "Han: ${bill.dueDate.toDisplayDate()}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
