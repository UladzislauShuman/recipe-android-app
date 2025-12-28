package shuman.ulad.recipe.presentation.recipe_list

import shuman.ulad.recipe.domain.model.Recipe

data class RecipeListState(
    val isLoading: Boolean = false,
    val recipes: List<Recipe> = emptyList(),
    val error: String = "",
    val query: String = "chicken"
)