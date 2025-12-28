package shuman.ulad.recipe.presentation.recipe_detail

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import shuman.ulad.recipe.common.Resource
import shuman.ulad.recipe.domain.usecase.CheckFavoriteStatusUseCase
import shuman.ulad.recipe.domain.usecase.GetRecipeUseCase
import shuman.ulad.recipe.domain.usecase.ToggleFavoriteUseCase
import javax.inject.Inject

@HiltViewModel
class RecipeDetailViewModel @Inject constructor(
    private val getRecipeUseCase: GetRecipeUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val checkFavoriteStatusUseCase: CheckFavoriteStatusUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = mutableStateOf(RecipeDetailState())
    val state: State<RecipeDetailState> = _state

    init {
        savedStateHandle.get<String>("recipeId")?.let { recipeId ->
            getRecipe(recipeId)
            checkFavoriteStatus(recipeId)
        }
    }

    private fun getRecipe(recipeId: String) {
        getRecipeUseCase(recipeId).onEach { result ->
            when(result) {
                is Resource.Success -> {
                    _state.value = RecipeDetailState(recipe = result.data)
                }
                is Resource.Error -> {
                    _state.value = RecipeDetailState(
                        error = result.message ?: "An unexpected error occurred"
                    )
                }
                is Resource.Loading -> {
                    _state.value = RecipeDetailState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun checkFavoriteStatus(id: String) {
        viewModelScope.launch {
            val isFavorite = checkFavoriteStatusUseCase(id)
            _state.value = _state.value.copy(isFavorite = isFavorite)
        }
    }

    fun onFavoriteClick() {
        val currentRecipe = _state.value.recipe ?: return
        val isFavorite = _state.value.isFavorite

        viewModelScope.launch {
            val newStatus = toggleFavoriteUseCase(currentRecipe, isFavorite)
            _state.value = _state.value.copy(isFavorite = newStatus)
        }
    }
}