package me.showang.mypokemon.model

data class PocketMonDetails(
    val info: PocketMonInfo,
    val types: List<PocketMonType>,
    val evolutionFrom: PocketMonInfo?,
    val descriptions: String,
)
