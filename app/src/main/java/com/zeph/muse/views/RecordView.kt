package com.zeph.muse.views

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import com.example.muse.Songs

@Composable
@ExperimentalMaterial3Api
fun RecordView(song: Songs.Song, navController: NavHostController) {
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(text = "Record", fontWeight = FontWeight.Bold)
                        Text(text = song.title, style = MaterialTheme.typography.titleLarge)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Go back to home"
                        )
                    }
                },
            )
        }
    ) {

    }
}