package com.shuhaibnc.mpvnc.ui.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.shuhaibnc.mpvnc.R
import com.shuhaibnc.mpvnc.presentation.Screen
import com.shuhaibnc.mpvnc.ui.player.controls.components.YOUTUBE_RED
import com.shuhaibnc.mpvnc.ui.preferences.PreferencesScreen
import com.shuhaibnc.mpvnc.ui.theme.spacing
import com.shuhaibnc.mpvnc.ui.utils.LocalBackStack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

private data class VideoFolder(
  val bucketId: Long,
  val name: String,
  val videoCount: Int,
)

@Serializable
object VideoFoldersScreen : Screen {
  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  override fun Content() {
    val context = LocalContext.current
    val backstack = LocalBackStack.current
    val mediaPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      Manifest.permission.READ_MEDIA_VIDEO
    } else {
      Manifest.permission.READ_EXTERNAL_STORAGE
    }
    var hasPermission by remember {
      mutableStateOf(
        ContextCompat.checkSelfPermission(context, mediaPermission) == PackageManager.PERMISSION_GRANTED,
      )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
      ActivityResultContracts.RequestPermission(),
    ) { granted -> hasPermission = granted }
    LaunchedEffect(Unit) {
      if (!hasPermission) permissionLauncher.launch(mediaPermission)
    }
    Scaffold(
      topBar = {
        TopAppBar(
          title = { Text(text = stringResource(id = R.string.app_name)) },
          navigationIcon = {
            Image(
              painter = painterResource(id = R.drawable.ic_launcher_foreground),
              contentDescription = "app_logo",
            )
          },
          actions = {
            IconButton(onClick = { backstack.add(OpenScreen) }) {
              Icon(
                Icons.Default.FolderOpen,
                contentDescription = stringResource(id = R.string.home_open),
                tint = YOUTUBE_RED,
              )
            }
            IconButton(onClick = { backstack.add(PreferencesScreen) }) {
              Icon(
                Icons.Default.Settings,
                contentDescription = stringResource(id = R.string.pref_preferences),
                tint = YOUTUBE_RED,
              )
            }
          },
        )
      },
    ) { padding ->
      var folders by remember { mutableStateOf<List<VideoFolder>>(emptyList()) }
      var isLoading by remember { mutableStateOf(true) }
      LaunchedEffect(hasPermission) {
        if (!hasPermission) {
          isLoading = false
          return@LaunchedEffect
        }
        isLoading = true
        folders = withContext(Dispatchers.IO) { loadVideoFolders(context) }
        isLoading = false
      }
      when {
        !hasPermission -> {
          Column(
            modifier = Modifier
              .fillMaxSize()
              .padding(padding)
              .padding(MaterialTheme.spacing.medium),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium, Alignment.CenterVertically),
          ) {
            Text(
              text = stringResource(id = R.string.home_media_permission_rationale),
              style = MaterialTheme.typography.bodyLarge,
            )
            Button(onClick = { permissionLauncher.launch(mediaPermission) }) {
              Text(text = stringResource(id = R.string.home_allow_media_access))
            }
          }
        }

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

        folders.isEmpty() -> {
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
            items(folders, key = { it.bucketId }) { folder ->
              Row(
                modifier = Modifier
                  .clickable { backstack.add(FolderVideosScreen(folder.bucketId, folder.name)) }
                  .fillMaxWidth()
                  .padding(
                    vertical = MaterialTheme.spacing.small,
                    horizontal = MaterialTheme.spacing.medium,
                  ),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Icon(
                  Icons.Default.Folder,
                  contentDescription = null,
                  tint = YOUTUBE_RED,
                  modifier = Modifier.size(40.dp),
                )
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = folder.name,
                    style = MaterialTheme.typography.bodyLarge,
                  )
                  Text(
                    text = pluralStringResource(
                      R.plurals.plural_videos,
                      folder.videoCount,
                      folder.videoCount,
                    ),
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

  private fun loadVideoFolders(context: Context): List<VideoFolder> {
    val counts = mutableMapOf<Long, Pair<String, Int>>()
    val projection = arrayOf(
      MediaStore.Video.Media.BUCKET_ID,
      MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
    )
    context.contentResolver.query(
      MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
      projection,
      null,
      null,
      "${MediaStore.Video.Media.BUCKET_DISPLAY_NAME} ASC",
    )?.use { cursor ->
      val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_ID)
      val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
      while (cursor.moveToNext()) {
        val id = cursor.getLong(idColumn)
        val name = cursor.getString(nameColumn) ?: "Unknown"
        val (existingName, count) = counts[id] ?: (name to 0)
        counts[id] = existingName to count + 1
      }
    }
    return counts.map { (id, nameAndCount) ->
      VideoFolder(bucketId = id, name = nameAndCount.first, videoCount = nameAndCount.second)
    }.sortedBy { it.name.lowercase() }
  }
}
