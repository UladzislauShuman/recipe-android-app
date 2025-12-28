package shuman.ulad.recipe.presentation.local_recipes

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import shuman.ulad.recipe.domain.model.Recipe
import shuman.ulad.recipe.domain.repository.RecipeRepository
import shuman.ulad.recipe.domain.usecase.SearchLocalRecipesUseCase
import shuman.ulad.recipe.domain.usecase.ToggleFavoriteUseCase
import javax.inject.Inject

@HiltViewModel
class LocalRecipesViewModel @Inject constructor(
    private val searchLocalRecipesUseCase: SearchLocalRecipesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val repository: RecipeRepository
) : ViewModel() {
    var state = mutableStateOf(LocalRecipesState())
        private set
    private var searchJob: Job? = null

    init {
        search("")
    }

    fun onQueryChange(newQuery: String) {
        state.value = state.value.copy(query = newQuery)
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500L)
            search(newQuery)
        }
    }

    private fun search(query: String) {
        searchLocalRecipesUseCase(query).onEach { recipes ->
            state.value = state.value.copy(recipes = recipes)
        }.launchIn(viewModelScope)
    }

    fun onFavoriteClick(recipe: Recipe) {
        viewModelScope.launch {
            toggleFavoriteUseCase(recipe, recipe.isFavorite)
        }
    }

    fun onDeleteClick(recipe: Recipe) {
        viewModelScope.launch {
            repository.delete(recipe)
        }
    }
}