package shuman.ulad.recipe.presentation.recipe_detail.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import shuman.ulad.recipe.presentation.recipe_detail.RecipeDetailState
import shuman.ulad.recipe.presentation.recipe_detail.RecipeDetailViewModel

@Composable
fun RecipeDetailScreen(
    viewModel: RecipeDetailViewModel = hiltViewModel()
) {
    val state: RecipeDetailState = viewModel.state.value

    Scaffold(
        floatingActionButton = {
            if (state.recipe != null) {
                FloatingActionButton(
                    onClick = { viewModel.onFavoriteClick() }
                ) {
                    Icon(
                        imageVector = if (state.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (state.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    ) {
        paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            state.recipe?.let { recipe ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = paddingValues
                ) {
                    item {
                        AsyncImage(
                            model = recipe.imageUrl,
                            contentDescription = recipe.name,
                            modifier = Modifier
                                .fillMaxSize()
                                .height(250.dp),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(15.dp))
                    }

                    item {
                        Text(
                            text = recipe.name,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    item {
                        Text(
                            text = "Category: ${recipe.category}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(15.dp))
                    }

                    item {
                        Text(
                            text = recipe.instructions,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            if (state.error.isNotBlank()) {
                Text(
                    text = state.error,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                        .align(Alignment.Center)
                )
            }

            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }


}