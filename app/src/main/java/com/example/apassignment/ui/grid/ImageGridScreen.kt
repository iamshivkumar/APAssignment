package com.example.apassignment.ui.grid

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.apassignment.data.cache.ImageLoader
import com.example.apassignment.ui.components.AsyncImage


@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun ImageGridScreen(
    viewModel: ImageGridViewModel,
    imageLoader: ImageLoader
) {
    val items by viewModel.items.collectAsState()
    val configuration = LocalConfiguration.current
    val columns = (configuration.screenWidthDp / 160).coerceAtLeast(2)

    val listState = rememberLazyGridState()

    val shouldLoadNext by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleItem >= items.size - 20
        }
    }

    LaunchedEffect(shouldLoadNext) {
        if (shouldLoadNext) viewModel.loadNextPage()
    }



    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        state = listState,
        contentPadding = PaddingValues(8.dp)
    ) {
        items(items.size) { index ->
            AsyncImage(
                url = items[index].imageUrl,
                imageLoader = imageLoader,
                modifier = Modifier
                    .aspectRatio(1f)
                    .padding(4.dp)
            )
        }
    }
}
