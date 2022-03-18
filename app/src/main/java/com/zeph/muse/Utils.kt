package com.zeph.muse

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.example.muse.Songs
import com.zeph.muse.classes.SongsSerializer

object Utils {
    fun getSongsStore(context: Context): DataStore<Songs> {
        return context.songsStore
    }

    @ExperimentalAnimationApi
    @Composable
    fun EnterAnimation(content: @Composable () -> Unit) {
        AnimatedVisibility(
            visible = true,
            enter = slideInVertically(
                initialOffsetY = { -40 }
            ) + expandVertically(
                expandFrom = Alignment.Top
            ) + fadeIn(initialAlpha = 0.3f),
            exit = slideOutVertically() + shrinkVertically() + fadeOut(),
            content = content,
            initiallyVisible = false
        )
    }
}

private val Context.songsStore: DataStore<Songs> by dataStore(
    fileName = "songs.pb",
    serializer = SongsSerializer
)

var songsList: MutableList<Songs.Song> = mutableListOf()
