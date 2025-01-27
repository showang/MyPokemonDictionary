package me.showang.mypokemon.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PokemonInfo(
    val monsterId: Long,
    val name: String,
    val imageUrl: String,
) : Parcelable
