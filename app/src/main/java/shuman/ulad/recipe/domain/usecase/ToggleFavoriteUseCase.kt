package shuman.ulad.recipe.domain.usecase

import shuman.ulad.recipe.domain.model.Recipe
import shuman.ulad.recipe.domain.repository.RecipeRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(recipe: Recipe, isCurrentlyFavorite: Boolean): Boolean {
        val newStatus = !isCurrentlyFavorite

        if (recipe.isLocal) {
            repository.updateFavoriteStatus(recipe.id, newStatus)
            return newStatus
        } else {
            if (newStatus) {
                repository.insertFavorite(recipe.copy(isFavorite = true))
                return true
            } else {
                repository.delete(recipe)
                return false
            }
        }
    }
}