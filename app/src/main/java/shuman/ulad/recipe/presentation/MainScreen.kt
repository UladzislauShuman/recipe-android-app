package com.example.recipekeeper.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import shuman.ulad.recipe.presentation.Screen
import shuman.ulad.recipe.presentation.create_recipe.components.CreateRecipeScreen
import shuman.ulad.recipe.presentation.favorites.components.FavoritesScreen
import shuman.ulad.recipe.presentation.recipe_detail.components.RecipeDetailScreen
import shuman.ulad.recipe.presentation.recipe_list.components.RecipeListScreen
import shuman.ulad.recipe.presentation.local_recipes.components.LocalRecipesScreen
import shuman.ulad.recipe.presentation.settings.components.BackupScreen
import shuman.ulad.recipe.presentation.settings.components.SettingsScreen

@Composable
fun MainScreen(
    deepLinkRecipeId: String? = null
) {
    val navController = rememberNavController()
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->

        }
    )

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionCheck = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            )
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    LaunchedEffect(deepLinkRecipeId) {
        if (deepLinkRecipeId != null) {
            navController.navigate(Screen.RecipeDetail.passId(deepLinkRecipeId))
        }
    }

    val bottomScreens = listOf(
        Screen.RecipeList,
        Screen.LocalRecipes,
        Screen.Favorites,
        Screen.Settings
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = bottomScreens.any { it.route == currentDestination?.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomScreens.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon!!, contentDescription = null) },
                            label = { Text(stringResource(screen.title!!)) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
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
            navController = navController,
            startDestination = Screen.RecipeList.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.RecipeList.route) {
                RecipeListScreen(
                    onNavigateToDetail = { id -> navController.navigate(Screen.RecipeDetail.passId(id)) }
                )
            }

            composable(Screen.LocalRecipes.route) {
                LocalRecipesScreen(
                    onNavigateToDetail = { id -> navController.navigate(Screen.RecipeDetail.passId(id)) },
                    onNavigateToCreate = { navController.navigate(Screen.CreateRecipe.route) }
                )
            }

            composable(Screen.Favorites.route) {
                FavoritesScreen(
                    onNavigateToDetail = { id -> navController.navigate(Screen.RecipeDetail.passId(id)) }
                )
            }

            composable(Screen.RecipeDetail.route) {
                RecipeDetailScreen()
            }

            composable(Screen.CreateRecipe.route) {
                CreateRecipeScreen(
                    onRecipeSaved = { navController.popBackStack() }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateToBackup = { navController.navigate(Screen.Backup.route) }
                )
            }

            composable(Screen.Backup.route) {
                BackupScreen()
            }
        }
    }
}