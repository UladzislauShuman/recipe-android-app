package shuman.ulad.recipe.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import shuman.ulad.recipe.common.Constants
import shuman.ulad.recipe.data.local.RecipeDao
import shuman.ulad.recipe.data.local.RecipeDatabase
import shuman.ulad.recipe.data.remote.api.RecipeApi
import shuman.ulad.recipe.data.repository.RecipeRepositoryImpl
import shuman.ulad.recipe.domain.repository.RecipeRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideRecipeApi(retrofit: Retrofit) : RecipeApi {
        return retrofit.create(RecipeApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRecipeRepository(api: RecipeApi, dao: RecipeDao): RecipeRepository {
        return RecipeRepositoryImpl(api, dao)
    }

    @Provides
    @Singleton
    fun provideRecipeDatabase(app: Application): RecipeDatabase {
        return Room.databaseBuilder(
            app,
            RecipeDatabase::class.java,
            "recipe_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideRecipeDao(db: RecipeDatabase): RecipeDao {
        return db.recipeDao()
    }
}