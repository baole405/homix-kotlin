package com.exe202.nova.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.exe202.nova.data.model.Customer
import com.exe202.nova.data.model.ManagerApartment
import com.exe202.nova.data.model.ManagerBill
import com.exe202.nova.data.model.ManagerBooking
import com.exe202.nova.data.model.ServiceType
import java.text.NumberFormat
import java.util.Locale

private fun formatVnd(amount: Double): String {
    val format = NumberFormat.getNumberInstance(Locale("vi", "VN"))
    return "${format.format(amount.toLong())}đ"
}

private fun serviceTypeLabel(type: ServiceType): String = when (type) {
    ServiceType.SWIMMING_POOL -> "Hồ bơi"
    ServiceType.BBQ -> "BBQ"
    ServiceType.PARKING -> "Bãi xe"
}

@Composable
fun ManagerBookingCard(
    booking: ManagerBooking,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(booking.residentName, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    Text(booking.apartmentUnit, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                BookingStatusChip(status = booking.status)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(serviceTypeLabel(booking.serviceType), fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                booking.slotNumber?.let { Text("• $it", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
            Text("${booking.date}  ${booking.startTime} - ${booking.endTime}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun ManagerBillRow(
    bill: ManagerBill,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(bill.residentName, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Text("${bill.apartmentUnit} • ${bill.period}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(bill.title, fontSize = 13.sp)
                Text("Hạn: ${bill.dueDate}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(formatVnd(bill.amount), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(4.dp))
                BillStatusChip(status = bill.status)
            }
        }
    }
}

@Composable
fun CustomerCard(
    customer: Customer,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar with initials
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = customer.name.take(1).uppercase(),
                    modifier = Modifier.padding(12.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(customer.name, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Text(customer.email, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                customer.apartmentUnit?.let {
                    Text("Phòng: $it", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
fun ApartmentCard(
    apartment: ManagerApartment,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Phòng ${apartment.unitNumber}",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
                Text(
                    "Block ${apartment.block} • Tầng ${apartment.floor} • ${apartment.area}m²",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                apartment.residentName?.let {
                    Text(it, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                }
                Text(
                    formatVnd(apartment.monthlyFee) + "/tháng",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            ApartmentStatusChip(status = apartment.status)
        }
    }
}
