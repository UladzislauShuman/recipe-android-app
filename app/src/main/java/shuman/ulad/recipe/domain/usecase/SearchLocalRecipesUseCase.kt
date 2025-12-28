package shuman.ulad.recipe.domain.usecase

import kotlinx.coroutines.flow.Flow
import shuman.ulad.recipe.domain.model.Recipe
import shuman.ulad.recipe.domain.repository.RecipeRepository
import javax.inject.Inject

class SearchLocalRecipesUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    operator fun invoke(query: String): Flow<List<Recipe>> {
        return repository.searchLocalRecipes(query)
    }
}