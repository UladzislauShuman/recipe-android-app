package shuman.ulad.recipe.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import shuman.ulad.recipe.data.local.entity.RecipeEntity

@Database(
    entities = [RecipeEntity::class],
    version = 1
)
abstract class RecipeDatabase : RoomDatabase() {
    abstract fun recipeDao() : RecipeDao
}