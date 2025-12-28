package shuman.ulad.recipe.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RecipeDto(
    @SerializedName("idMeal")
    val id: String,

    @SerializedName("strMeal")
    val name: String,

    @SerializedName("strCategory")
    val category: String,

    @SerializedName("strInstructions")
    val instructions: String,

    @SerializedName("strMealThumb")
    val imageUrl: String
)

data class RecipeResponseDto(
    @SerializedName("meals")
    val meals: List<RecipeDto>?
)
