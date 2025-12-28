package shuman.ulad.recipe.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String? = null, val icon: ImageVector? = null) {
    //with menu
    data object RecipeList: Screen("recipe_list", "Web Search", Icons.Default.Home)
    data object LocalRecipes: Screen("local_recipes", "My Recipes", Icons.Default.List)
    data object Favorites : Screen("favorites", "Favorites", Icons.Default.Favorite)
    data object Settings : Screen("settings", "Settings", Icons.Default.Settings)

    // no menu
    data object RecipeDetail: Screen("recipe_detail/{recipeId}") {
        fun passId(recipeId: String): String {
            return "recipe_detail/$recipeId"
        }
    }
    data object CreateRecipe: Screen("create_recipe")
}