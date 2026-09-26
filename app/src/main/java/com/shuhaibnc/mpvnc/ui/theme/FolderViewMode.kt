package com.shuhaibnc.mpvnc.ui.theme

import androidx.annotation.StringRes
import com.shuhaibnc.mpvnc.R

enum class FolderViewMode(@StringRes val titleRes: Int) {
  List(R.string.pref_appearance_folder_view_list),
  Grid(R.string.pref_appearance_folder_view_grid),
}
