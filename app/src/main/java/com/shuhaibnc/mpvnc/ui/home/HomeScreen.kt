package com.shuhaibnc.mpvnc.ui.home

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.core.net.toUri
import com.shuhaibnc.mpvnc.presentation.Screen
import com.shuhaibnc.mpvnc.ui.player.PlayerActivity
import kotlinx.serialization.Serializable

@Serializable
object HomeScreen : Screen {
  @Composable
  override fun Content() {
    VideoFoldersScreen.Content()
  }

  fun playFile(
    filepath: String,
    context: Context,
  ) {
    val i = Intent(Intent.ACTION_VIEW, filepath.toUri())
    i.setClass(context, PlayerActivity::class.java)
    context.startActivity(i)
  }
}
