package shuman.ulad.recipe.domain.usecase

import android.net.Uri
import shuman.ulad.recipe.data.local.files.ImageStorageManager
import shuman.ulad.recipe.domain.model.Recipe
import shuman.ulad.recipe.domain.repository.RecipeRepository
import java.util.UUID
import javax.inject.Inject

class CreateRecipeUseCase @Inject constructor(
    private val repository: RecipeRepository,
    private val imageStorageManager: ImageStorageManager
) {
    suspend operator fun invoke(
        title: String,
        category: String,
        instructions: String,
        imageUri: String?
    ) {
        val savedImagePath = if (imageUri != null) {
            imageStorageManager.saveImage(Uri.parse(imageUri))
        } else {
            ""
        }

        val newRecipe = Recipe(
            id = UUID.randomUUID().toString(),
            name = title,
            category = category,
            instructions = instructions,
            imageUrl = savedImagePath,
            isLocal = true
        )

        repository.insertFavorite(newRecipe)
    }
}