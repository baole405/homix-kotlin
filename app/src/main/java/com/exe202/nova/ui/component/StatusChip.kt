package com.exe202.nova.ui.component

import androidx.compose.foundation.BorderStroke
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
import com.exe202.nova.data.model.ApartmentStatus
import com.exe202.nova.data.model.BillStatus
import com.exe202.nova.data.model.BookingStatus
import com.exe202.nova.ui.theme.LocalIsDarkTheme
import com.exe202.nova.ui.theme.StatusCancelledDarkBg
import com.exe202.nova.ui.theme.StatusCancelledDarkText
import com.exe202.nova.ui.theme.StatusCancelledLightBg
import com.exe202.nova.ui.theme.StatusCancelledLightText
import com.exe202.nova.ui.theme.StatusErrorDarkBg
import com.exe202.nova.ui.theme.StatusErrorDarkText
import com.exe202.nova.ui.theme.StatusErrorLightBg
import com.exe202.nova.ui.theme.StatusErrorLightText
import com.exe202.nova.ui.theme.StatusPendingDarkBg
import com.exe202.nova.ui.theme.StatusPendingDarkText
import com.exe202.nova.ui.theme.StatusPendingLightBg
import com.exe202.nova.ui.theme.StatusPendingLightText
import com.exe202.nova.ui.theme.StatusSuccessDarkBg
import com.exe202.nova.ui.theme.StatusSuccessDarkText
import com.exe202.nova.ui.theme.StatusSuccessLightBg
import com.exe202.nova.ui.theme.StatusSuccessLightText

@Composable
fun StatusChip(
    text: String,
    bgColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = bgColor,
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.dp, textColor.copy(alpha = 0.5f)),
        modifier = modifier
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun BillStatusChip(status: BillStatus, modifier: Modifier = Modifier) {
    val isDark = LocalIsDarkTheme.current
    val (bg, text) = when (status) {
        BillStatus.PENDING -> if (isDark) StatusPendingDarkBg to StatusPendingDarkText
                              else StatusPendingLightBg to StatusPendingLightText
        BillStatus.PAID -> if (isDark) StatusSuccessDarkBg to StatusSuccessDarkText
                           else StatusSuccessLightBg to StatusSuccessLightText
        BillStatus.OVERDUE -> if (isDark) StatusErrorDarkBg to StatusErrorDarkText
                              else StatusErrorLightBg to StatusErrorLightText
        BillStatus.CANCELLED -> if (isDark) StatusCancelledDarkBg to StatusCancelledDarkText
                                else StatusCancelledLightBg to StatusCancelledLightText
    }
    val label = when (status) {
        BillStatus.PENDING -> "Chờ thanh toán"
        BillStatus.PAID -> "Đã thanh toán"
        BillStatus.OVERDUE -> "Quá hạn"
        BillStatus.CANCELLED -> "Đã hủy"
    }
    StatusChip(text = label, bgColor = bg, textColor = text, modifier = modifier)
}

@Composable
fun BookingStatusChip(status: BookingStatus, modifier: Modifier = Modifier) {
    val isDark = LocalIsDarkTheme.current
    val (bg, text) = when (status) {
        BookingStatus.PENDING -> if (isDark) StatusPendingDarkBg to StatusPendingDarkText
                                 else StatusPendingLightBg to StatusPendingLightText
        BookingStatus.CONFIRMED -> if (isDark) StatusSuccessDarkBg to StatusSuccessDarkText
                                   else StatusSuccessLightBg to StatusSuccessLightText
        BookingStatus.REJECTED -> if (isDark) StatusErrorDarkBg to StatusErrorDarkText
                                  else StatusErrorLightBg to StatusErrorLightText
        BookingStatus.CANCELLED -> if (isDark) StatusCancelledDarkBg to StatusCancelledDarkText
                                   else StatusCancelledLightBg to StatusCancelledLightText
    }
    val label = when (status) {
        BookingStatus.PENDING -> "Chờ duyệt"
        BookingStatus.CONFIRMED -> "Đã xác nhận"
        BookingStatus.REJECTED -> "Bị từ chối"
        BookingStatus.CANCELLED -> "Đã hủy"
    }
    StatusChip(text = label, bgColor = bg, textColor = text, modifier = modifier)
}

@Composable
fun ApartmentStatusChip(status: ApartmentStatus, modifier: Modifier = Modifier) {
    val isDark = LocalIsDarkTheme.current
    val (bg, text) = when (status) {
        ApartmentStatus.OCCUPIED -> if (isDark) StatusSuccessDarkBg to StatusSuccessDarkText
                                    else StatusSuccessLightBg to StatusSuccessLightText
        ApartmentStatus.VACANT -> if (isDark) StatusPendingDarkBg to StatusPendingDarkText
                                  else StatusPendingLightBg to StatusPendingLightText
        ApartmentStatus.MAINTENANCE -> if (isDark) StatusErrorDarkBg to StatusErrorDarkText
                                       else StatusErrorLightBg to StatusErrorLightText
    }
    val label = when (status) {
        ApartmentStatus.OCCUPIED -> "Có người ở"
        ApartmentStatus.VACANT -> "Trống"
        ApartmentStatus.MAINTENANCE -> "Bảo trì"
    }
    StatusChip(text = label, bgColor = bg, textColor = text, modifier = modifier)
}
