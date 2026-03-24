package com.exe202.nova.ui.screen.manager

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.exe202.nova.ui.component.AppDrawer
import com.exe202.nova.ui.navigation.ManagerAnnouncementsRoute
import com.exe202.nova.ui.navigation.ManagerApartmentsRoute
import com.exe202.nova.ui.navigation.ManagerBillingRoute
import com.exe202.nova.ui.navigation.ManagerBookingsRoute
import com.exe202.nova.ui.navigation.ManagerCreateAnnouncementRoute
import com.exe202.nova.ui.navigation.ManagerCustomerDetailRoute
import com.exe202.nova.ui.navigation.ManagerCustomersRoute
import com.exe202.nova.ui.navigation.ManagerDashboardRoute
import com.exe202.nova.ui.navigation.ManagerFacilitiesRoute
import com.exe202.nova.ui.navigation.ManagerFeeTypesRoute
import com.exe202.nova.ui.navigation.ManagerReportsRoute
import com.exe202.nova.ui.navigation.ManagerChatRoute
import com.exe202.nova.ui.navigation.ProfileRoute
import com.exe202.nova.ui.navigation.SettingsRoute
import com.exe202.nova.ui.screen.resident.ProfileScreen
import com.exe202.nova.ui.screen.resident.SettingsScreen
import com.exe202.nova.ui.theme.ManagerAccent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagerMainScreen(onLogout: () -> Unit) {
    val nestedNavController = rememberNavController()
    val navBackStackEntry by nestedNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val title = when {
        currentDestination?.hasRoute(ManagerDashboardRoute::class) == true -> "Tổng quan"
        currentDestination?.hasRoute(ManagerBookingsRoute::class) == true -> "Quản lý Booking"
        currentDestination?.hasRoute(ManagerBillingRoute::class) == true -> "Quản lý Hóa đơn"
        currentDestination?.hasRoute(ManagerCustomersRoute::class) == true -> "Quản lý Cư dân"
        currentDestination?.hasRoute(ManagerCustomerDetailRoute::class) == true -> "Chi tiết Cư dân"
        currentDestination?.hasRoute(ManagerApartmentsRoute::class) == true -> "Quản lý Căn hộ"
        currentDestination?.hasRoute(ManagerFacilitiesRoute::class) == true -> "Quản lý Tiện ích"
        currentDestination?.hasRoute(ManagerAnnouncementsRoute::class) == true -> "Thông báo BQL"
        currentDestination?.hasRoute(ManagerCreateAnnouncementRoute::class) == true -> "Tạo Thông báo"
        currentDestination?.hasRoute(ManagerReportsRoute::class) == true -> "Báo cáo"
        currentDestination?.hasRoute(ManagerFeeTypesRoute::class) == true -> "Loại phí"
        currentDestination?.hasRoute(ManagerChatRoute::class) == true -> "Chat"
        currentDestination?.hasRoute(ProfileRoute::class) == true -> "Tài khoản"
        currentDestination?.hasRoute(SettingsRoute::class) == true -> "Cài đặt"
        else -> "Nova"
    }

    AppDrawer(
        isManager = true,
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
    ) {
        Scaffold(
            topBar = {
                Column {
                    TopAppBar(
                        title = { Text(title) },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Mở menu")
                            }
                        }
                    )
                    // Manager accent line below top bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(ManagerAccent)
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = nestedNavController,
                startDestination = ManagerDashboardRoute,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable<ManagerDashboardRoute> {
                    ManagerDashboardScreen()
                }
                composable<ManagerBookingsRoute> {
                    ManagerBookingsScreen()
                }
                composable<ManagerBillingRoute> {
                    ManagerBillingScreen()
                }
                composable<ManagerCustomersRoute> {
                    ManagerCustomersScreen(
                        onNavigateToDetail = { customerId ->
                            nestedNavController.navigate(ManagerCustomerDetailRoute(customerId))
                        }
                    )
                }
                composable<ManagerCustomerDetailRoute> { backStackEntry ->
                    val route = backStackEntry.toRoute<ManagerCustomerDetailRoute>()
                    ManagerCustomerDetailScreen(
                        customerId = route.customerId,
                        onNavigateBack = { nestedNavController.popBackStack() }
                    )
                }
                composable<ManagerApartmentsRoute> {
                    ManagerApartmentsScreen()
                }
                composable<ManagerFacilitiesRoute> {
                    ManagerFacilitiesScreen()
                }
                composable<ManagerAnnouncementsRoute> {
                    ManagerAnnouncementsScreen(
                        onNavigateToCreate = { nestedNavController.navigate(ManagerCreateAnnouncementRoute) }
                    )
                }
                composable<ManagerCreateAnnouncementRoute> {
                    ManagerCreateAnnouncementScreen(
                        onNavigateBack = { nestedNavController.popBackStack() }
                    )
                }
                composable<ManagerReportsRoute> {
                    ManagerReportsScreen()
                }
                composable<ManagerFeeTypesRoute> {
                    ManagerFeeTypesScreen()
                }
                composable<ManagerChatRoute> {
                    ManagerChatScreen()
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
