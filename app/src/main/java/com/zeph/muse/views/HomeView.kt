package com.zeph.muse.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.muse.Songs
import com.zeph.muse.songsList

@ExperimentalMaterial3Api
@Composable
fun HomeView(navController: NavController) {
    var searchText by remember { mutableStateOf("") }

    Scaffold(floatingActionButton = {
        ExtendedFloatingActionButton(
            text = { Text(text = "New song") },
            onClick = { navController.navigate("addSong") },
            icon = { Icon(Icons.Rounded.Add, "New song") },
            shape = RoundedCornerShape(corner = CornerSize(16.dp)),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(16.dp)
        )
    }) {
        Column(
            Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Column {
                Text("Hello there!", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold)
                Text(
                    if (songsList.size > 0) {
                        "You have ${songsList.size} song${if (songsList.size > 1) "s" else ""}${if (songsList.filter { it.completed }.size != songsList.size) ", ${songsList.filter { it.completed }.size} of which are completed" else ""}"
                    } else { "You don't have any songs in your library yet." },
                    style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(Modifier.padding(24.dp))
            if (songsList.size > 0) {
                OutlinedTextField(
                    value = searchText,
                    placeholder = { Text("Search...") },
                    onValueChange = { searchText = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                        textColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.Words
                    )
                )
                Spacer(Modifier.padding(top = 8.dp))
                Divider(Modifier.padding(16.dp))
                Songs(navController, searchText = searchText)
            } else {
                Divider(Modifier.padding(16.dp))
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center)
                ) {
                    Text(
                        "Add some songs to get started!",
                        Modifier
                            .padding(16.dp)
                            .clickable { navController.navigate("addSong") },
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun Songs(navController: NavController, searchText: String) {
    Column {
        songsList.forEach { song ->
            if (song.title.lowercase().startsWith(searchText.lowercase()) || song.author.lowercase()
                .startsWith(searchText.lowercase())
            ) {
                SongItem(song = song, navController = navController)
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun SongItem(song: Songs.Song, navController: NavController) {
    Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .clickable { navController.navigate("song/${song.id}") },
        shape = RoundedCornerShape(corner = CornerSize(16.dp)),
        containerColor = if (song.completed) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.secondaryContainer
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = song.author,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}
