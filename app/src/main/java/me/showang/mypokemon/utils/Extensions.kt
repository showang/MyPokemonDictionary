package me.showang.mypokemon.utils

fun String.uppercaseFirstChar(): String {
    return replaceRange(0, 1, get(0).uppercaseChar().toString())
}
