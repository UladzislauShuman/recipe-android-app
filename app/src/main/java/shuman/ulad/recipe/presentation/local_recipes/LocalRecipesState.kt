package shuman.ulad.recipe.presentation.local_recipes

import shuman.ulad.recipe.domain.model.Recipe

data class LocalRecipesState(
    val query: String = "",
    val recipes: List<Recipe> = emptyList()
)
