package shuman.ulad.recipe.presentation.local_recipes.components

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
import shuman.ulad.recipe.presentation.local_recipes.LocalRecipesViewModel
import shuman.ulad.recipe.presentation.recipe_list.components.RecipeListItem

@Composable
fun LocalRecipesScreen(
    viewModel: LocalRecipesViewModel = hiltViewModel(),
    onNavigateToDetail: (String) -> Unit,
    onNavigateToCreate: () -> Unit
) {
    val state = viewModel.state.value

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreate) {
                Icon(Icons.Default.Add, contentDescription = "Create Recipe")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            OutlinedTextField(
                value = state.query,
                onValueChange = { viewModel.onQueryChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text(stringResource(R.string.search_hint_my_recipes)) },
                singleLine = true,
                trailingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                }
            )

            if (state.recipes.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.empty_no_recipes_found))
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.recipes) {
                            recipe ->
                        RecipeListItem(
                            recipe = recipe,
                            onItemClick = { onNavigateToDetail(recipe.id) },
                            onFavoriteClick = { viewModel.onFavoriteClick(recipe) },
                            onDeleteClick = { viewModel.onDeleteClick(recipe) }
                        )
                    }
                }
            }
        }
    }
}