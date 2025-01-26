package me.showang.mypokemon

import me.showang.transtate.async.AsyncDelegate
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.bind
import org.koin.dsl.module

class ViewModelTestRule : TestWatcher() {

    override fun starting(description: Description) {
        startKoin {
            loadKoinModules(
                module {
                    single { AsyncUnitTest() } bind AsyncDelegate::class
                }
            )
        }
    }

    override fun finished(description: Description) {
        stopKoin()
    }
}
