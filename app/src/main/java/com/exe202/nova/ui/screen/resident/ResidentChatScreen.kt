package com.exe202.nova.ui.screen.resident

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exe202.nova.ui.viewmodel.ResidentAiChatViewModel
import com.exe202.nova.ui.viewmodel.ResidentChatViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ResidentChatScreen(
    managerChatViewModel: ResidentChatViewModel = hiltViewModel(),
    aiChatViewModel: ResidentAiChatViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Chat với Quản lý") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Chat với AI") }
            )
        }

        if (selectedTab == 0) {
            ManagerChatTab(viewModel = managerChatViewModel)
        } else {
            AiChatTab(viewModel = aiChatViewModel)
        }
    }
}

@Composable
private fun ManagerChatTab(viewModel: ResidentChatViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Manager",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Column
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.messages) { message ->
                val mine = message.senderId == uiState.residentId
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (mine) Arrangement.End else Arrangement.Start
                ) {
                    Column(
                        modifier = Modifier
                            .background(
                                if (mine) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceVariant,
                                shape = MaterialTheme.shapes.medium
                            )
                            .padding(10.dp)
                    ) {
                        if (!mine) {
                            Text(
                                text = message.senderName.ifBlank { "Manager" },
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(message.text)
                        Text(
                            text = formatTime(message.createdAtMillis),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = uiState.input,
                onValueChange = viewModel::onInputChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                label = { Text("Nhập tin nhắn") }
            )
            Button(onClick = viewModel::sendMessage) {
                Text("Gửi")
            }
        }

        uiState.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun AiChatTab(viewModel: ResidentAiChatViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "AI Nova",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.messages) { message ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (message.fromResident) Arrangement.End else Arrangement.Start
                ) {
                    Column(
                        modifier = Modifier
                            .background(
                                if (message.fromResident) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.tertiaryContainer,
                                shape = MaterialTheme.shapes.medium
                            )
                            .padding(10.dp)
                    ) {
                        Text(
                            text = if (message.fromResident) "Bạn" else "AI Nova",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(message.text)
                        Text(
                            text = formatTime(message.timestampMillis),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = uiState.input,
                onValueChange = viewModel::onInputChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                enabled = !uiState.isSending,
                label = { Text("Hỏi AI về Nova Home") }
            )
            Button(
                onClick = viewModel::sendMessage,
                enabled = !uiState.isSending
            ) {
                Text(if (uiState.isSending) "..." else "Gửi")
            }
        }

        uiState.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
    }
}

private fun formatTime(value: Long): String {
    if (value <= 0L) return ""
    return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(value))
}
