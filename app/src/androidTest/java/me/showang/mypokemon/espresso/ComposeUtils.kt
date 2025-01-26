package me.showang.mypokemon.espresso

import androidx.activity.ComponentActivity

fun <A : ComponentActivity> getActivityFromTestRule(rule: CustomActivityScenarioRule<A>): A {
    var activity: A? = null
    rule.getScenario().onActivity { activity = it }
    return activity ?: error("Activity was not set in the ActivityScenarioRule!")
}
