package hu.bme.ait.kalaapp.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Search : Screen("search")
    object Menu : Screen("menu")
    object Saved : Screen("saved")
    object Profile : Screen("profile")
    object Login : Screen("login")
    object Register : Screen("register")
    object BrandDetail : Screen("brand/{brandId}") {
        fun createRoute(brandId: String) = "brand/$brandId"
    }
    object ProductDetail : Screen("product/{productId}") {
        fun createRoute(productId: String) = "product/$productId"
    }
}