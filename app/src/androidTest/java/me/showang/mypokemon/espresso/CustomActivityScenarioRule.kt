package me.showang.mypokemon.espresso

import android.app.Activity
import androidx.test.core.app.ActivityScenario
import androidx.test.internal.util.Checks
import org.junit.rules.ExternalResource

class CustomActivityScenarioRule<A : Activity?>
/**
 * Constructs ActivityScenarioRule for a given activity class.
 *
 * @param activityClass an activity class to launch
 */
(activityClass: Class<A>, initDelegate: () -> Unit) : ExternalResource() {
    private val scenarioSupplier: () -> ActivityScenario<A> = {
        initDelegate()
        ActivityScenario.launch(Checks.checkNotNull(activityClass))
    }
    private var mScenario: ActivityScenario<A>? = null

    @Throws(Throwable::class)
    override fun before() {
        mScenario = scenarioSupplier()
    }

    override fun after() {
        mScenario!!.close()
    }

    /**
     * Returns [ActivityScenario] of the given activity class.
     *
     * @throws NullPointerException if you call this method while test is not running
     * @return a non-null [ActivityScenario] instance
     */
    fun getScenario(): ActivityScenario<A> {
        return Checks.checkNotNull(mScenario)!!
    }
}
