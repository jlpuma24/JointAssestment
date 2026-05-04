package com.joist.assestment.di

import com.joist.assestment.data.ValidationRepository
import com.joist.assestment.data.ValidationRepositoryImpl
import com.joist.assestment.ui.EchoViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<ValidationRepository> { ValidationRepositoryImpl() }
    viewModel { EchoViewModel(get()) }
}
