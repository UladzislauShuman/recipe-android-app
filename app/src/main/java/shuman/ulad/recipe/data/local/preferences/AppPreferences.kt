package shuman.ulad.recipe.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("app_preferences")

enum class AppTheme { SYSTEM, LIGHT, DARK}

@Singleton
class AppPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val THEME_KEY = stringPreferencesKey("theme_mode")

    val theme: Flow<AppTheme> = context.dataStore.data.map { preferences ->
        val saved = preferences[THEME_KEY] ?: AppTheme.SYSTEM.name
        AppTheme.valueOf(saved)
    }

    suspend fun setTheme(theme: AppTheme) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme.name
        }
    }

    suspend fun getThemeSnapshot(): AppTheme {
        val preferences = context.dataStore.data.first()
        val saved = preferences[THEME_KEY] ?: AppTheme.SYSTEM.name
        return AppTheme.valueOf(saved)
    }
}