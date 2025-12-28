package shuman.ulad.recipe.presentation.recipe_detail

import shuman.ulad.recipe.domain.model.Recipe

data class RecipeDetailState(
    val isLoading: Boolean = false,
    val recipe: Recipe? = null,
    val error: String = "",
    val isFavorite: Boolean = false,
)