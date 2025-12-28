package shuman.ulad.recipe.presentation.create_recipe

data class CreateRecipeState(
    val title: String = "",
    val category: String = "",
    val instructions: String = "",
    val imageUri: String? = null,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false
)
