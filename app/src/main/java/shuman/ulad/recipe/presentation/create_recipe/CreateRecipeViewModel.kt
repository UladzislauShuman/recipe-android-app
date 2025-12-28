package shuman.ulad.recipe.presentation.create_recipe

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import shuman.ulad.recipe.domain.usecase.CreateRecipeUseCase
import javax.inject.Inject

@HiltViewModel
class CreateRecipeViewModel @Inject constructor(
    private val createRecipeUseCase: CreateRecipeUseCase
) : ViewModel() {
    var state = mutableStateOf(CreateRecipeState())
        private set

    fun onTitleChange(value: String) {
        state.value = state.value.copy(title = value)
    }

    fun onCategoryChange(value: String) {
        state.value = state.value.copy(category = value)
    }

    fun onInstructionsChange(value: String) {
        state.value = state.value.copy(instructions = value)
    }

    fun onImageSelected(uri: String?) {
        state.value = state.value.copy(imageUri = uri)
    }

    fun saveRecipe() {
        viewModelScope.launch {
            state.value = state.value.copy(isLoading = true)
            createRecipeUseCase(
                title = state.value.title,
                category = state.value.category,
                instructions = state.value.instructions,
                imageUri = state.value.imageUri
            )
            state.value = state.value.copy(isLoading = false, isSaved = true)
        }
    }

    fun onNavigated() {
        state.value = state.value.copy(isSaved = false)
    }
}