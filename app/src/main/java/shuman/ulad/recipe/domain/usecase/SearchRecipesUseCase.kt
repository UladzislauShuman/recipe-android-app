package shuman.ulad.recipe.domain.usecase

import coil.network.HttpException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import shuman.ulad.recipe.common.Resource
import shuman.ulad.recipe.domain.model.Recipe
import shuman.ulad.recipe.domain.repository.RecipeRepository
import java.io.IOException
import javax.inject.Inject

class SearchRecipesUseCase @Inject constructor(
    private val repository: RecipeRepository
) {

    operator fun invoke(query: String): Flow<Resource<List<Recipe>>> = flow {
        try {
            emit(Resource.Loading())
            val remoteRecipes = repository.searchRecipes(query)
            val favoriteRecipes = repository.getFavoriteIds()
            val mergeRecipes = remoteRecipes.map { recipe ->
                recipe.copy(isFavorite = recipe.id in favoriteRecipes)
            }
            emit(Resource.Success(mergeRecipes))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "\"An unexpected error occurred"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }
}