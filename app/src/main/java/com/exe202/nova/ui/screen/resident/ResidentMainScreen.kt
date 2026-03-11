package com.exe202.nova.ui.screen.resident

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.exe202.nova.ui.component.AppDrawer
import com.exe202.nova.ui.navigation.BillDetailRoute
import com.exe202.nova.ui.navigation.BillsRoute
import com.exe202.nova.ui.navigation.BookingRoute
import com.exe202.nova.ui.navigation.DashboardRoute
import com.exe202.nova.ui.navigation.MyBookingsRoute
import com.exe202.nova.ui.navigation.NotificationsRoute
import com.exe202.nova.ui.navigation.ProfileRoute
import com.exe202.nova.ui.navigation.SettingsRoute
import com.exe202.nova.ui.navigation.TransactionHistoryRoute
import kotlinx.coroutines.launch

private fun screenTitle(route: Any?): String = when (route) {
    is DashboardRoute -> "Tổng quan"
    is BillsRoute -> "Hóa đơn"
    is BookingRoute -> "Đặt chỗ"
    is NotificationsRoute -> "Thông báo"
    is ProfileRoute -> "Tài khoản"
    is TransactionHistoryRoute -> "Lịch sử giao dịch"
    else -> "Nova"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResidentMainScreen(onLogout: () -> Unit) {
    val nestedNavController = rememberNavController()
    val navBackStackEntry by nestedNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val title = when {
        currentDestination?.hasRoute(DashboardRoute::class) == true -> "Tổng quan"
        currentDestination?.hasRoute(BillsRoute::class) == true -> "Hóa đơn"
        currentDestination?.hasRoute(BillDetailRoute::class) == true -> "Chi tiết hóa đơn"
        currentDestination?.hasRoute(BookingRoute::class) == true -> "Đặt chỗ"
        currentDestination?.hasRoute(MyBookingsRoute::class) == true -> "Đặt chỗ của tôi"
        currentDestination?.hasRoute(NotificationsRoute::class) == true -> "Thông báo"
        currentDestination?.hasRoute(ProfileRoute::class) == true -> "Tài khoản"
        currentDestination?.hasRoute(TransactionHistoryRoute::class) == true -> "Lịch sử giao dịch"
        currentDestination?.hasRoute(SettingsRoute::class) == true -> "Cài đặt"
        else -> "Nova"
    }

    AppDrawer(
        isManager = false,
        drawerState = drawerState,
        currentDestination = currentDestination,
        onNavigateTo = { route ->
            nestedNavController.navigate(route) {
                popUpTo(nestedNavController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        },
        onLogout = onLogout
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(title) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Mở menu")
                        }
                    }
                )
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
}
