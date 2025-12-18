package com.example.apassignment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.apassignment.data.cache.DiskImageCache
import com.example.apassignment.data.cache.ImageLoader
import com.example.apassignment.data.cache.MemoryImageCache
import com.example.apassignment.data.remote.MediaApi
import com.example.apassignment.data.repository.MediaRepository
import com.example.apassignment.ui.grid.ImageGridScreen
import com.example.apassignment.ui.grid.ImageGridViewModel
import com.example.apassignment.ui.theme.ApAssignmentTheme

import androidx.lifecycle.viewmodel.compose.viewModel


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        // Manual dependency wiring (no Hilt required)
        val api = MediaApi()
        val repository = MediaRepository(api)

        val memoryCache = MemoryImageCache()
        val diskCache = DiskImageCache(this)
        val imageLoader = ImageLoader(memoryCache, diskCache)

        setContent {
            ApAssignmentTheme {
                val viewModel: ImageGridViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return ImageGridViewModel(repository) as T
                        }
                    }
                )


                Scaffold {
                    paddingValues ->
                    Surface(modifier = Modifier.padding(paddingValues)) {
                        ImageGridScreen(
                            viewModel = viewModel,
                            imageLoader = imageLoader
                        )
                    }
                }
            }
        }
    }
}



//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    ApAssignmentTheme {
//        Greeting("Android")
//    }
//}