package shuman.ulad.recipe.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import shuman.ulad.recipe.data.local.entity.RecipeEntity

@Dao
interface RecipeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: RecipeEntity)

    @Delete
    suspend fun deleteRecipe(recipe: RecipeEntity)

    @Query("SELECT * FROM recipes WHERE isFavorite = 1")
    fun getFavoriteRecipes(): Flow<List<RecipeEntity>>

    @Query("SELECT id FROM recipes")
    suspend fun getAllFavoriteIds(): List<String>

    @Query("SELECT EXISTS(SELECT 1 FROM recipes WHERE id = :id)")
    suspend fun isRecipeFavorite(id: String): Boolean

    @Query("SELECT * FROM recipes WHERE id = :id")
    suspend fun getRecipeById(id: String): RecipeEntity?

    @Query("SELECT * FROM recipes WHERE isLocal = 1 AND name LIKE '%' || :query || '%'")
    fun searchLocalRecipes(query: String): Flow<List<RecipeEntity>>

    @Query("UPDATE recipes SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: String, isFavorite: Boolean)

    @Query("SELECT * FROM recipes")
    suspend fun getAllRecipesOneShot(): List<RecipeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(recipes: List<RecipeEntity>)

    @Query("SELECT * FROM recipes WHERE isFavorite = 1 AND name LIKE '%' || :query || '%'")
    fun searchFavoriteRecipes(query: String): Flow<List<RecipeEntity>>
}