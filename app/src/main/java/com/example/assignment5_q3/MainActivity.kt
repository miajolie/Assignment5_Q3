package com.example.assignment5_q3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
//NEW IMPORTS
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

// DATA MODELS

data class Location(
    val id: Int,
    val name: String,
    val description: String,
    val rating: Double,
    val emoji: String,
    val address: String,
    val hours: String,
    val admission: String
)

data class Category(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val color: Color,
    val description: String
)

// VIEWMODEL WITH TOUR DATA

class TourViewModel : ViewModel() {
    val categories = listOf(
        Category(
            "museums",
            "Museums",
            Icons.Default.AccountBalance,
            Color(0xFF9333EA),
            "Explore art, science, and history"
        ),
        Category(
            "parks",
            "Parks",
            Icons.Default.Park,
            Color(0xFF10B981),
            "Discover green spaces and nature"
        ),
        Category(
            "restaurants",
            "Restaurants",
            Icons.Default.Restaurant,
            Color(0xFFF97316),
            "Taste the best local cuisine"
        )
    )

    private val allLocations = mapOf(
        "museums" to listOf(
            Location(
                1,
                "MIT Museum",
                "Explore cutting-edge science and technology exhibits showcasing innovation and research from one of the world's leading institutions.",
                4.8,
                "🏛️",
                "265 Massachusetts Ave, Cambridge, MA",
                "10 AM - 5 PM Daily",
                "$10 Adults, $5 Students"
            ),
            Location(
                2,
                "Museum of Fine Arts",
                "World-class art collection spanning millennia, from ancient Egyptian artifacts to contemporary masterpieces.",
                4.9,
                "🎨",
                "465 Huntington Ave, Boston, MA",
                "10 AM - 5 PM, Closed Tuesdays",
                "$27 Adults, Free for under 18"
            ),
            Location(
                3,
                "Isabella Stewart Gardner Museum",
                "Venetian-style palace with stunning art collection displayed in intimate galleries surrounding a lush courtyard.",
                4.7,
                "🖼️",
                "25 Evans Way, Boston, MA",
                "11 AM - 5 PM, Closed Tuesdays",
                "$20 Adults, Free for visitors named Isabella"
            )
        ),
        "parks" to listOf(
            Location(
                4,
                "Boston Common",
                "America's oldest public park established in 1634, perfect for picnics, swan boats, and ice skating in winter.",
                4.6,
                "🌳",
                "139 Tremont St, Boston, MA",
                "Open 24 hours",
                "Free admission"
            ),
            Location(
                5,
                "Arnold Arboretum",
                "287 acres of trees and plants from around the world, part of Harvard University's living laboratory.",
                4.8,
                "🌲",
                "125 Arborway, Boston, MA",
                "Dawn to Dusk Daily",
                "Free admission"
            ),
            Location(
                6,
                "Charles River Esplanade",
                "Beautiful riverside park with walking and biking paths, offering stunning views of the Boston skyline.",
                4.7,
                "🏞️",
                "Along Charles River, Boston, MA",
                "Open 24 hours",
                "Free admission"
            )
        ),
        "restaurants" to listOf(
            Location(
                7,
                "Legal Sea Foods",
                "Boston's iconic seafood restaurant since 1950, famous for its clam chowder served at presidential inaugurations.",
                4.5,
                "🦞",
                "Multiple locations throughout Boston",
                "11 AM - 10 PM Daily",
                "$$ - $$$"
            ),
            Location(
                8,
                "Neptune Oyster",
                "Intimate spot in the North End known for incredible lobster rolls, fresh oysters, and Italian-inspired seafood.",
                4.9,
                "🦪",
                "63 Salem St, Boston, MA",
                "11:30 AM - 9:30 PM Daily",
                "$$ - $$$"
            ),
            Location(
                9,
                "Union Oyster House",
                "America's oldest continuously operating restaurant, serving seafood since 1826 in a historic building.",
                4.4,
                "🍽️",
                "41 Union St, Boston, MA",
                "11 AM - 10 PM Daily",
                "$$ - $$$"
            )
        )
    )

    fun getLocationsForCategory(categoryId: String): List<Location> {
        return allLocations[categoryId] ?: emptyList()
    }

    fun getLocationById(categoryId: String, locationId: Int): Location? {
        return allLocations[categoryId]?.find { it.id == locationId }
    }

    fun getCategoryById(categoryId: String): Category? {
        return categories.find { it.id == categoryId }
    }
}

// NAVIGATION ROUTES

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Categories : Screen("categories")
    object LocationList : Screen("list/{category}") {
        fun createRoute(category: String) = "list/$category"
    }
    object LocationDetail : Screen("detail/{category}/{locationId}") {
        fun createRoute(category: String, locationId: Int) = "detail/$category/$locationId"
    }
}

// MAIN ACTIVITY

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                CityTourApp()
            }
        }
    }
}

// MAIN APP COMPOSABLE

@Composable
fun CityTourApp() {
    val navController = rememberNavController()
    val viewModel: TourViewModel = viewModel()

    Scaffold { padding ->
        Box(modifier = Modifier.padding(padding)) {
            TourNavGraph(
                navController = navController,
                viewModel = viewModel
            )
        }
    }
}

// NAVIGATION GRAPH

