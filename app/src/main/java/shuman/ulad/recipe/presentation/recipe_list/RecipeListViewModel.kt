package shuman.ulad.recipe.presentation.recipe_list

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import shuman.ulad.recipe.common.Resource
import shuman.ulad.recipe.domain.model.Recipe
import shuman.ulad.recipe.domain.usecase.SearchRecipesUseCase
import shuman.ulad.recipe.domain.usecase.ToggleFavoriteUseCase
import javax.inject.Inject

@HiltViewModel
class RecipeListViewModel @Inject constructor(
    private val searchRecipesUseCase: SearchRecipesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {
    private val _state = mutableStateOf(RecipeListState())
    val state: State<RecipeListState> = _state

    init {
        searchRecipes()
    }

    fun onQueryChange(newQuery: String) {
        _state.value = _state.value.copy(query = newQuery)
    }
    fun searchRecipes() {
        val query = _state.value.query

        searchRecipesUseCase(query).onEach {
            result -> when(result) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        recipes = result.data ?: emptyList(),
                        isLoading = false,
                        error = ""
                    )
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        error = result.message ?: "An unexpected error occurred",
                        isLoading = false
                    )
                }
                is Resource.Loading -> {
                    _state.value = _state.value.copy(
                        isLoading = true,
                        error = ""
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    fun onToggleFavorite(recipe: Recipe) {
        viewModelScope.launch {
            val newStatus = toggleFavoriteUseCase(recipe, recipe.isFavorite)
            val updatedList = _state.value.recipes.map {
                if (it.id == recipe.id) {
                    it.copy(isFavorite =  newStatus)
                } else {
                    it
                }
            }
            _state.value = _state.value.copy(recipes = updatedList)
        }
    }
}