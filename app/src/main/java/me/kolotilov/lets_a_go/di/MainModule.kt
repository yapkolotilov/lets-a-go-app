package me.kolotilov.lets_a_go.di

import me.kolotilov.lets_a_go.App
import org.kodein.di.DI
import org.kodein.di.android.x.androidXModule

fun mainModule(app: App) = DI.Module("Main") {
    import(androidXModule(app))
    import(uiModule())
    import(presentationModule())
}

