package com.zeph.muse

import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.muse.Songs
import com.zeph.muse.Utils.EnterAnimation
import com.zeph.muse.Utils.getSongsStore
import com.zeph.muse.ui.theme.MuseTheme
import com.zeph.muse.views.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.coroutines.EmptyCoroutineContext

@ExperimentalAnimationApi
@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val songsFlow: Flow<Songs> = getSongsStore(this).data
        CoroutineScope(EmptyCoroutineContext).launch {
            songsList = songsFlow.first().songsList
        }

        setContent {
            val navController = rememberNavController()

            MuseTheme {

                // Hiding both the StatusBar (top) and the NavigationBar (bottom)
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    window.insetsController?.hide(WindowInsets.Type.navigationBars())
                }

                Surface(color = colorScheme.background, modifier = Modifier.fillMaxSize()) {
                    NavigationView(navController = navController)
                }
            }
        }
    }
}

@ExperimentalAnimationApi
@ExperimentalMaterial3Api
@Composable
fun NavigationView(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeView(navController = navController)
        }
        composable("song/{songId}", arguments = listOf(navArgument("songId") { type = NavType.StringType })) {
            EnterAnimation {
                val song = songsList.firstOrNull { item -> item.id == it.arguments?.getString("songId") } ?: Songs.Song.getDefaultInstance()
                SongView(song = song, navController = navController)
            }
        }
        composable("editSong/{songId}", arguments = listOf(navArgument("songId") { type = NavType.StringType })) {
            EnterAnimation {
                val song = songsList.first { item -> item.id == it.arguments?.getString("songId") }
                EditSongView(song = song, navController = navController)
            }
        }
        composable("addSong") {
            EnterAnimation {
                AddSongView(navController = navController)
            }
        }
        composable("record/{songId}", arguments = listOf(navArgument("songId") { type = NavType.StringType })) {
            EnterAnimation {
                val song = songsList.first { item -> item.id == it.arguments?.getString("songId") }
                RecordView(song = song, navController = navController)
            }
        }
    }
}
