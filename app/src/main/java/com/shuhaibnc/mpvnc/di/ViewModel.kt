package com.shuhaibnc.mpvnc.di

import com.shuhaibnc.mpvnc.ui.custombuttons.CustomButtonsScreenViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val ViewModelModule = module {
  viewModelOf(::CustomButtonsScreenViewModel)
}
