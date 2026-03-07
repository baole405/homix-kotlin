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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.LocalParking
import androidx.compose.material.icons.outlined.Pool
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exe202.nova.ui.component.BillStatusChip
import com.exe202.nova.ui.component.ErrorScreen
import com.exe202.nova.ui.component.LoadingScreen
import com.exe202.nova.ui.viewmodel.DashboardViewModel
import com.exe202.nova.util.toDisplayDate
import com.exe202.nova.util.toVndFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToBills: () -> Unit,
    onNavigateToBooking: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isLoading) {
        LoadingScreen()
        return
    }

    uiState.error?.let { error ->
        if (uiState.user == null) {
            ErrorScreen(message = error, onRetry = viewModel::loadDashboard)
            return
        }
    }

    PullToRefreshBox(
        isRefreshing = uiState.isRefreshing,
        onRefresh = viewModel::refresh
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "Xin chao, ${uiState.user?.fullName ?: ""}",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Hoa don sap den han", style = MaterialTheme.typography.titleMedium)
                TextButton(onClick = onNavigateToBills) { Text("Xem tat ca") }
            }

            if (uiState.upcomingBills.isEmpty()) {
                Text("Khong co hoa don sap den han", style = MaterialTheme.typography.bodyMedium)
            } else {
                uiState.upcomingBills.forEach { bill ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { },
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(bill.title, style = MaterialTheme.typography.titleSmall)
                                BillStatusChip(bill.status)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(bill.amount.toVndFormat(), style = MaterialTheme.typography.bodyLarge)
                            Text("Han: ${bill.dueDate.toDisplayDate()}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Dat cho nhanh", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FilledTonalButton(onClick = onNavigateToBooking) {
                    Icon(Icons.Outlined.Pool, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Be boi")
                }
                FilledTonalButton(onClick = onNavigateToBooking) {
                    Icon(Icons.Outlined.CalendarMonth, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("BBQ")
                }
                FilledTonalButton(onClick = onNavigateToBooking) {
                    Icon(Icons.Outlined.LocalParking, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Bai xe")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Thong bao gan day", style = MaterialTheme.typography.titleMedium)
                TextButton(onClick = onNavigateToNotifications) { Text("Xem tat ca") }
            }

            if (uiState.recentNotifications.isEmpty()) {
                Text("Khong co thong bao", style = MaterialTheme.typography.bodyMedium)
            } else {
                uiState.recentNotifications.forEach { notification ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(notification.title, style = MaterialTheme.typography.titleSmall)
                            Text(
                                notification.content,
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 2
                            )
                        }
                    }
                }
            }
        }
    }
}
