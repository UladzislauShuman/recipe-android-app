package shuman.ulad.recipe.data.remote.dto

import shuman.ulad.recipe.domain.model.Recipe

fun RecipeDto.toDomain(): Recipe {
    return Recipe(
        id = id,
        name = name,
        category = category,
        instructions = instructions,
        imageUrl = imageUrl
    )
}