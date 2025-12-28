package shuman.ulad.recipe.presentation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import shuman.ulad.recipe.R

sealed class Screen(val route: String, @StringRes val title: Int? = null, val icon: ImageVector? = null) {
    //with menu
    data object RecipeList: Screen("recipe_list", R.string.nav_web, Icons.Default.Home)
    data object LocalRecipes: Screen("local_recipes", R.string.nav_my_recipes, Icons.Default.List)
    data object Favorites : Screen("favorites", R.string.nav_favorites, Icons.Default.Favorite)
    data object Settings : Screen("settings", R.string.nav_settings, Icons.Default.Settings)

    // no menu
    data object RecipeDetail: Screen("recipe_detail/{recipeId}") {
        fun passId(recipeId: String): String {
            return "recipe_detail/$recipeId"
        }
    }
    data object CreateRecipe: Screen("create_recipe")
    data object Backup : Screen("backup")
}