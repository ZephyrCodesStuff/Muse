package com.zeph.muse.views

import android.graphics.BitmapFactory
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.muse.Songs
import com.zeph.muse.Utils
import com.zeph.muse.songsList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.coroutines.EmptyCoroutineContext

@ExperimentalMaterial3Api
@Composable
fun SongView(song: Songs.Song, navController: NavController) {
    val context = LocalContext.current
    var completed by remember { mutableStateOf(song.completed) }
    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val scrollBehavior = remember(decayAnimationSpec) { TopAppBarDefaults.exitUntilCollapsedScrollBehavior(decayAnimationSpec) }
    var openDialog by remember { mutableStateOf(false) }
    var scale by remember { mutableStateOf(1f) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("record/${song.id}")
                },
                content = {
                    Icon(
                        Icons.Filled.Mic,
                        "Record progress"
                    )
                },
                shape = RoundedCornerShape(corner = CornerSize(16.dp)),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(16.dp)
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(text = song.title, fontWeight = FontWeight.Bold)
                        Text(text = song.author, style = MaterialTheme.typography.titleLarge)
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
                scrollBehavior = scrollBehavior,
                actions = {
                    IconButton(onClick = {
                        openDialog = true
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete"
                        )
                    }
                    IconButton(onClick = {
                        navController.navigate("editSong/${song.id}")
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit"
                        )
                    }
                    IconButton(onClick = {
                        CoroutineScope(EmptyCoroutineContext).launch {
                            completed = !completed
                            Utils.getSongsStore(context).updateData {
                                val editedSong = it.songsList.first { delete -> delete.id == song.id }
                                it.toBuilder().setSongs(
                                    it.songsList.indexOf(editedSong),
                                    editedSong.toBuilder().setCompleted(completed)
                                ).build()
                            }
                            songsList = Utils.getSongsStore(context).data.first().songsList
                        }
                    }) {
                        Icon(
                            if (!completed) Icons.Filled.Done else Icons.Filled.Clear,
                            "Mark as ${if (!completed) "completed" else "incomplete"}"
                        )
                    }
                },
            )
        }
    ) {
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
        ) {
            if (song.imagesCount > 0) {
                song.imagesList.forEach {
                    Image(
                        bitmap = BitmapFactory.decodeByteArray(it.toByteArray(), 0, it.size())
                            .asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .size(500.dp)
                    )
                }
            } else {
                Divider(Modifier.padding(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center)
                ) {
                    Text(
                        "This song doesn't have sheet music added!",
                        Modifier.padding(8.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            if (openDialog) {
                AlertDialog(
                    onDismissRequest = { openDialog = false },
                    title = { Text("Delete") },
                    text = { Text("Are you sure you want to delete this song?") },
                    confirmButton = {
                        TextButton(onClick = {
                            openDialog = false
                            navController.navigate("home") {
                                navController.popBackStack()
                            }
                            CoroutineScope(EmptyCoroutineContext).launch {
                                Utils.getSongsStore(context).updateData {
                                    val deletedSong =
                                        it.songsList.first { delete -> delete.id == song.id }
                                    it.toBuilder().removeSongs(it.songsList.indexOf(deletedSong))
                                        .build()
                                }
                                songsList = Utils.getSongsStore(context).data.first().songsList
                            }
                        }) { Text("Confirm") }
                    },
                    dismissButton = {
                        TextButton(onClick = { openDialog = false }) { Text("Dismiss") }
                    }
                )
            }
        }
    }
}
