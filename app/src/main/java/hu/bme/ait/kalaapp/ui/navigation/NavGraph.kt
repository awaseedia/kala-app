package hu.bme.ait.kalaapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import hu.bme.ait.kalaapp.ui.screens.auth.LoginScreen
import hu.bme.ait.kalaapp.ui.screens.auth.RegisterScreen
import hu.bme.ait.kalaapp.ui.screens.brand.BrandDetailScreen
import hu.bme.ait.kalaapp.ui.screens.home.HomeScreen
import hu.bme.ait.kalaapp.ui.screens.menu.MenuScreen
import hu.bme.ait.kalaapp.ui.screens.product.ProductDetailScreen
import hu.bme.ait.kalaapp.ui.screens.profile.ProfileScreen
import hu.bme.ait.kalaapp.ui.screens.saved.SavedScreen
import hu.bme.ait.kalaapp.ui.screens.search.SearchScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        // Bottom Navigation Screens
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToProduct = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                },
                onNavigateToBrand = { brandId ->
                    navController.navigate(Screen.BrandDetail.createRoute(brandId))
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                }
            )
        }

        composable(Screen.Search.route) {
            SearchScreen(
                onNavigateToProduct = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                },
                onNavigateToBrand = { brandId ->
                    navController.navigate(Screen.BrandDetail.createRoute(brandId))
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Menu.route) {
            MenuScreen(
                onNavigateToBrand = { brandId ->
                    navController.navigate(Screen.BrandDetail.createRoute(brandId))
                }
            )
        }

        composable(Screen.Saved.route) {
            SavedScreen(
                onNavigateToProduct = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                }
            )
        }

        // Detail Screens
        composable(
            route = Screen.BrandDetail.route,
            arguments = listOf(navArgument("brandId") { type = NavType.StringType })
        ) { backStackEntry ->
            val brandId = backStackEntry.arguments?.getString("brandId") ?: return@composable
            BrandDetailScreen(
                brandId = brandId,
                onNavigateToProduct = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.ProductDetail.route,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: return@composable
            ProductDetailScreen(
                productId = productId,
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // Auth Screens
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = {
                    navController.popBackStack()
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.popBackStack(Screen.Login.route, inclusive = true)
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
