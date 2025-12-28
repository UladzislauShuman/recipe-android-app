package shuman.ulad.recipe.presentation.settings.components

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import shuman.ulad.recipe.R
import shuman.ulad.recipe.data.local.preferences.AppTheme
import shuman.ulad.recipe.presentation.settings.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateToBackup: () -> Unit
) {
    val currentTheme by viewModel.currentTheme.collectAsState()
    var showThemeDialog by remember { mutableStateOf(false) }
    var showLangDialog by remember { mutableStateOf(false) }

    val currentLocales = AppCompatDelegate.getApplicationLocales()
    val currentLanguageCode = currentLocales.get(0)?.language

    val languageSubtitle = when (currentLanguageCode) {
        "ru" -> "Русский"
        "be" -> "Беларуская"
        "en" -> "English"
        else -> stringResource(R.string.theme_system)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SettingsItem(
                title = stringResource(R.string.settings_language),
                subtitle = languageSubtitle,
                onClick = { showLangDialog = true }
            )

            SettingsItem(
                title = stringResource(R.string.settings_theme),
                subtitle = when(currentTheme) {
                    AppTheme.LIGHT -> stringResource(R.string.theme_light)
                    AppTheme.DARK -> stringResource(R.string.theme_dark)
                    AppTheme.SYSTEM -> stringResource(R.string.theme_system)
                },
                onClick = { showThemeDialog = true }
            )

            SettingsItem(
                title = stringResource(R.string.settings_backup),
                subtitle = stringResource(R.string.settings_backup_desc),
                onClick = onNavigateToBackup
            )
        }
    }

    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text(stringResource(R.string.settings_theme)) },
            text = {
                Column {
                    ThemeOption(stringResource(R.string.theme_system), AppTheme.SYSTEM, currentTheme, viewModel) { showThemeDialog = false }
                    ThemeOption(stringResource(R.string.theme_light), AppTheme.LIGHT, currentTheme, viewModel) { showThemeDialog = false }
                    ThemeOption(stringResource(R.string.theme_dark), AppTheme.DARK, currentTheme, viewModel) { showThemeDialog = false }
                }
            },
            confirmButton = {}
        )
    }

    if (showLangDialog) {
        AlertDialog(
            onDismissRequest = { showLangDialog = false },
            title = { Text(stringResource(R.string.settings_language)) },
            text = {
                Column {
                    LanguageOption("English", "en", viewModel) { showLangDialog = false }
                    LanguageOption("Русский", "ru", viewModel) { showLangDialog = false }
                    LanguageOption("Беларуская", "be", viewModel) { showLangDialog = false }
                }
            },
            confirmButton = {}
        )
    }
}


@Composable
fun SettingsItem(
    title: String,
    subtitle: String,
    icon: ImageVector? = null,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(subtitle) },
        leadingContent = icon?.let { { Icon(it, contentDescription = null) } },
        trailingContent = { Icon(Icons.Default.ArrowForward, contentDescription = null) },
        modifier = Modifier.clickable { onClick() }
    )
}

@Composable
fun ThemeOption(
    text: String,
    theme: AppTheme,
    current: AppTheme,
    vm: SettingsViewModel,
    onSelect: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable {
                vm.changeTheme(theme)
                onSelect()
            }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = (theme == current), onClick = null)
        Spacer(Modifier.width(8.dp))
        Text(text)
    }
}

@Composable
fun LanguageOption(
    text: String,
    code: String,
    vm: SettingsViewModel,
    onSelect: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable {
                vm.changeLanguage(code)
                onSelect()
            }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text, style = MaterialTheme.typography.bodyLarge)
    }
}