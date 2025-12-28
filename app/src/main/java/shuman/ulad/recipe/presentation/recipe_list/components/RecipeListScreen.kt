package shuman.ulad.recipe.presentation.recipe_list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import shuman.ulad.recipe.presentation.recipe_list.RecipeListViewModel

@Composable
fun RecipeListScreen (
    viewModel: RecipeListViewModel = hiltViewModel(),
    onNavigateToDetail: (String) -> Unit
) {
    val state by viewModel.state
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.error) {
        if (state.error.isNotBlank() && state.recipes.isNotEmpty()) {
            snackbarHostState.showSnackbar(message = state.error)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { padding ->

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = state.query,
                    onValueChange = {
                        viewModel.onQueryChange(it)
                    },
                    modifier = Modifier
                        .weight(1f),
                    placeholder = {
                        Text(text = "Search...")
                    },
                    trailingIcon = {
                        IconButton(onClick = { viewModel.searchRecipes() }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search"
                            )
                        }
                    },
                    singleLine = true
                )
            }

            Box(modifier = Modifier.fillMaxWidth()) {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(state.recipes) { recipe ->
                        RecipeListItem(
                            recipe = recipe,
                            onItemClick = {
                                onNavigateToDetail(recipe.id)
                            },
                            onFavoriteClick = {
                                clickedRecipe ->
                                viewModel.onToggleFavorite(clickedRecipe)
                            }
                        )
                    }
                }

                if (state.error.isNotBlank()) {
                    Text(
                        text = state.error,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .align(Alignment.Center)
                    )
                }

                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}