package shuman.ulad.recipe.presentation.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import shuman.ulad.recipe.data.local.preferences.AppPreferences
import shuman.ulad.recipe.data.local.preferences.AppTheme
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appPreferences: AppPreferences
) : ViewModel() {

    val currentTheme = appPreferences.theme.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        AppTheme.SYSTEM
    )

    fun changeTheme(theme: AppTheme) {
        viewModelScope.launch {
            appPreferences.setTheme(theme)
        }
    }

    fun changeLanguage(languageCode: String) {
        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }
}