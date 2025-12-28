package shuman.ulad.recipe.presentation.favorites

import shuman.ulad.recipe.domain.model.Recipe

data class FavoritesState(
    val query: String = "",
    val recipes: List<Recipe>  = emptyList()
)