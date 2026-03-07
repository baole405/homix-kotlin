package com.exe202.nova.ui.screen.resident

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.exe202.nova.ui.navigation.BillDetailRoute
import com.exe202.nova.ui.navigation.BillsRoute
import com.exe202.nova.ui.navigation.BookingRoute
import com.exe202.nova.ui.navigation.DashboardRoute
import com.exe202.nova.ui.navigation.MyBookingsRoute
import com.exe202.nova.ui.navigation.NotificationsRoute
import com.exe202.nova.ui.navigation.ProfileRoute
import com.exe202.nova.ui.navigation.SettingsRoute
import com.exe202.nova.ui.navigation.TransactionHistoryRoute

data class ResidentTab(
    val route: Any,
    val label: String,
    val icon: ImageVector
)

val residentTabs = listOf(
    ResidentTab(DashboardRoute, "Tong quan", Icons.Outlined.Home),
    ResidentTab(BillsRoute, "Hoa don", Icons.Outlined.Receipt),
    ResidentTab(BookingRoute, "Dat cho", Icons.Outlined.CalendarMonth),
    ResidentTab(NotificationsRoute, "Thong bao", Icons.Outlined.Notifications),
    ResidentTab(ProfileRoute, "Tai khoan", Icons.Outlined.Person)
)

@Composable
fun ResidentMainScreen(onLogout: () -> Unit) {
    val nestedNavController = rememberNavController()
    val navBackStackEntry by nestedNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                residentTabs.forEach { tab ->
                    NavigationBarItem(
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) },
                        selected = currentDestination?.hasRoute(tab.route::class) == true,
                        onClick = {
                            nestedNavController.navigate(tab.route) {
                                popUpTo(nestedNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = nestedNavController,
            startDestination = DashboardRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<DashboardRoute> {
                DashboardScreen(
                    onNavigateToBills = { nestedNavController.navigate(BillsRoute) },
                    onNavigateToBooking = { nestedNavController.navigate(BookingRoute) },
                    onNavigateToNotifications = { nestedNavController.navigate(NotificationsRoute) }
                )
            }
            composable<BillsRoute> {
                BillsScreen(
                    onNavigateToBillDetail = { billId ->
                        nestedNavController.navigate(BillDetailRoute(billId))
                    }
                )
            }
            composable<BillDetailRoute> {
                BillDetailScreen(onNavigateBack = { nestedNavController.popBackStack() })
            }
            composable<TransactionHistoryRoute> {
                TransactionHistoryScreen()
            }
            composable<BookingRoute> {
                BookingScreen(
                    onNavigateToMyBookings = {
                        nestedNavController.navigate(MyBookingsRoute)
                    }
                )
            }
            composable<MyBookingsRoute> {
                MyBookingsScreen(onNavigateBack = { nestedNavController.popBackStack() })
            }
            composable<NotificationsRoute> {
                NotificationsScreen()
            }
            composable<ProfileRoute> {
                ProfileScreen(
                    onNavigateToSettings = { nestedNavController.navigate(SettingsRoute) },
                    onLogout = onLogout
                )
            }
            composable<SettingsRoute> {
                SettingsScreen(
                    onNavigateBack = { nestedNavController.popBackStack() },
                    onLogout = onLogout
                )
            }
        }
    }
}
