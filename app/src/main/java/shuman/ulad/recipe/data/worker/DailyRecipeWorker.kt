package shuman.ulad.recipe.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import shuman.ulad.recipe.domain.repository.RecipeRepository
import shuman.ulad.recipe.presentation.util.NotificationHelper

@HiltWorker
class DailyRecipeWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: RecipeRepository
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO){
        return@withContext try {
            val recipe = repository.fetchRandomRecipe()

            NotificationHelper.showNotification(
                applicationContext,
                "Recipe of the Day! 🍲",
                "Try to cook: ${recipe.name}",
                recipe.id
            )

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}