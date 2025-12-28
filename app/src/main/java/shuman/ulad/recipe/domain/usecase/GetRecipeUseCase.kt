package shuman.ulad.recipe.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import shuman.ulad.recipe.common.Resource
import shuman.ulad.recipe.domain.model.Recipe
import shuman.ulad.recipe.domain.repository.RecipeRepository
import java.io.IOException
import javax.inject.Inject

class GetRecipeUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    operator fun invoke(id: String): Flow<Resource<Recipe>> = flow {
        try {
            emit(Resource.Loading())
            val recipe = repository.getRecipeById(id)
            emit(Resource.Success(recipe))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error"))
        }
    }
}