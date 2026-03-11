package com.exe202.nova.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.HistoryEdu
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.MeetingRoom
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Pool
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import com.exe202.nova.ui.navigation.BillsRoute
import com.exe202.nova.ui.navigation.BookingRoute
import com.exe202.nova.ui.navigation.DashboardRoute
import com.exe202.nova.ui.navigation.ManagerAnnouncementsRoute
import com.exe202.nova.ui.navigation.ManagerApartmentsRoute
import com.exe202.nova.ui.navigation.ManagerBillingRoute
import com.exe202.nova.ui.navigation.ManagerBookingsRoute
import com.exe202.nova.ui.navigation.ManagerCustomersRoute
import com.exe202.nova.ui.navigation.ManagerDashboardRoute
import com.exe202.nova.ui.navigation.ManagerFacilitiesRoute
import com.exe202.nova.ui.navigation.ManagerFeeTypesRoute
import com.exe202.nova.ui.navigation.ManagerReportsRoute
import com.exe202.nova.ui.navigation.NotificationsRoute
import com.exe202.nova.ui.navigation.ProfileRoute
import com.exe202.nova.ui.navigation.TransactionHistoryRoute
import com.exe202.nova.ui.theme.LocalIsDarkTheme
import com.exe202.nova.ui.theme.LocalToggleTheme
import com.exe202.nova.ui.theme.ManagerAccent
import kotlinx.coroutines.launch

private data class DrawerItem(
    val route: Any,
    val label: String,
    val icon: ImageVector
)

private val residentItems = listOf(
    DrawerItem(DashboardRoute, "Tổng quan", Icons.Outlined.Home),
    null, // divider
    DrawerItem(BillsRoute, "Hóa đơn", Icons.Outlined.Receipt),
    DrawerItem(BookingRoute, "Đặt chỗ", Icons.Outlined.CalendarMonth),
    DrawerItem(TransactionHistoryRoute, "Lịch sử giao dịch", Icons.Outlined.HistoryEdu),
    null, // divider
    DrawerItem(NotificationsRoute, "Thông báo", Icons.Outlined.Notifications),
    DrawerItem(ProfileRoute, "Tài khoản", Icons.Outlined.Person),
)

private val managerItems = listOf(
    DrawerItem(ManagerDashboardRoute, "Tổng quan", Icons.Outlined.Dashboard),
    null, // divider
    DrawerItem(ManagerBookingsRoute, "Quản lý Booking", Icons.Outlined.CalendarMonth),
    DrawerItem(ManagerBillingRoute, "Quản lý Hóa đơn", Icons.Outlined.Receipt),
    DrawerItem(ManagerCustomersRoute, "Quản lý Cư dân", Icons.Outlined.People),
    null, // divider
    DrawerItem(ManagerApartmentsRoute, "Quản lý Căn hộ", Icons.Outlined.Apartment),
    DrawerItem(ManagerFacilitiesRoute, "Quản lý Tiện ích", Icons.Outlined.Pool),
    DrawerItem(ManagerFeeTypesRoute, "Loại phí", Icons.Outlined.Payments),
    DrawerItem(ManagerAnnouncementsRoute, "Thông báo BQL", Icons.Outlined.Campaign),
    DrawerItem(ManagerReportsRoute, "Báo cáo", Icons.Outlined.Assessment),
    null, // divider
    DrawerItem(ProfileRoute, "Tài khoản", Icons.Outlined.Person),
)

@Composable
fun AppDrawer(
    isManager: Boolean,
    drawerState: DrawerState,
    currentDestination: NavDestination?,
    onNavigateTo: (Any) -> Unit,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    val isDark = LocalIsDarkTheme.current
    val toggleTheme = LocalToggleTheme.current
    val items = if (isManager) managerItems else residentItems
    val accentColor = if (isManager) ManagerAccent else MaterialTheme.colorScheme.primary

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                // Header
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.AccountCircle,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isManager) "Quản lý" else "Cư dân",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = if (isManager) "Manager" else "Resident",
                        fontSize = 12.sp,
                        color = if (isManager) ManagerAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }

                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))

                // Nav Items
                items.forEach { item ->
                    if (item == null) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    } else {
                        val isSelected = currentDestination?.hasRoute(item.route::class) == true
                        val itemColor = if (isSelected && isManager) ManagerAccent else accentColor
                        NavigationDrawerItem(
                            icon = {
                                Icon(
                                    item.icon,
                                    contentDescription = item.label,
                                    tint = if (isSelected) itemColor
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            label = {
                                Text(
                                    item.label,
                                    color = if (isSelected) itemColor
                                    else MaterialTheme.colorScheme.onSurface
                                )
                            },
                            selected = isSelected,
                            onClick = {
                                onNavigateTo(item.route)
                                scope.launch { drawerState.close() }
                            },
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = if (isManager)
                                    ManagerAccent.copy(alpha = 0.12f)
                                else MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                            ),
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
                HorizontalDivider()

                // Footer: theme toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isDark) "Chế độ tối" else "Chế độ sáng",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { toggleTheme() }) {
                        Icon(
                            imageVector = if (isDark) Icons.Outlined.LightMode else Icons.Outlined.Bedtime,
                            contentDescription = "Toggle theme"
                        )
                    }
                }
            }
        },
        content = content
    )
}
