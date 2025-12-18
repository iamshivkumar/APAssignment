package com.example.apassignment.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.apassignment.data.cache.ImageLoader
import kotlinx.coroutines.launch

sealed interface ImageUiState {
    object Loading : ImageUiState
    data class Success(val bitmap: Bitmap) : ImageUiState
    data class Error(val message: String) : ImageUiState
}
@Composable
fun AsyncImage(
    url: String,
    imageLoader: ImageLoader,
    modifier: Modifier = Modifier
) {
    var state by remember { mutableStateOf<ImageUiState>(ImageUiState.Loading) }
    val scope = rememberCoroutineScope()

    DisposableEffect(url) {
        val job = scope.launch {
            state = try {
                val bitmap = imageLoader.load(url, reqWidth = 240, reqHeight = 240)
                ImageUiState.Success(bitmap)
            } catch (e: Exception) {
                ImageUiState.Error(e.message ?: "Image load failed")
            }
        }

        onDispose { job.cancel() }
    }

    when (val s = state) {
        is ImageUiState.Loading -> {
            Box(
                modifier = modifier
                    .size(160.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            )
        }

        is ImageUiState.Success -> {
            Image(
                bitmap = s.bitmap.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = modifier
            )
        }

        is ImageUiState.Error -> {
            Box(
                modifier = modifier
                    .size(160.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                androidx.compose.material3.Text(
                    text = s.message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
