package shuman.ulad.recipe.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import shuman.ulad.recipe.data.local.RecipeDao
import shuman.ulad.recipe.data.local.entity.toEntity
import shuman.ulad.recipe.data.remote.api.RecipeApi
import shuman.ulad.recipe.data.remote.dto.RecipeResponseDto
import shuman.ulad.recipe.data.remote.dto.toDomain
import shuman.ulad.recipe.domain.model.Recipe
import shuman.ulad.recipe.domain.repository.RecipeRepository
import java.io.File
import javax.inject.Inject

class RecipeRepositoryImpl @Inject constructor(
    private val api: RecipeApi,
    private val dao: RecipeDao
) : RecipeRepository {
    override suspend fun searchRecipes(query: String): List<Recipe> {
        val response = api.searchRecipes(query)
        return response.meals?.map { it.toDomain() } ?: emptyList()
    }

    override suspend fun getRecipeById(id: String): Recipe {
        return dao.getRecipeById(id)?.toDomain() ?: api.getRecipeById(id).meals?.firstOrNull()?.toDomain()
            ?: throw Exception("Recipe not found")
    }

    override suspend fun insertFavorite(recipe: Recipe) {
        dao.insertRecipe(recipe.toEntity())
    }

    override suspend fun delete(recipe: Recipe) {
        dao.deleteRecipe(recipe.toEntity())

        if (recipe.imageUrl.isNotEmpty() && !recipe.imageUrl.startsWith("http")) {
            try {
                val file = File(recipe.imageUrl)
                if (file.exists()) {
                    file.delete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override suspend fun isRecipeFavorite(id: String): Boolean {
        return dao.isRecipeFavorite(id)
    }

    override fun getFavoriteRecipes(): Flow<List<Recipe>> {
        return dao.getFavoriteRecipes().map {
            entities -> entities.map {it.toDomain()}
        }
    }

    override suspend fun getFavoriteIds(): List<String> {
        return dao.getAllFavoriteIds()
    }

    override suspend fun fetchRandomRecipe(): Recipe {
        return api.getRandomRecipe().meals?.first()?.toDomain()
            ?: throw Exception("No random recipe found")
    }

    override fun searchLocalRecipes(query: String): Flow<List<Recipe>> {
        return dao.searchLocalRecipes(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun updateFavoriteStatus(id: String, isFavorite: Boolean) {
        dao.updateFavoriteStatus(id, isFavorite)
    }

    override fun searchFavoriteRecipes(query: String): Flow<List<Recipe>> {
        return dao.searchFavoriteRecipes(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }
}