package shuman.ulad.recipe.domain.repository

import kotlinx.coroutines.flow.Flow
import shuman.ulad.recipe.domain.model.Recipe

interface RecipeRepository {
    suspend fun searchRecipes(query: String): List<Recipe>
    suspend fun getRecipeById(id: String): Recipe
    suspend fun insertFavorite(recipe: Recipe)
    suspend fun delete(recipe: Recipe)
    suspend fun isRecipeFavorite(id: String): Boolean
    fun getFavoriteRecipes(): Flow<List<Recipe>>
    suspend fun getFavoriteIds(): List<String>
    suspend fun fetchRandomRecipe(): Recipe
    fun searchLocalRecipes(query: String): Flow<List<Recipe>>
    suspend fun updateFavoriteStatus(id: String, isFavorite: Boolean)
    fun searchFavoriteRecipes(query: String): Flow<List<Recipe>>
}