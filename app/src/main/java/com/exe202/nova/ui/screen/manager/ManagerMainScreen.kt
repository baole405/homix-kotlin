package com.exe202.nova.ui.screen.manager

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.exe202.nova.ui.navigation.ManagerAnnouncementsRoute
import com.exe202.nova.ui.navigation.ManagerApartmentsRoute
import com.exe202.nova.ui.navigation.ManagerBillingRoute
import com.exe202.nova.ui.navigation.ManagerBookingsRoute
import com.exe202.nova.ui.navigation.ManagerCreateAnnouncementRoute
import com.exe202.nova.ui.navigation.ManagerCustomerDetailRoute
import com.exe202.nova.ui.navigation.ManagerCustomersRoute
import com.exe202.nova.ui.navigation.ManagerDashboardRoute
import com.exe202.nova.ui.navigation.ManagerFacilitiesRoute
import com.exe202.nova.ui.navigation.ManagerMoreRoute
import com.exe202.nova.ui.navigation.ManagerReportsRoute
import com.exe202.nova.ui.screen.resident.ResidentTab
import com.exe202.nova.ui.theme.NovaTheme

private val managerTabs = listOf(
    ResidentTab(ManagerDashboardRoute, "Tổng quan", Icons.Outlined.Dashboard),
    ResidentTab(ManagerBookingsRoute, "Đặt chỗ", Icons.Outlined.CalendarMonth),
    ResidentTab(ManagerBillingRoute, "Hóa đơn", Icons.Outlined.Receipt),
    ResidentTab(ManagerCustomersRoute, "Cư dân", Icons.Outlined.People),
    ResidentTab(ManagerMoreRoute, "Thêm", Icons.Outlined.MoreHoriz),
)

@Composable
fun ManagerMainScreen(onLogout: () -> Unit) {
    val nestedNavController = rememberNavController()
    val navBackStackEntry by nestedNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Tab routes for bottom bar visibility
    val tabRoutes = setOf(
        ManagerDashboardRoute::class,
        ManagerBookingsRoute::class,
        ManagerBillingRoute::class,
        ManagerCustomersRoute::class,
        ManagerMoreRoute::class
    )
    val showBottomBar = tabRoutes.any { currentDestination?.hasRoute(it) == true }

    NovaTheme(isManager = true) {
        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar {
                        managerTabs.forEach { tab ->
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
                composable<ManagerMoreRoute> {
                    ManagerMoreScreen(
                        onNavigateToApartments = { nestedNavController.navigate(ManagerApartmentsRoute) },
                        onNavigateToFacilities = { nestedNavController.navigate(ManagerFacilitiesRoute) },
                        onNavigateToAnnouncements = { nestedNavController.navigate(ManagerAnnouncementsRoute) },
                        onNavigateToReports = { nestedNavController.navigate(ManagerReportsRoute) }
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
            }
        }
    }
}