@Composable
fun TourNavGraph(
    navController: NavHostController,
    viewModel: TourViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        // Home Screen
        composable(Screen.Home.route) {
            HomeScreen(
                onStartTour = {
                    navController.navigate(Screen.Categories.route)
                }
            )
        }

        // Categories Screen
        composable(Screen.Categories.route) {
            CategoriesScreen(
                categories = viewModel.categories,
                onCategoryClick = { categoryId ->
                    // Using String argument in navigation
                    navController.navigate(Screen.LocationList.createRoute(categoryId))
                },
                onHomeClick = {
                    // Clear stack when going home
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) {
                            inclusive = true
                        }
                    }
                },
                canNavigateBack = navController.previousBackStackEntry != null,
                onBackClick = { navController.navigateUp() }
            )
        }

        // Location List Screen - String argument
        composable(
            route = Screen.LocationList.route,
            arguments = listOf(
                navArgument("category") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("category") ?: ""
            val locations = viewModel.getLocationsForCategory(categoryId)
            val category = viewModel.getCategoryById(categoryId)

            LocationListScreen(
                category = category,
                locations = locations,
                onLocationClick = { locationId ->
                    // Using both String and Int arguments
                    navController.navigate(
                        Screen.LocationDetail.createRoute(categoryId, locationId)
                    )
                },
                onHomeClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) {
                            inclusive = true
                        }
                    }
                },
                canNavigateBack = navController.previousBackStackEntry != null,
                onBackClick = { navController.navigateUp() }
            )
        }

        // Location Detail Screen - String and Int arguments
        composable(
            route = Screen.LocationDetail.route,
            arguments = listOf(
                navArgument("category") {
                    type = NavType.StringType
                },
                navArgument("locationId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("category") ?: ""
            val locationId = backStackEntry.arguments?.getInt("locationId") ?: 0
            val location = viewModel.getLocationById(categoryId, locationId)

            location?.let {
                LocationDetailScreen(
                    location = it,
                    onPlanAnotherTour = {
                        // Navigate home and clear entire stack
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) {
                                inclusive = true
                            }
                        }
                    },
                    onHomeClick = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) {
                                inclusive = true
                            }
                        }
                    },
                    canNavigateBack = navController.previousBackStackEntry != null,
                    onBackClick = { navController.navigateUp() }
                )
            }
        }
    }
}

// SCREEN COMPOSABLES

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onStartTour: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Boston City Tour", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4F46E5),
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFEEF2FF),
                            Color(0xFFE0E7FF)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                Text(
                    text = "🏙️",
                    fontSize = 80.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = "Welcome to Boston",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                Text(
                    text = "Discover the best museums, parks, and restaurants in the city",
                    fontSize = 18.sp,
                    color = Color(0xFF64748B),
                    modifier = Modifier.padding(vertical = 16.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Button(
                    onClick = onStartTour,
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4F46E5)
                    )
                ) {
                    Text(
                        "Start Your Tour",
                        fontSize = 18.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    categories: List<Category>,
    onCategoryClick: (String) -> Unit,
    onHomeClick: () -> Unit,
    canNavigateBack: Boolean,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Explore Boston", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    if (canNavigateBack) {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onHomeClick) {
                        Icon(Icons.Default.Home, "Home", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4F46E5),
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF9FAFB))
                .padding(16.dp)
        ) {
            Text(
                text = "Choose a category to discover amazing places",
                fontSize = 16.sp,
                color = Color(0xFF64748B),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            categories.forEach { category ->
                CategoryCard(
                    category = category,
                    onClick = { onCategoryClick(category.id) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun CategoryCard(category: Category, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(category.color),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    category.icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(
                    text = category.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                Text(
                    text = category.description,
                    fontSize = 14.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationListScreen(
    category: Category?,
    locations: List<Location>,
    onLocationClick: (Int) -> Unit,
    onHomeClick: () -> Unit,
    canNavigateBack: Boolean,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(category?.name ?: "Locations", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    if (canNavigateBack) {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onHomeClick) {
                        Icon(Icons.Default.Home, "Home", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4F46E5),
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF9FAFB)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(locations) { location ->
                LocationCard(
                    location = location,
                    onClick = { onLocationClick(location.id) }
                )
            }
        }
    }
}

@Composable
fun LocationCard(location: Location, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = location.emoji,
                fontSize = 48.sp,
                modifier = Modifier.padding(end = 16.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = location.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                Text(
                    text = location.description,
                    fontSize = 14.sp,
                    color = Color(0xFF64748B),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⭐", fontSize = 16.sp)
                    Text(
                        text = location.rating.toString(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFEAB308),
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationDetailScreen(
    location: Location,
    onPlanAnotherTour: () -> Unit,
    onHomeClick: () -> Unit,
    canNavigateBack: Boolean,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    if (canNavigateBack) {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onHomeClick) {
                        Icon(Icons.Default.Home, "Home", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4F46E5),
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                // Header with gradient
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF4F46E5),
                                    Color(0xFF7C3AED)
                                )
                            )
                        )
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = location.emoji, fontSize = 80.sp)
                        Text(
                            text = location.name,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("⭐", fontSize = 20.sp)
                            Text(
                                text = location.rating.toString(),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFBBF24),
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                }
            }

            item {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "About",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = location.description,
                        fontSize = 16.sp,
                        color = Color(0xFF475569),
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF1F5F9)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            DetailRow(icon = "📍", label = "Address", value = location.address)
                            Spacer(modifier = Modifier.height(8.dp))
                            DetailRow(icon = "🕐", label = "Hours", value = location.hours)
                            Spacer(modifier = Modifier.height(8.dp))
                            DetailRow(icon = "💰", label = "Admission", value = location.admission)
                        }
                    }

                    Button(
                        onClick = onPlanAnotherTour,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4F46E5)
                        )
                    ) {
                        Text("Plan Another Tour", fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(icon: String, label: String, value: String) {
    Row(verticalAlignment = Alignment.Top) {
        Text(text = icon, fontSize = 20.sp, modifier = Modifier.padding(end = 8.dp))
        Column {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color(0xFF64748B),
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                fontSize = 14.sp,
                color = Color(0xFF1E293B)
            )
        }
    }
}