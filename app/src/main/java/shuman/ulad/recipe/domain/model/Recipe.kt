package shuman.ulad.recipe.domain.model

data class Recipe(
    val id: String,
    val name: String,
    val category: String,
    val instructions: String,
    val imageUrl: String,
    val isFavorite: Boolean = false,
    val isLocal: Boolean = false
)
