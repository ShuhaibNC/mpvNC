package com.shuhaibnc.mpvnc.ui.home

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shuhaibnc.mpvnc.R
import com.shuhaibnc.mpvnc.presentation.Screen
import com.shuhaibnc.mpvnc.ui.player.controls.components.YOUTUBE_RED
import com.shuhaibnc.mpvnc.ui.theme.spacing
import com.shuhaibnc.mpvnc.ui.utils.LocalBackStack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.util.Locale

private data class VideoItem(
  val id: Long,
  val displayName: String,
  val durationMs: Long,
)

@Serializable
data class FolderVideosScreen(
  val bucketId: Long,
  val folderName: String,
) : Screen {

  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  override fun Content() {
    val context = LocalContext.current
    val backstack = LocalBackStack.current
    Scaffold(
      topBar = {
        TopAppBar(
          title = { Text(text = folderName) },
          navigationIcon = {
            IconButton(onClick = { backstack.removeLastOrNull() }) {
              Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)
            }
          },
        )
      },
    ) { padding ->
      var videos by remember { mutableStateOf<List<VideoItem>>(emptyList()) }
      var isLoading by remember { mutableStateOf(true) }
      LaunchedEffect(bucketId) {
        isLoading = true
        videos = withContext(Dispatchers.IO) { loadVideos(context, bucketId) }
        isLoading = false
      }
      when {
        isLoading -> {
          Column(
            modifier = Modifier
              .fillMaxSize()
              .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
          ) {
            CircularProgressIndicator(color = YOUTUBE_RED)
          }
        }

        videos.isEmpty() -> {
          Column(
            modifier = Modifier
              .fillMaxSize()
              .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
          ) {
            Text(text = stringResource(id = R.string.home_no_videos))
          }
        }

        else -> {
          LazyColumn(
            modifier = Modifier
              .fillMaxSize()
              .padding(padding),
          ) {
            items(videos, key = { it.id }) { video ->
              Row(
                modifier = Modifier
                  .clickable {
                    val uri = ContentUris.withAppendedId(
                      MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                      video.id,
                    )
                    HomeScreen.playFile(uri.toString(), context)
                  }
                  .fillMaxWidth()
                  .padding(
                    vertical = MaterialTheme.spacing.small,
                    horizontal = MaterialTheme.spacing.medium,
                  ),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Icon(
                  Icons.Default.Movie,
                  contentDescription = null,
                  tint = YOUTUBE_RED,
                  modifier = Modifier.size(40.dp),
                )
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = video.displayName,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                  )
                  Text(
                    text = formatDuration(video.durationMs),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                  )
                }
              }
            }
          }
        }
      }
    }
  }

  private fun loadVideos(context: Context, bucketId: Long): List<VideoItem> {
    val videos = mutableListOf<VideoItem>()
    val projection = arrayOf(
      MediaStore.Video.Media._ID,
      MediaStore.Video.Media.DISPLAY_NAME,
      MediaStore.Video.Media.DURATION,
    )
    val selection = "${MediaStore.Video.Media.BUCKET_ID} = ?"
    val selectionArgs = arrayOf(bucketId.toString())
    context.contentResolver.query(
      MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
      projection,
      selection,
      selectionArgs,
      "${MediaStore.Video.Media.DATE_MODIFIED} DESC",
    )?.use { cursor ->
      val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
      val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
      val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
      while (cursor.moveToNext()) {
        videos += VideoItem(
          id = cursor.getLong(idColumn),
          displayName = cursor.getString(nameColumn) ?: "Unknown",
          durationMs = cursor.getLong(durationColumn),
        )
      }
    }
    return videos
  }

  private fun formatDuration(durationMs: Long): String {
    val totalSeconds = durationMs / 1000
    val hours = totalSeconds / 3600
    val minutes = totalSeconds % 3600 / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) {
      String.format(Locale.US, "%d:%02d:%02d", hours, minutes, seconds)
    } else {
      String.format(Locale.US, "%d:%02d", minutes, seconds)
    }
  }
}
