package shuman.ulad.recipe

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.recipekeeper.presentation.MainScreen
import dagger.hilt.android.AndroidEntryPoint
import shuman.ulad.recipe.data.local.preferences.AppPreferences
import shuman.ulad.recipe.data.local.preferences.AppTheme
import shuman.ulad.recipe.data.remote.api.RecipeApi
import shuman.ulad.recipe.data.worker.DailyRecipeWorker
import shuman.ulad.recipe.ui.theme.RecipeTheme
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject lateinit var api: RecipeApi
    @Inject lateinit var appPreferences: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupPeriodicWork()

        val recipeIdFromNotification = intent.getStringExtra("recipe_id_from_notification")
        if (recipeIdFromNotification != null) {
            intent.removeExtra("recipe_id_from_notification")
        }

        enableEdgeToEdge()
        setContent {
            val currentTheme by appPreferences.theme.collectAsState(initial = AppTheme.SYSTEM)
            RecipeTheme(appTheme = currentTheme) {
                MainScreen(
                    deepLinkRecipeId = recipeIdFromNotification
                )
            }
        }
    }

    private fun setupPeriodicWork() {
//        val testRequest = androidx.work.OneTimeWorkRequestBuilder<DailyRecipeWorker>()
//            .setInitialDelay(10, TimeUnit.SECONDS)
//            .setConstraints(
//                Constraints.Builder()
//                    .setRequiredNetworkType(NetworkType.CONNECTED)
//                    .build()
//            )
//            .build()
//
//        WorkManager.getInstance(this).enqueue(testRequest)

        val workRequest = PeriodicWorkRequestBuilder<DailyRecipeWorker>(
            24, TimeUnit.HOURS
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "DailyRecipeWork",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RecipeTheme {
        Greeting("Android")
    }
}