package com.shuhaibnc.mpvnc.ui.home

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.provider.MediaStore
import android.util.LruCache
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.shuhaibnc.mpvnc.ui.theme.accentColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val thumbnailCache = object : LruCache<Long, Bitmap>(64) {
  override fun sizeOf(key: Long, value: Bitmap): Int = value.byteCount / 1024
}

private suspend fun loadVideoThumbnail(context: Context, videoId: Long): Bitmap? =
  withContext(Dispatchers.IO) {
    thumbnailCache.get(videoId)?.let { return@withContext it }
    val uri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoId)
    val retriever = MediaMetadataRetriever()
    try {
      retriever.setDataSource(context, uri)
      val frame =
        retriever.getFrameAtTime(1_000_000L, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
          ?: return@withContext null
      val targetWidth = 240
      val scale = targetWidth / frame.width.toFloat()
      val scaled = Bitmap.createScaledBitmap(
        frame,
        targetWidth,
        (frame.height * scale).toInt().coerceAtLeast(1),
        true,
      )
      if (scaled !== frame) frame.recycle()
      thumbnailCache.put(videoId, scaled)
      scaled
    } catch (_: Exception) {
      null
    } finally {
      runCatching { retriever.release() }
    }
  }

@Composable
fun VideoThumbnail(
  videoId: Long,
  modifier: Modifier = Modifier,
) {
  val context = LocalContext.current
  val bitmap by produceState<Bitmap?>(initialValue = null, videoId) {
    value = loadVideoThumbnail(context, videoId)
  }
  val shape = RoundedCornerShape(8.dp)
  Box(
    modifier = modifier
      .size(width = 112.dp, height = 64.dp)
      .clip(shape)
      .background(MaterialTheme.colorScheme.surfaceContainerHigh),
    contentAlignment = Alignment.Center,
  ) {
    if (bitmap != null) {
      Image(
        bitmap = bitmap!!.asImageBitmap(),
        contentDescription = null,
        modifier = Modifier.size(width = 112.dp, height = 64.dp),
        contentScale = ContentScale.Crop,
      )
    } else {
      Icon(
        Icons.Default.Movie,
        contentDescription = null,
        tint = accentColor(),
        modifier = Modifier.size(32.dp),
      )
    }
  }
}
