package com.exe202.nova.ui.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.exe202.nova.data.model.BillStatus
import com.exe202.nova.data.model.BookingStatus
import com.exe202.nova.ui.theme.StatusCancelled
import com.exe202.nova.ui.theme.StatusConfirmed
import com.exe202.nova.ui.theme.StatusOverdue
import com.exe202.nova.ui.theme.StatusPaid
import com.exe202.nova.ui.theme.StatusPending
import com.exe202.nova.ui.theme.StatusRejected

@Composable
fun StatusChip(text: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun BillStatusChip(status: BillStatus, modifier: Modifier = Modifier) {
    val (text, color) = when (status) {
        BillStatus.PENDING -> "Cho thanh toan" to StatusPending
        BillStatus.PAID -> "Da thanh toan" to StatusPaid
        BillStatus.OVERDUE -> "Qua han" to StatusOverdue
        BillStatus.CANCELLED -> "Da huy" to StatusCancelled
    }
    StatusChip(text = text, color = color, modifier = modifier)
}

@Composable
fun BookingStatusChip(status: BookingStatus, modifier: Modifier = Modifier) {
    val (text, color) = when (status) {
        BookingStatus.PENDING -> "Cho duyet" to StatusPending
        BookingStatus.CONFIRMED -> "Da xac nhan" to StatusConfirmed
        BookingStatus.REJECTED -> "Bi tu choi" to StatusRejected
        BookingStatus.CANCELLED -> "Da huy" to StatusCancelled
    }
    StatusChip(text = text, color = color, modifier = modifier)
}
