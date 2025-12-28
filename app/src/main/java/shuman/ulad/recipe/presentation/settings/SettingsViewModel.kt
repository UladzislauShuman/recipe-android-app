package shuman.ulad.recipe.presentation.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import shuman.ulad.recipe.data.local.files.BackupManager
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val backupManager: BackupManager
) : ViewModel() {
    private val _uiEvent = Channel<String>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun exportData(uri: Uri) {
        viewModelScope.launch {
            try {
                backupManager.exportBackup(uri)
                _uiEvent.send("Backup saved successfully!")
            } catch (e: Exception) {
                e.printStackTrace()
                _uiEvent.send("Error exporting data: ${e.message}")
            }
        }
    }

    fun importData(uri: Uri) {
        viewModelScope.launch {
            try {
                backupManager.importBackup(uri)
                _uiEvent.send("Data restored successfully!")
            } catch (e: Exception) {
                e.printStackTrace()
                _uiEvent.send("Error importing data: ${e.message}")
            }
        }
    }
}