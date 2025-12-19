package com.snapshopping.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.snapshopping.ui.camera.CameraScreen
import com.snapshopping.ui.inventory.InventoryScreen

/**
 * Navigation routes for the app
 */
object Routes {
    const val CAMERA = "camera"
    const val INVENTORY = "inventory"
}

@Composable
fun SnapShoppingNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.CAMERA
    ) {
        composable(Routes.CAMERA) {
            CameraScreen(
                onNavigateToInventory = {
                    navController.navigate(Routes.INVENTORY) {
                        popUpTo(Routes.CAMERA) { inclusive = false }
                    }
                }
            )
        }

        composable(Routes.INVENTORY) {
            InventoryScreen(
                onNavigateToCamera = {
                    navController.navigate(Routes.CAMERA) {
                        popUpTo(Routes.INVENTORY) { inclusive = false }
                    }
                }
            )
        }
    }
}
