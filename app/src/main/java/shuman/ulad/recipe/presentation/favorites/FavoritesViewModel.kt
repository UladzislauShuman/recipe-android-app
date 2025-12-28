package shuman.ulad.recipe.presentation.favorites

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.util.query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import shuman.ulad.recipe.domain.model.Recipe
import shuman.ulad.recipe.domain.usecase.GetFavoriteRecipesUseCase
import shuman.ulad.recipe.domain.usecase.ToggleFavoriteUseCase
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavoriteRecipesUseCase: GetFavoriteRecipesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _state = mutableStateOf(FavoritesState())
    val state: State<FavoritesState> = _state
    private var searchJob: Job? = null

    init {
        search("")
    }

    fun onQueryChange(newQuery: String) {
        _state.value = _state.value.copy(query = newQuery)

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300L)
            search(newQuery)
        }
    }
    fun onFavoriteClick(recipe: Recipe) {
        viewModelScope.launch {
            toggleFavoriteUseCase(recipe, recipe.isFavorite)
        }
    }

    private fun search(query: String) {
        getFavoriteRecipesUseCase(query).onEach { recipes ->
            _state.value = _state.value.copy(recipes = recipes)
        }.launchIn(viewModelScope)
    }
}