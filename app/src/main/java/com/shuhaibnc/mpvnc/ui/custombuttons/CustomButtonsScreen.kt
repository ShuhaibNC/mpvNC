package com.shuhaibnc.mpvnc.ui.custombuttons

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalUriHandler
import kotlinx.collections.immutable.toImmutableList
import com.shuhaibnc.mpvnc.database.entities.CustomButtonEntity
import com.shuhaibnc.mpvnc.preferences.PlayerPreferences
import com.shuhaibnc.mpvnc.preferences.preference.collectAsState
import com.shuhaibnc.mpvnc.presentation.Screen
import com.shuhaibnc.mpvnc.presentation.custombuttons.CustomButtonsScreen
import com.shuhaibnc.mpvnc.presentation.custombuttons.components.CustomButtonAddDialog
import com.shuhaibnc.mpvnc.presentation.custombuttons.components.CustomButtonDeleteDialog
import com.shuhaibnc.mpvnc.presentation.custombuttons.components.CustomButtonEditDialog
import com.shuhaibnc.mpvnc.ui.utils.LocalBackStack
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

object CustomButtonsScreen : Screen {
  @Composable
  override fun Content() {
    val backstack = LocalBackStack.current
    val uriHandler = LocalUriHandler.current
    val viewModel = koinViewModel<CustomButtonsScreenViewModel>()
    val playerPreferences = koinInject<PlayerPreferences>()

    val primaryButtonId by playerPreferences.primaryCustomButtonId.collectAsState()
    val customButtons by viewModel.customButtons.collectAsState()
    val dialog by viewModel.dialog.collectAsState()

    CustomButtonsScreen(
      buttons = customButtons,
      primaryId = primaryButtonId,
      onClickAdd = { viewModel.showDialog(CustomButtonDialog.Create) },
      onClickRename = { viewModel.showDialog(CustomButtonDialog.Edit(it)) },
      onClickDelete = { viewModel.showDialog(CustomButtonDialog.Delete(it)) },
      onTogglePrimary = viewModel::togglePrimary,
      onClickMoveUp = viewModel::moveUp,
      onClickMoveDown = viewModel::moveDown,
      onClickFaq = { uriHandler.openUri(CUSTOM_BUTTONS_DOC_URL) },
      onNavigateBack = backstack::removeLastOrNull,
    )

    when (dialog) {
      is CustomButtonDialog.None -> {}
      is CustomButtonDialog.Create -> {
        CustomButtonAddDialog(
          onDismissRequest = viewModel::dismissDialog,
          onAdd = viewModel::addCustomButton,
          buttonNames = customButtons.map { it.title }.toImmutableList(),
        )
      }
      is CustomButtonDialog.Edit -> {
        val button = (dialog as CustomButtonDialog.Edit).customButton
        CustomButtonEditDialog(
          onDismissRequest = viewModel::dismissDialog,
          onEdit = { title, content, longPressContent ->
            viewModel.editButton(
              button.copy(title = title, content = content, longPressContent = longPressContent)
            )
          },
          buttonNames = customButtons.filter { it.title != button.title }
            .map { it.title }
            .toImmutableList(),
          initialState = button,
        )
      }
      is CustomButtonDialog.Delete -> {
        val button = (dialog as CustomButtonDialog.Delete).customButton
        CustomButtonDeleteDialog(
          onDismissRequest = viewModel::dismissDialog,
          onDelete = { viewModel.removeButton(button) },
          title = button.title,
        )
      }
    }
  }
}

sealed interface CustomButtonDialog {
  data object None : CustomButtonDialog
  data object Create : CustomButtonDialog
  data class Edit(val customButton: CustomButtonEntity) : CustomButtonDialog
  data class Delete(val customButton: CustomButtonEntity) : CustomButtonDialog
}

private const val CUSTOM_BUTTONS_DOC_URL = "https://github.com/ShuhaibNC/mpvNC/blob/main/docs/CUSTOMBUTTONS.rst"
