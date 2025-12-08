package hu.bme.ait.kalaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import hu.bme.ait.kalaapp.ui.navigation.BottomNavigationBar
import hu.bme.ait.kalaapp.ui.navigation.NavGraph
import hu.bme.ait.kalaapp.ui.navigation.Screen
import hu.bme.ait.kalaapp.ui.theme.KALATheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KALATheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // Determine which screens should show the bottom navigation
                val screensWithBottomNav = listOf(
                    Screen.Home.route,
                    Screen.Search.route,
                    Screen.Menu.route,
                    Screen.Saved.route,
                    Screen.Profile.route
                )

                val shouldShowBottomBar = currentRoute in screensWithBottomNav

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (shouldShowBottomBar) {
                            BottomNavigationBar(
                                navController = navController,
                                currentRoute = currentRoute
                            )
                        }
                    }
                ) { innerPadding ->
                    NavGraph(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
