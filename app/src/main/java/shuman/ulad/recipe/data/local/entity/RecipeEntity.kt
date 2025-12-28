package shuman.ulad.recipe.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import shuman.ulad.recipe.domain.model.Recipe

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val name: String,
    val category: String,
    val instructions: String,
    val imageUrl: String,
    val isLocal: Boolean,
    val isFavorite: Boolean,
) {
    fun toDomain(): Recipe {
        return Recipe(
            id = id,
            name = name,
            category = category,
            instructions = instructions,
            imageUrl = imageUrl,
            isFavorite = isFavorite,
            isLocal = isLocal
        )
    }
}

fun Recipe.toEntity(): RecipeEntity {
    return RecipeEntity(
        id = id,
        name = name,
        category = category,
        instructions = instructions,
        imageUrl = imageUrl,
        isLocal = isLocal,
        isFavorite = isFavorite
    )
}
