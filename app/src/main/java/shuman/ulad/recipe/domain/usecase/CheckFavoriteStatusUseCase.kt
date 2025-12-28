package shuman.ulad.recipe.domain.usecase

import shuman.ulad.recipe.domain.repository.RecipeRepository
import javax.inject.Inject

class CheckFavoriteStatusUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(id: String): Boolean {
        return repository.isRecipeFavorite(id)
    }
}