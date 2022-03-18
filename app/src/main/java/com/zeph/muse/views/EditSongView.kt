package com.zeph.muse.views

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.muse.Songs
import com.google.protobuf.ByteString
import com.zeph.muse.Utils
import com.zeph.muse.songsList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import kotlin.coroutines.EmptyCoroutineContext

@ExperimentalMaterial3Api
@Composable
fun EditSongView(navController: NavController, song: Songs.Song) {
    var name by remember { mutableStateOf(song.title) }
    var artist by remember { mutableStateOf(song.author) }

    // PDF picker
    val context = LocalContext.current
    val bitmaps by remember { mutableStateOf<MutableList<ByteString>>(mutableListOf()) }
    var selected by remember { mutableStateOf(false) }

    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { result ->
            val item = result?.let { context.contentResolver.openInputStream(it) }
            val bytes = item?.readBytes()
            val file = File.createTempFile("temp", null)
            if (bytes != null) {
                file.writeBytes(bytes)
            }

            CoroutineScope(EmptyCoroutineContext).launch {
                try {
                    val renderer = PdfRenderer(
                        ParcelFileDescriptor.open(
                            file,
                            ParcelFileDescriptor.MODE_READ_ONLY
                        )
                    )
                    selected = true

                    for (i in 0 until renderer.pageCount) {
                        val page: PdfRenderer.Page = renderer.openPage(i)

                        val bitmap =
                            Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                        // say we render for showing on the screen
                        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

                        // do stuff with the bitmap
                        val stream = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                        val image = ByteString.copyFrom(stream.toByteArray())
                        bitmaps.add(image)

                        // close the page
                        page.close()
                    }
                    file.delete()
                } catch (e: IOException) {
                    return@launch
                }
            }
            item?.close()
        }
    Scaffold(topBar = {
        SmallTopAppBar(
            title = { Text(text = "Edit song", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onBackground) },
            navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Go back to song"
                    )
                }
            }
        )
    }) {
        Column(Modifier.padding(24.dp)) {
            Spacer(Modifier.size(16.dp))
            Column {
                Text("Song info", color = MaterialTheme.colorScheme.onBackground)
                TextField(
                    value = name,
                    placeholder = { Text("Name") },
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.titleLarge,
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = MaterialTheme.colorScheme.background,
                        textColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.secondary,
                        placeholderColor = MaterialTheme.colorScheme.secondary
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.Words
                    )
                )
                TextField(
                    value = artist,
                    placeholder = { Text("Artist") },
                    onValueChange = { artist = it },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.titleLarge,
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = MaterialTheme.colorScheme.background,
                        textColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.secondary,
                        placeholderColor = MaterialTheme.colorScheme.secondary
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.Words
                    )
                )

                Button(
                    onClick = { launcher.launch("application/pdf") },
                    Modifier
                        .padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = if (!selected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onTertiaryContainer
                    )
                ) {
                    Text(if (song.imagesCount == 0) "Select a sheet file" else "Change sheet file", color = if (!selected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onTertiaryContainer)
                }

                Button(
                    onClick = {
                        if (name != "" && artist != "") {
                            CoroutineScope(EmptyCoroutineContext).launch {
                                Utils.getSongsStore(context).updateData {
                                    val editedSong =
                                        it.songsList.first { edit -> edit.id == song.id }
                                    val edit = editedSong.toBuilder()
                                        .setTitle(name)
                                        .setAuthor(artist)
                                    if (selected) {
                                        edit.clearImages().addAllImages(bitmaps)
                                    }

                                    it.toBuilder().setSongs(it.songsList.indexOf(editedSong), edit)
                                        .build()
                                }
                                songsList = Utils.getSongsStore(context).data.first().songsList
                            }
                            navController.navigate("song/${song.id}") { navController.navigateUp(); navController.popBackStack() }
                        }
                    },
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Text("Confirm changes", color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
        }
    }
}
