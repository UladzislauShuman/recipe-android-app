package shuman.ulad.recipe.data.remote.api

import retrofit2.http.GET
import retrofit2.http.Query
import shuman.ulad.recipe.data.remote.dto.RecipeResponseDto

interface RecipeApi {

    @GET("search.php")
    suspend fun searchRecipes(
        @Query("s") query: String
    ): RecipeResponseDto

    @GET("random.php")
    suspend fun getRandomRecipe(): RecipeResponseDto

    @GET("lookup.php")
    suspend fun getRecipeById(
        @Query("i") id: String
    ): RecipeResponseDto
}