/*
 * Main entry point for the Namma-Raste application.
 * This activity handles the high-level navigation and sets up the Material 3 theme.
 */

package com.example.nammarasthehealth

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.nammarasthehealth.data.DamageReport
import com.example.nammarasthehealth.data.User
import com.example.nammarasthehealth.ui.*
import com.example.nammarasthehealth.ui.theme.NammaRastheHealthTheme
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {
    
    // Dependency injection via a custom factory (accessing the app's repository)
    private val roadViewModel: RoadViewModel by viewModels {
        RoadViewModelFactory((application as NammaRasteApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize osmdroid configuration - required for tile loading
        Configuration.getInstance().userAgentValue = packageName
        
        // Edge-to-edge for a modern, immersive UI feel
        enableEdgeToEdge()
        
        setContent {
            NammaRastheHealthTheme {
                AppNavigation(roadViewModel)
            }
        }
    }
}

/**
 * Main navigation graph for the application.
 * Handles transitions between Login, Dashboard, Map, and Reporting screens.
 */
@Composable
fun AppNavigation(viewModel: RoadViewModel) {
    val navController = rememberNavController()
    val roads by viewModel.allRoads.collectAsState(initial = emptyList())
    val stats by viewModel.talukStats.collectAsState()
    val user by viewModel.currentUser.collectAsState()
    val context = LocalContext.current

    // Navigation Host defining the app's flow
    NavHost(
        navController = navController, 
        startDestination = if (user == null) "login" else "directory"
    ) {
        
        // --- Authentication Flow ---
        
        composable("login") {
            LoginScreen(
                onLoginClick = { email, pass, onComplete ->
                    viewModel.login(email, pass) { success ->
                        onComplete() // Reset loading state in the UI
                        if (success) {
                            navController.navigate("directory") {
                                popUpTo("login") { inclusive = true }
                            }
                        } else {
                            Toast.makeText(context, "Incorrect email or password", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                onSignUpClick = {
                    navController.navigate("signup")
                }
            )
        }

        composable("signup") {
            SignUpScreen(
                onSignUpClick = { name, email, pass, onComplete ->
                    viewModel.signUp(User(name = name, email = email, password = pass)) { success ->
                        onComplete() // Reset loading state in the UI
                        if (success) {
                            navController.navigate("directory") {
                                popUpTo("login") { inclusive = true }
                            }
                        } else {
                            Toast.makeText(context, "This email is already registered", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                onLoginClick = {
                    navController.popBackStack()
                }
            )
        }

        // --- Main App Content ---

        composable("directory") {
            RoadDirectoryScreen(
                roads = roads,
                talukStats = stats,
                userName = user?.name ?: "Guest",
                onRoadClick = { road ->
                    navController.navigate("detail/${road.id}")
                },
                onAddRoadClick = {
                    navController.navigate("add_road")
                },
                onMapClick = {
                    navController.navigate("map")
                },
                onLogoutClick = {
                    viewModel.logout()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onRefreshClick = {
                    viewModel.refreshRoadData()
                    Toast.makeText(context, "Fetching latest data...", Toast.LENGTH_SHORT).show()
                }
            )
        }

        composable("add_road") {
            AddRoadScreen(
                onAddRoad = { newRoad ->
                    viewModel.insertRoad(newRoad)
                    Toast.makeText(context, "Road project added successfully", Toast.LENGTH_SHORT).show()
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("map") {
            RoadMapScreen(
                roads = roads,
                onRoadClick = { road ->
                    navController.navigate("detail/${road.id}")
                },
                onBackClick = {
                    navController.popBackStack()
                },
                onRefreshClick = {
                    viewModel.refreshRoadData()
                }
            )
        }

        // --- Dynamic Detail & Reporting Screens ---

        composable(
            route = "detail/{roadId}",
            arguments = listOf(navArgument("roadId") { type = NavType.IntType })
        ) { entry ->
            val id = entry.arguments?.getInt("roadId") ?: 0
            val road by viewModel.getRoadById(id).collectAsState(initial = null)
            val reports by viewModel.getReportsForRoad(id).collectAsState(initial = emptyList())
            
            road?.let { r ->
                RoadDetailScreen(
                    road = r,
                    reports = reports,
                    onReportDamageClick = {
                        navController.navigate("report/${r.id}/${r.name}")
                    },
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }

        composable(
            route = "report/{roadId}/{roadName}",
            arguments = listOf(
                navArgument("roadId") { type = NavType.IntType },
                navArgument("roadName") { type = NavType.StringType }
            )
        ) { entry ->
            val roadId = entry.arguments?.getInt("roadId") ?: 0
            val name = entry.arguments?.getString("roadName") ?: ""
            
            DamageReportScreen(
                roadName = name,
                onReportSubmitted = { desc, photo ->
                    viewModel.reportDamage(
                        DamageReport(
                            roadId = roadId,
                            description = desc,
                            latitude = 0.0,
                            longitude = 0.0,
                            photoData = photo
                        )
                    )
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
