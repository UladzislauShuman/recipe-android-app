package shuman.ulad.recipe.presentation.favorites.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import shuman.ulad.recipe.R
import shuman.ulad.recipe.presentation.favorites.FavoritesViewModel
import shuman.ulad.recipe.presentation.recipe_list.components.RecipeListItem

@Composable
fun FavoritesScreen (
    viewModel: FavoritesViewModel = hiltViewModel(),
    onNavigateToDetail: (String) -> Unit
) {
    val state = viewModel.state.value

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        OutlinedTextField(
            value = state.query,
            onValueChange = { viewModel.onQueryChange(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text(stringResource(R.string.search_hint_favorites)) },
            singleLine = true,
            trailingIcon = {
                Icon(Icons.Default.Search, contentDescription = null)
            }
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            if (state.recipes.isEmpty()) {
                val emptyTextResId =
                    if (state.query.isEmpty())
                        R.string.empty_no_favorites
                    else
                        R.string.empty_no_matches
                Text(
                    text = stringResource(emptyTextResId),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.recipes) {
                            recipe ->
                        RecipeListItem(
                            recipe = recipe,
                            onItemClick = { onNavigateToDetail(recipe.id) },
                            onFavoriteClick = {
                                viewModel.onFavoriteClick(recipe)
                            }
                        )
                    }
                }
            }
        }
    }
}